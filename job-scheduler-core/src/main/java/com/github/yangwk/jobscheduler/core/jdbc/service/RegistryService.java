package com.github.yangwk.jobscheduler.core.jdbc.service;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.github.yangwk.jobscheduler.core.jdbc.JdbcHelper;
import com.github.yangwk.jobscheduler.core.jdbc.dao.RegistryDAO;
import com.github.yangwk.jobscheduler.core.jdbc.entity.RegistryEntity;

public class RegistryService {
    private final DataSource dataSource;
    private final JdbcHelper jdbcHelper;
    private final RegistryDAO registryDAO;

    public RegistryService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcHelper = new JdbcHelper(this.dataSource);
        this.registryDAO = new RegistryDAO(this.jdbcHelper);
    }
    
    public RegistryEntity selectOne(String host, Integer port){
        try {
            return registryDAO.selectOne(host, port);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    public boolean insert(RegistryEntity registryEntity){
        try {
            return registryDAO.insert(registryEntity);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
    public boolean updateTicketTime(String host, Integer port, Date newTicketTime) {
        try {
            return registryDAO.updateTicketTime(host, port, newTicketTime);
        }finally {
            jdbcHelper.recover();
        }
    }
    
    public List<RegistryEntity> selectEnabled(){
        try {
            return registryDAO.selectEnabled();
        }finally {
            jdbcHelper.recover();
        }
    }
    
    
    public boolean updateDisabled(String host, Integer port, Date oldTicketTime) {
        try {
            return registryDAO.updateDisabled(host, port, oldTicketTime);
        }finally {
            jdbcHelper.recover();
        }
    }

}

