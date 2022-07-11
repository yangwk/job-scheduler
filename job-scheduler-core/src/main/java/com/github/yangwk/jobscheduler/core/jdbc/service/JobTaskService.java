package com.github.yangwk.jobscheduler.core.jdbc.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.yangwk.jobscheduler.core.jdbc.BusinessException;
import com.github.yangwk.jobscheduler.core.jdbc.JdbcHelper;
import com.github.yangwk.jobscheduler.core.jdbc.dao.JobDAO;
import com.github.yangwk.jobscheduler.core.jdbc.dao.TaskDAO;
import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;
import com.github.yangwk.jobscheduler.core.jdbc.entity.TaskEntity;

public class JobTaskService {
    private final DataSource dataSource;
    private final JdbcHelper jdbcHelper;
    private final JobDAO jobDAO;
    private final TaskDAO taskDAO;

    public JobTaskService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcHelper = new JdbcHelper(this.dataSource);
        this.jobDAO = new JobDAO(this.jdbcHelper);
        this.taskDAO = new TaskDAO(this.jdbcHelper);
    }
    
    
    public boolean updateRepeatableJobEnded(Long jobId, Date jobNewNextTime, Date jobOldNextTime, Date taskEndTime) {
        try {
            jdbcHelper.beginTransaction();
            if(! jobDAO.updateNextTime(jobId, jobNewNextTime) ) {
                throw new IllegalStateException();
            }
            if(! taskDAO.updateEnded(jobId, jobOldNextTime, taskEndTime)) {
                throw new IllegalStateException();
            }
            
            jdbcHelper.commitTransaction();
            return true;
        }catch (Throwable t) {
            jdbcHelper.rollbackTransaction();
        }finally {
            jdbcHelper.recover();
        }
        return false;
    }
    
    
    public boolean updateOneShotJobEnded(Long jobId, Date jobEndTime, Date jobNextTime, Date taskEndTime) {
        try {
            jdbcHelper.beginTransaction();
            if(! jobDAO.updateEnded(jobId, jobEndTime) ) {
                throw new IllegalStateException();
            }
            if(! taskDAO.updateEnded(jobId, jobNextTime, taskEndTime)) {
                throw new IllegalStateException();
            }
            
            jdbcHelper.commitTransaction();
            return true;
        }catch (Throwable t) {
            jdbcHelper.rollbackTransaction();
        }finally {
            jdbcHelper.recover();
        }
        return false;
    }
    
    public boolean updateStarted(Long jobId, Date jobStartTime, TaskEntity taskEntity) {
        try {
            jdbcHelper.beginTransaction();
            if(! jobDAO.updateStarted(jobId, jobStartTime) ) {
                throw new BusinessException();
            }
            if(! taskDAO.insert(taskEntity)) {
                throw new BusinessException();
            }
            
            jdbcHelper.commitTransaction();
            return true;
        }catch (Throwable t) {
            jdbcHelper.rollbackTransaction();
            boolean swallow = (ExceptionUtils.getRootCause(t) instanceof BusinessException)
                    || (t instanceof BusinessException);
            if(! swallow) {
                throw new IllegalStateException(t);
            }
        }finally {
            jdbcHelper.recover();
        }
        return false;
    }
    
    public boolean insertJob(JobEntity jobEntity) {
        try {
            return jobDAO.insert(jobEntity);
        }catch (Exception e) {
            // XXX maybe other database is not SQLIntegrityConstraintViolationException
            boolean swallow = (ExceptionUtils.getRootCause(e) instanceof SQLIntegrityConstraintViolationException)
                    || (e instanceof SQLIntegrityConstraintViolationException);
            if(! swallow) {
                throw new IllegalStateException(e);
            }
        }finally {
            jdbcHelper.recover();
        }
        return false;
    }
    
    public List<JobEntity> selectReadyJob(Integer limit, Date nextTimeEnd){
        try {
            return jobDAO.selectReady(limit, nextTimeEnd);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
    public JobEntity selectJobById(Long id){
        try {
            return jobDAO.selectById(id);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
    public JobEntity selectJobByName(String name){
        try {
            return jobDAO.selectByName(name);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
    public boolean updateJobDisabled(String name, Date endTime) {
        try {
            return jobDAO.updateDisabled(name, endTime);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
    public boolean updateJobSimpleByName(JobEntity jobEntity) {
        try {
            jdbcHelper.beginTransaction();
            if(! jobDAO.updateSimpleByName(jobEntity) ) {
                throw new IllegalStateException();
            }
            
            jdbcHelper.commitTransaction();
            return true;
        }catch (Throwable t) {
            jdbcHelper.rollbackTransaction();
        }finally {
            jdbcHelper.recover();
        }
        return false;
    }
    
    
    
    
    
    
    // for test
    public boolean insertTask(TaskEntity taskEntity){
        try {
            return taskDAO.insert(taskEntity);
        }finally {
            jdbcHelper.recover();
        }
    }
    // for test
    public boolean updateEndedTask(Long jobId, Date jobNextTime, Date endTime){
        try {
            return taskDAO.updateEnded(jobId, jobNextTime, endTime);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
}

