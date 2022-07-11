package com.github.yangwk.jobscheduler.core.test;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.github.yangwk.jobscheduler.core.Job;
import com.github.yangwk.jobscheduler.core.JobData;
import com.github.yangwk.jobscheduler.core.JobTime;
import com.github.yangwk.jobscheduler.core.impl.BaseJob;
import com.github.yangwk.jobscheduler.core.impl.JobDataImpl;
import com.github.yangwk.jobscheduler.core.jdbc.DataSourceCreator;
import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;
import com.github.yangwk.jobscheduler.core.jdbc.service.JobTaskService;

public class JobTaskServiceTest {
    
    public static class MyJob extends BaseJob {
        
        public MyJob() {
            super("hello-world");
        }

        @Override
        public void run() {
            LOG.info("hello world, my age is " + getJobData().get("age"));
        }

        @Override
        protected JobData initJobData() {
            JobData jobData = new JobDataImpl();
            jobData.put("age", 18);
            return jobData;
        }

    }
    
    static String toString(Object obj) {
        return ToStringBuilder.reflectionToString(obj, ToStringStyle.JSON_STYLE);
    }
    
    public static void main(String[] args) {
        DataSource dataSource = new DataSourceCreator().getDataSource();
        JobTaskService jobTaskService = new JobTaskService(dataSource);
        
        Job job = new MyJob();
        JobTime jobTime = JobTime.buildOneShot(30);
        JobEntity jobEntity = JobEntity.buildAll(job, jobTime);
        boolean inserted = jobTaskService.insertJob(jobEntity);
        System.out.println(inserted);
        
        
        JobEntity query = jobTaskService.selectJobByName(jobEntity.getName());
        try {
            Job runJob = query.getJob();
            System.out.println(runJob.getName());
            System.out.println(runJob.getJobData().toString());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        System.out.println(toString(query));
        
        
        boolean ok = jobTaskService.updateRepeatableJobEnded(2L, new Date(), new Date(), new Date());
        System.out.println(ok);
        
        if(dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable)dataSource).close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
