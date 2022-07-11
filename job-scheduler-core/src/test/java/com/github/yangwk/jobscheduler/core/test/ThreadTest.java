package com.github.yangwk.jobscheduler.core.test;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadTest {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadTest.class);
    
    public static void main(String[] args) {
        final Object mutex = new Object();
        
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    
                    LOG.info("hello");
                    
                    synchronized (mutex) {
                        try {
                            mutex.wait(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    
                }catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        thread.setName("JobPoller");
        thread.setDaemon(true);
        thread.start();
        
        try(Scanner scanner = new Scanner(System.in)){
            while(true) {
                int n = scanner.nextInt();
                if(n == 9) {
                    break;
                }
                
                if(n == 1) {
                    synchronized (mutex) {
                        mutex.notifyAll();
                    }
                }
            }
        }
        
    }
    
}
