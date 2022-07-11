package com.github.yangwk.jobscheduler.core.test;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;

import com.github.yangwk.jobscheduler.core.impl.JobSchedulerConfig;
import com.github.yangwk.jobscheduler.core.jdbc.DataSourceCreator;
import com.github.yangwk.jobscheduler.core.jdbc.entity.TaskEntity;
import com.github.yangwk.jobscheduler.core.jdbc.service.JobTaskService;

public class DateWhereTest {
    
    static String toString(Object obj) {
        return ToStringBuilder.reflectionToString(obj, ToStringStyle.JSON_STYLE);
    }
    
    public static void main(String[] args) {
        DataSource dataSource = new DataSourceCreator().getDataSource();
        JobTaskService jobTaskService = new JobTaskService(dataSource);
        
        Date nextTime = new Date();
        
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setJobId(1L);
        taskEntity.setStartTime(new Date());
        taskEntity.setEndTime(null);
        taskEntity.setState(1);
        taskEntity.setInstanceId(JobSchedulerConfig.getHostInfo().toString());
        taskEntity.setNextTime(nextTime);
        
        boolean ok = jobTaskService.insertTask(taskEntity);
        System.out.println(ok);
        
        ok = jobTaskService.updateEndedTask(taskEntity.getJobId(), nextTime, DateUtils.addSeconds(nextTime, 30));
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
