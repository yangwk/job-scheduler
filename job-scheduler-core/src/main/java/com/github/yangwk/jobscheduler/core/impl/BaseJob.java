package com.github.yangwk.jobscheduler.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yangwk.jobscheduler.core.Job;
import com.github.yangwk.jobscheduler.core.JobData;

public abstract class BaseJob implements Job {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    private JobData jobData;
    private final String name;
    
    protected BaseJob(String name) {
        this.name = name;
    }
    
    @Override
    final public void init(JobData jobData) {
        this.jobData = jobData;
    }

    @Override
    final public JobData getJobData() {
        if(jobData == null) {
            jobData = initJobData();
        }
        return jobData;
    }
    
    protected abstract JobData initJobData();

    @Override
    final public String getName() {
        return name;
    }
    
}
