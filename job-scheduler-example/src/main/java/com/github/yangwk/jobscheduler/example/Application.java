package com.github.yangwk.jobscheduler.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.yangwk.jobscheduler.core.impl.JobScheduler;

@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public JobScheduler jobScheduler() {
        return JobScheduler.getInstance();
    }
}
