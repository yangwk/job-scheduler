package com.github.yangwk.jobscheduler.core.test;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yangwk.jobscheduler.core.impl.JobExecutor;
import com.github.yangwk.jobscheduler.core.impl.JobExecutor.Key;

public class JobExecutorTest {
    private static final Logger LOG = LoggerFactory.getLogger(JobExecutorTest.class);
    
    public static void main(String[] args) {
        final JobExecutor jobExecutor = new JobExecutor();
        
        final AtomicLong counter = new AtomicLong(0);
        
        boolean ok = jobExecutor.scheduleWithFixedDelay(new Key("one", null), () -> {
            LOG.info("hello world");
        }, 0, 5);
        
        System.out.println(ok);
        
        ok = jobExecutor.scheduleWithFixedDelay(new Key("one", null), () -> {
            LOG.info("I love pretty girl");
        }, 2, 8);
        
        System.out.println(ok);
        
        jobExecutor.scheduleWithFixedDelay(new Key("two", null), () -> {
            LOG.info("so beautiful");
            long v = counter.incrementAndGet();
            if(v >= 10) {
                jobExecutor.unschedule("two");
                System.out.println("end");
            }
        }, 5, 1);
        
        jobExecutor.scheduleWithFixedDelay(new Key("three", 2L), () -> {
            LOG.info("you are so pretty");
        }, 0, 3);
        
        try(Scanner scanner = new Scanner(System.in)){
            while(true) {
                int n = scanner.nextInt();
                if(n == 9) {
                    try {
                        jobExecutor.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                if(n == 1) {
                    jobExecutor.unschedule("one");
                }else if(n == 2) {
                    jobExecutor.unschedule("two");
                }else if(n == 3) {
                    boolean a = jobExecutor.compareAndUnschedule(new Key("three", null));
                    System.out.println(a);
                }else if(n == 4) {
                    boolean a = jobExecutor.compareAndUnschedule(new Key("three", 2L));
                    System.out.println(a);
                }else if(n == 5) {
                    jobExecutor.scheduleWithFixedDelay(new Key("one", 6L), () -> {
                        LOG.info("hello world new");
                    }, 2, 4);
                }
                
            }
        }
        
    }
    
}
