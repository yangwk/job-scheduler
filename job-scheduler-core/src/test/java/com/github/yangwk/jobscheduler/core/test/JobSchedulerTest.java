package com.github.yangwk.jobscheduler.core.test;

import java.util.Scanner;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.github.yangwk.jobscheduler.core.JobData;
import com.github.yangwk.jobscheduler.core.JobDetail;
import com.github.yangwk.jobscheduler.core.JobTime;
import com.github.yangwk.jobscheduler.core.impl.BaseJob;
import com.github.yangwk.jobscheduler.core.impl.JobDataImpl;
import com.github.yangwk.jobscheduler.core.impl.JobScheduler;

public class JobSchedulerTest {
    
    public static class HelloWorldJob extends BaseJob {

        public HelloWorldJob() {
            super("HelloWorld-Job");
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
    
    
    public static class LoveJob extends BaseJob {

        public LoveJob() {
            super("Love-Job");
        }

        @Override
        public void run() {
            LOG.info("Love {}", getJobData().toString());
        }

        @Override
        protected JobData initJobData() {
            JobData jobData = new JobDataImpl();
            jobData.put("target", "girl");
            jobData.put("action", "I love you");
            return jobData;
        }
        
    }
    
    static String toString(Object obj) {
        return ToStringBuilder.reflectionToString(obj, ToStringStyle.JSON_STYLE);
    }
    
    public static void main(String[] args) {
        JobScheduler jobScheduler = JobScheduler.getInstance();
        
        try(Scanner scanner = new Scanner(System.in)){
            while(true) {
                int n = scanner.nextInt();
                if(n == 9) {
                    System.exit(0);
                    break;
                }
                if(n == 1) {
                    jobScheduler.createJob(new HelloWorldJob(), JobTime.buildOneShot(20));
                }else if(n == 2) {
                    jobScheduler.createJob(new LoveJob(), JobTime.buildRepeatable(50, 10));
                }else if(n == 3) {
                    jobScheduler.updateJob(new HelloWorldJob(), JobTime.buildRepeatable(23, 30));
                }else if(n == 4) {
                    JobDetail jobDetail = jobScheduler.queryJob("HelloWorld-Job");
                    System.out.println(toString(jobDetail));
                }else if(n == 5) {
                    jobScheduler.deleteJob("HelloWorld-Job");
                }
            }
        }
    }
    
}
