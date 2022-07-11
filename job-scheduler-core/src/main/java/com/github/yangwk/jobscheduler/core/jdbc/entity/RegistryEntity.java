package com.github.yangwk.jobscheduler.core.jdbc.entity;

import java.util.Date;

import com.github.yangwk.jobscheduler.core.jdbc.annotation.Id;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Table;

@Table(name = "registry")
public class RegistryEntity {
    @Id
    private Long id;
    
    private String host;
    private Integer port;
    private Date ticketTime;
    private Integer enabled;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public Date getTicketTime() {
        return ticketTime;
    }
    public void setTicketTime(Date ticketTime) {
        this.ticketTime = ticketTime;
    }
    public Integer getEnabled() {
        return enabled;
    }
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
    
}
