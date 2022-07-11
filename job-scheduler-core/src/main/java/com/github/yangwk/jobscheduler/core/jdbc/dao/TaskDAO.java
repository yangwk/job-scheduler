package com.github.yangwk.jobscheduler.core.jdbc.dao;

import java.util.Date;

import com.github.yangwk.jobscheduler.core.jdbc.JdbcHelper;
import com.github.yangwk.jobscheduler.core.jdbc.entity.TaskEntity;

public class TaskDAO extends AbstractDAO<TaskEntity> {
    
    public TaskDAO(JdbcHelper jdbcHelper) {
        super(jdbcHelper);
    }
    
    public boolean updateEnded(Long jobId, Date jobNextTime, Date endTime) {
        String sql = "update task set end_time = ? , state = 2 where job_id = ? and next_time = ? and state != 2";
        Object[] paras = {endTime, jobId, jobNextTime};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
}
