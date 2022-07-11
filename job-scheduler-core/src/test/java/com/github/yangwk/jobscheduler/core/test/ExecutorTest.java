package com.github.yangwk.jobscheduler.core.test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutorTest.class);
    
    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        
        System.out.println(new Date());
        ScheduledFuture<?> future = executorService.scheduleWithFixedDelay(()->{
            LOG.info("hello");
        }, 0, 10, TimeUnit.SECONDS);
        
        System.out.println(future.isDone());
        
//        future.cancel(false);
        
        
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println(future.isDone());
        
        executorService.shutdown();
    }
    
}
