package com.github.yangwk.jobscheduler.core.test;

import com.github.yangwk.jobscheduler.core.impl.JobSchedulerConfig;

public class JobSchedulerConfigTest {
    
    public static void main(String[] args) {
        System.out.println( JobSchedulerConfig.getDatasource() );
        System.out.println( JobSchedulerConfig.getRegistryUseIp() );
        System.out.println( JobSchedulerConfig.getRegistryPort() );
        System.out.println( JobSchedulerConfig.getRegistryPeriod() );
        System.out.println( JobSchedulerConfig.getHostInfo() );
        System.out.println( JobSchedulerConfig.getPollTimeWindow() );
        System.out.println( JobSchedulerConfig.getPollLimit() );
        System.out.println( JobSchedulerConfig.getJobExecutorSize() );
    }
    
}
