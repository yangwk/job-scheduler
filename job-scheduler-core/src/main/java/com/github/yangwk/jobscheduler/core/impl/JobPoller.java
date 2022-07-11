package com.github.yangwk.jobscheduler.core.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yangwk.jobscheduler.core.Job;
import com.github.yangwk.jobscheduler.core.impl.JobExecutor.Key;
import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;
import com.github.yangwk.jobscheduler.core.jdbc.service.JobTaskService;
import com.github.yangwk.jobscheduler.core.util.JobUtils;

public class JobPoller {
    private static final Logger LOG = LoggerFactory.getLogger(JobPoller.class);
    
    private final int timeWindow;
    private final int limit;
    
    private Thread thread;
    private final JobTaskService jobTaskService;
    private final JobExecutor jobExecutor;
    private final Object mutex = new Object();
    
    public JobPoller(JobTaskService jobTaskService, JobExecutor jobExecutor) {
        this.jobTaskService = jobTaskService;
        this.jobExecutor = jobExecutor;
        this.timeWindow = JobSchedulerConfig.getPollTimeWindow();
        this.limit = JobSchedulerConfig.getPollLimit();
    }
    
    public void start() {
        if(thread != null) {
            return ;
        }
        thread = new Thread(() -> {
            while(true) {
                try {
                    work();
                    
                    
                    synchronized (mutex) {
                        try {
                            mutex.wait(timeWindow * 1000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                    
                }catch (Throwable t) {
                    if(LOG.isErrorEnabled()) {
                        LOG.error("work error", t);
                    }
                }
            }
        });
        thread.setName("JobPoller");
        thread.setDaemon(true);
        thread.start();
    }
    
    
    private void work() {
        List<JobEntity> jobEntities = jobTaskService.selectReadyJob(limit, DateUtils.addSeconds(new Date(), timeWindow));
        for(JobEntity je : jobEntities) {
            Job job = null;
            try {
                job = je.getJob();
            } catch (Throwable t) {
                if(LOG.isErrorEnabled()) {
                    LOG.error("can not instance job", t);
                }
            }
            if(job == null) {
                continue;
            }
            JobTaskDecorator decorator = new JobTaskDecorator(jobTaskService, job, je, jobExecutor);
            if(je.getRepeatable() == 1) {
                jobExecutor.scheduleWithFixedDelay(new Key(je.getName(), je.getVersion()), decorator, JobUtils.calcRealDelay(je.getNextTime()), je.getDelay());
            }else {
                jobExecutor.schedule(new Key(je.getName(), je.getVersion()), decorator, JobUtils.calcRealDelay(je.getNextTime()));
            }
        }
    }
    
    public void wakeup() {
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }
    
}