package com.github.yangwk.jobscheduler.core.impl;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.github.yangwk.jobscheduler.core.Job;
import com.github.yangwk.jobscheduler.core.JobDetail;
import com.github.yangwk.jobscheduler.core.JobTime;
import com.github.yangwk.jobscheduler.core.Scheduler;
import com.github.yangwk.jobscheduler.core.impl.JobExecutor.Key;
import com.github.yangwk.jobscheduler.core.impl.registry.Notifier;
import com.github.yangwk.jobscheduler.core.impl.registry.command.JobDeletedCommand;
import com.github.yangwk.jobscheduler.core.impl.registry.command.JobShortTimedCommand;
import com.github.yangwk.jobscheduler.core.impl.registry.command.JobUpdatedCommand;
import com.github.yangwk.jobscheduler.core.impl.registry.command.JobUpdatedShortTimedCommand;
import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;
import com.github.yangwk.jobscheduler.core.jdbc.service.JobTaskService;
import com.github.yangwk.jobscheduler.core.util.JobUtils;

public final class JobScheduler implements Scheduler {

    private final JobExecutor jobExecutor;
    private final JobTaskService jobTaskService;
    private final Notifier notifier;
    
    
    private static volatile JobScheduler singletonInstance;
    
    
    JobScheduler(JobExecutor jobExecutor, JobTaskService jobTaskService, Notifier notifier) {
        this.jobExecutor = jobExecutor;
        this.jobTaskService = jobTaskService;
        this.notifier = notifier;
    }
    
    public static JobScheduler getInstance() {
        if(singletonInstance == null) {
            synchronized(JobScheduler.class) {
                if(singletonInstance == null) {
                    singletonInstance = JobSchedulerSupplier.SUPPLIER.get();
                }
            }
        }
        return singletonInstance;
    }
    
    private boolean isShortTimed(Date nextTime) {
        return nextTime.before( DateUtils.addSeconds(new Date(), JobSchedulerConfig.getPollTimeWindow()) );
    }
    
    @Override
    public boolean createJob(Job job, JobTime jobTime) {
        JobEntity jobEntity = JobEntity.buildAll(job, jobTime);
        boolean inserted = jobTaskService.insertJob(jobEntity);
        if(inserted && isShortTimed(jobEntity.getNextTime()) ) {
            notifier.broadcast(new JobShortTimedCommand(jobEntity.getName()));
        }
        return inserted;
    }

    @Override
    public JobDetail queryJob(String name) {
        return jobTaskService.selectJobByName(name);
    }

    @Override
    public boolean updateJob(Job job, JobTime jobTime) {
        final String name = job.getName();
        JobEntity oldJobEntity = jobTaskService.selectJobByName(name);
        if(! JobUtils.isValid(oldJobEntity)) {
            return false;
        }
        final Long version = oldJobEntity.getVersion();
        JobEntity jobEntity = JobEntity.buildSimple(job, jobTime);
        boolean ok = jobTaskService.updateJobSimpleByName(jobEntity);
        if(! ok) {
            return ok;
        }
        try {
            jobExecutor.compareAndUnschedule(new Key(name, version));
        }finally {
            notifier.broadcast(isShortTimed(jobEntity.getNextTime()) ? new JobUpdatedShortTimedCommand(version, name) : new JobUpdatedCommand(version, name));
        }
        return ok;
    }

    @Override
    public boolean deleteJob(String name) {
        boolean ok = jobTaskService.updateJobDisabled(name, new Date());
        if(! ok) {
            return ok;
        }
        try{
            jobExecutor.unschedule(name);
        }finally {
            notifier.broadcast(new JobDeletedCommand(name));
        }
        return ok;
    }
    
}
