package com.github.yangwk.jobscheduler.core.util;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.github.yangwk.jobscheduler.core.Job;
import com.github.yangwk.jobscheduler.core.JobTime;
import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;

public class JobUtils {
    
    public static void check(Job job, JobTime jobTime) {
        Objects.requireNonNull(job);
        Objects.requireNonNull(jobTime);
        if(StringUtils.isBlank(job.getName())) {
            throw new IllegalArgumentException("no name");
        }       
    }
    
    public static Date calcNextTime(JobTime jobTime, boolean first) {
        Date now = new Date();
        if(! jobTime.isRepeatable()) {
            return DateUtils.addSeconds(now, jobTime.getDelay());
        }
        
        return DateUtils.addSeconds(now, first ? jobTime.getInitialDelay() : jobTime.getDelay());
    }
    
    public static long calcRealDelay(Date nextTime) {
        Date now = new Date();
        long diff = (nextTime.getTime() - now.getTime()) / 1000;
        return Math.max(diff, 0);
    }
    
    public static Job instance(String className) throws Exception{
        return (Job) Class.forName(className).newInstance();
    }
    
    public static boolean isValid(JobEntity jobEntity) {
        if(jobEntity == null || jobEntity.getEnabled() == 0 || jobEntity.getState() == 2) {
            return false;
        }
        return true;
    }
    
}
