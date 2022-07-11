package com.github.yangwk.jobscheduler.core.jdbc.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.github.yangwk.jobscheduler.core.impl.JobShard;
import com.github.yangwk.jobscheduler.core.jdbc.JdbcHelper;
import com.github.yangwk.jobscheduler.core.jdbc.entity.JobEntity;

public class JobDAO extends AbstractDAO<JobEntity> {
    
    public JobDAO(JdbcHelper jdbcHelper) {
        super(jdbcHelper);
    }
    
    public List<JobEntity> selectReady(Integer limit, Date nextTimeEnd){
        String sql = "select * from job where enabled = 1 and state != 2 and next_time <= ? and id % ? = ? order by next_time asc";
        Pair<Integer, Integer> pair = JobShard.get();
        Object[] paras = {nextTimeEnd, pair.getLeft(), pair.getRight()};
        return super.select(sql, limit, paras);
    }
    
    public boolean updateStarted(Long id, Date startTime) {
        if(startTime != null) {
            String sql = "update job set start_time = ? , state = 1 where id = ? and state != 2";
            Object[] paras = {startTime, id};
            return jdbcHelper.update(sql, paras) > 0;
        }
        String sql = "update job set state = 1 where id = ? and state != 2";
        Object[] paras = {id};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
    public boolean updateEnded(Long id, Date endTime) {
        String sql = "update job set end_time = ? , state = 2 where id = ? and state != 2";
        Object[] paras = {endTime, id};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
    public boolean updateNextTime(Long id, Date nextTime) {
        String sql = "update job set next_time = ? where id = ? and state != 2";
        Object[] paras = {nextTime, id};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
    
    public JobEntity selectById(Long id) {
        String sql = "select * from job where id = ?";
        Object[] paras = {id};
        List<JobEntity> result = super.select(sql, null, paras);
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }
    
    public JobEntity selectByName(String name) {
        String sql = "select * from job where name = ?";
        Object[] paras = {name};
        List<JobEntity> result = super.select(sql, null, paras);
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }
    
    public boolean updateDisabled(String name, Date endTime) {
        String sql = "update job set enabled = 0 , end_time = ? , state = 2 where name = ? and state != 2";
        Object[] paras = {endTime, name};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
    public boolean updateSimpleByName(JobEntity jobEntity) {
        String sql = "update job set next_time = ? , class_name = ? , data = ? , initial_delay = ? , delay = ? , repeatable = ? , version = version + 1 where name = ? and state != 2";
        Object[] paras = {jobEntity.getNextTime(), jobEntity.getClassName(), jobEntity.getDataInputStream(), 
                    jobEntity.getInitialDelay(), jobEntity.getDelay(), jobEntity.getRepeatable(), jobEntity.getName()};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
}
