package com.github.yangwk.jobscheduler.core;

public interface Job extends Runnable{
    
    void init(JobData jobData);
    
    JobData getJobData();
    
    String getName();
}
