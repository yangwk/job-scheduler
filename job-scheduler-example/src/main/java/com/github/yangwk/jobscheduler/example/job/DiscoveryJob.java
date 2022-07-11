package com.github.yangwk.jobscheduler.example.job;

import com.github.yangwk.jobscheduler.core.JobData;
import com.github.yangwk.jobscheduler.core.impl.BaseJob;
import com.github.yangwk.jobscheduler.core.impl.JobDataImpl;

public class DiscoveryJob extends BaseJob {

    public DiscoveryJob() {
        super("DiscoveryJob");
    }

    @Override
    public void run() {
        LOG.info("Discovery {}", getJobData().toString());
    }

    @Override
    protected JobData initJobData() {
        JobData jobData = new JobDataImpl();
        jobData.put("target", "secret");
        return jobData;
    }
    
}