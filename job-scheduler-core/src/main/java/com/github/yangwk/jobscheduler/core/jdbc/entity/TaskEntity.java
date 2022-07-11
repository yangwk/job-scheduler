package com.github.yangwk.jobscheduler.core.jdbc.entity;

import java.util.Date;

import com.github.yangwk.jobscheduler.core.jdbc.annotation.Id;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Table;

@Table(name = "task")
public class TaskEntity {
    @Id
    private Long id;
    
    private Long jobId;
    private Date startTime;
    private Date endTime;
    private Integer state;
    private String instanceId;
    private Date nextTime;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getJobId() {
        return jobId;
    }
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    public Date getNextTime() {
        return nextTime;
    }
    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }
    
    
}
