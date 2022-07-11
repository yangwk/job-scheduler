package com.github.yangwk.jobscheduler.example.job;

import com.github.yangwk.jobscheduler.core.JobData;
import com.github.yangwk.jobscheduler.core.impl.BaseJob;

public class HelloWorldJob extends BaseJob {

    public HelloWorldJob() {
        super("HelloWorldJob");
    }

    @Override
    public void run() {
        LOG.info("Hello World");
    }

    @Override
    protected JobData initJobData() {
        return null;
    }
    
}