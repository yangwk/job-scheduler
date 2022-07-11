package com.github.yangwk.jobscheduler.core;

public interface Scheduler {
    boolean createJob(Job job, JobTime jobTime);
    
    JobDetail queryJob(String name);
    
    boolean updateJob(Job job, JobTime jobTime);
    
    boolean deleteJob(String name);
}
