package com.github.yangwk.jobscheduler.core.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;
import com.github.yangwk.jobscheduler.core.jdbc.entity.TaskEntity;
import com.github.yangwk.jobscheduler.core.jdbc.service.JobTaskService;
import com.github.yangwk.jobscheduler.core.util.JobUtils;

public class JobTaskDecorator implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(JobTaskDecorator.class);
    
    private final JobTaskService jobTaskService;
    private final Runnable runnable;
    private volatile JobEntity jobEntity;
    private final JobExecutor jobExecutor;
    
    public JobTaskDecorator(JobTaskService jobTaskService, Runnable runnable, JobEntity jobEntity, JobExecutor jobExecutor) {
        this.jobTaskService = jobTaskService;
        this.runnable = runnable;
        this.jobEntity = jobEntity;
        this.jobExecutor = jobExecutor;
    }

    // here all SQL operation can not swallow Throwable
    private boolean beforeImpl() {
        jobEntity = jobTaskService.selectJobById(jobEntity.getId());
        if(! JobUtils.isValid(jobEntity)) {
            return false;
        }
        
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setJobId(jobEntity.getId());
        taskEntity.setStartTime(new Date());
        taskEntity.setEndTime(null);
        taskEntity.setState(1);
        taskEntity.setInstanceId(JobSchedulerConfig.getHostInfo().toString());
        taskEntity.setNextTime(jobEntity.getNextTime());
        return jobTaskService.updateStarted(jobEntity.getId(), jobEntity.getStartTime() == null ? new Date() : null, taskEntity);
    }
    
    private boolean before() {
        Boolean keep = Boolean.FALSE;
        try {
            keep = beforeImpl();
        }catch (Exception e) {
            List<Throwable> list = ExceptionUtils.getThrowableList(e);
            if(CollectionUtils.isNotEmpty(list)) {
                for(Throwable th : list) {
                    // can keep to schedule
                    if(th instanceof SQLException) {
                        keep = Boolean.TRUE;
                        break;
                    }
                }
            }
            throw new IllegalStateException(e);
        }finally {
            // SQL operation return false
            if(! keep) {
                if(jobEntity != null) {
                    jobExecutor.unschedule(jobEntity.getName());
                }
            }
        }
        return keep;
    }
    
    @Override
    public void run() {
        try {
            if(! before()) {
                return ;
            }
            
            runnable.run();
        }catch (Throwable t) {
            if(LOG.isErrorEnabled()) {
                LOG.error("run error", t);
            }
        } finally {
            after();
        }
    }
    
    private void after() {
        Date endTime = new Date();
        if(jobEntity.getRepeatable() != 1) {
            jobTaskService.updateOneShotJobEnded(jobEntity.getId(), endTime, jobEntity.getNextTime(), endTime);
        }else {
            jobTaskService.updateRepeatableJobEnded(jobEntity.getId(), 
                    JobUtils.calcNextTime(jobEntity.getJobTime(), false), 
                    jobEntity.getNextTime(), endTime);
        }
    }
    
}
