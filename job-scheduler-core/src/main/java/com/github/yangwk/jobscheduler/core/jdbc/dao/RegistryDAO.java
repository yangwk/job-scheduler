package com.github.yangwk.jobscheduler.core.jdbc.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.github.yangwk.jobscheduler.core.jdbc.JdbcHelper;
import com.github.yangwk.jobscheduler.core.jdbc.entity.RegistryEntity;

public class RegistryDAO extends AbstractDAO<RegistryEntity> {
    
    public RegistryDAO(JdbcHelper jdbcHelper) {
        super(jdbcHelper);
    }
    
    public RegistryEntity selectOne(String host, Integer port){
        String sql = "select * from registry where host = ? and port = ?";
        Object[] paras = {host, port};
        List<RegistryEntity> result = super.select(sql, null, paras);
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }
    
    public List<RegistryEntity> selectEnabled(){
        String sql = "select * from registry where enabled = 1 ";
        return super.select(sql, (Integer)null, (Object[])null);
    }
    
    
    public boolean updateTicketTime(String host, Integer port, Date newTicketTime) {
        String sql = "update registry set ticket_time = ? where host = ? and port = ? and enabled = 1";
        Object[] paras = {newTicketTime, host, port};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
    
    public boolean updateDisabled(String host, Integer port, Date oldTicketTime) {
        String sql = "update registry set enabled = 0 where host = ? and port = ? and ticket_time = ?";
        Object[] paras = {host, port, oldTicketTime};
        return jdbcHelper.update(sql, paras) > 0;
    }
    
}
