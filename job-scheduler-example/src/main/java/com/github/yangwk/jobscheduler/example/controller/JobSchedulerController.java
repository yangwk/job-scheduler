package com.github.yangwk.jobscheduler.example.controller;

import java.util.Collections;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.yangwk.jobscheduler.core.JobDetail;
import com.github.yangwk.jobscheduler.core.JobTime;
import com.github.yangwk.jobscheduler.core.impl.JobScheduler;
import com.github.yangwk.jobscheduler.example.job.DiscoveryJob;
import com.github.yangwk.jobscheduler.example.job.HelloWorldJob;


@RestController
@RequestMapping("/JobScheduler")
public class JobSchedulerController {
    
    @Autowired
    private JobScheduler jobScheduler;
    
    @GetMapping("/invoke")
    public Object invoke(@RequestParam String action, @RequestParam String name) {
        
        if("create".equals(action)) {
            if("HelloWorldJob".equals(name)) {
                jobScheduler.createJob(new HelloWorldJob(), JobTime.buildOneShot(20));
            }else if("DiscoveryJob".equals(name)) {
                jobScheduler.createJob(new DiscoveryJob(), JobTime.buildRepeatable(50, 10));
            }
        }else if("update".equals(action)) {
            if("DiscoveryJob".equals(name)) {
                jobScheduler.updateJob(new DiscoveryJob(), JobTime.buildRepeatable(23, 30));
            }
        }else if("query".equals(action)) {
            JobDetail jobDetail = jobScheduler.queryJob(name);
            return ToStringBuilder.reflectionToString(jobDetail, ToStringStyle.JSON_STYLE);
        }else if("delete".equals(action)) {
            jobScheduler.deleteJob(name);
        }
        
        return Collections.singletonMap("data", Boolean.TRUE);
    }
    
}
