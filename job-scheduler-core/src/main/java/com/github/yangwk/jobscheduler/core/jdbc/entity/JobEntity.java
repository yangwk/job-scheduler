package com.github.yangwk.jobscheduler.core.jdbc.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import com.github.yangwk.jobscheduler.core.Job;
import com.github.yangwk.jobscheduler.core.JobData;
import com.github.yangwk.jobscheduler.core.JobDetail;
import com.github.yangwk.jobscheduler.core.JobTime;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Column;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Exclude;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Id;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Table;
import com.github.yangwk.jobscheduler.core.util.JobUtils;
import com.github.yangwk.jobscheduler.core.util.ObjectUtils;

@Table(name = "job")
public class JobEntity implements JobDetail{
    @Id
    private Long id;
    
    private Long version;
    private Integer enabled;
    private String name;
    private Date nextTime;
    private Date startTime;
    private Date endTime;
    private Integer state;
    private String className;
    
    @Column(insertValueMethod = "getDataInputStream")
    private byte[] data;
    
    @Exclude
    private JobData jobData;
    
    private Integer initialDelay;
    private Integer delay;
    private Integer repeatable;
    
    @Exclude
    private Job job;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    @Override
    public Integer getEnabled() {
        return enabled;
    }
    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public Date getNextTime() {
        return nextTime;
    }
    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }
    @Override
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    @Override
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    @Override
    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public void setJobData(JobData jobData) {
        this.jobData = jobData;
    }
    private JobData getJobData() {
        if(jobData == null) {
            jobData = ObjectUtils.deserialize(getData());
            setData(null);
        }
        return jobData;
    }
    public InputStream getDataInputStream() {
        byte[] bytes = ObjectUtils.serialize(jobData);
        if(bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        return null;
    }
    public Integer getInitialDelay() {
        return initialDelay;
    }
    public void setInitialDelay(Integer initialDelay) {
        this.initialDelay = initialDelay;
    }
    public Integer getDelay() {
        return delay;
    }
    public void setDelay(Integer delay) {
        this.delay = delay;
    }
    public Integer getRepeatable() {
        return repeatable;
    }
    public void setRepeatable(Integer repeatable) {
        this.repeatable = repeatable;
    }
    @Override
    public Job getJob() throws Exception {
        if(job == null) {
            job = JobUtils.instance(className);
            job.init(getJobData());
        }
        return job;
    }
    @Override
    public JobTime getJobTime() {
        return repeatable == 1 ? JobTime.buildRepeatable(initialDelay, delay) : JobTime.buildOneShot(delay);
    }
    
    public static JobEntity buildAll(Job job, JobTime jobTime) {
        JobUtils.check(job, jobTime);
        JobEntity je = new JobEntity();
        je.setVersion(0L);
        je.setEnabled(1);
        je.setName(job.getName());
        je.setNextTime(JobUtils.calcNextTime(jobTime, true));
        je.setStartTime(null);
        je.setEndTime(null);
        je.setState(0);
        je.setClassName(job.getClass().getName());
        je.setJobData(job.getJobData());
        je.setInitialDelay(jobTime.getInitialDelay());
        je.setDelay(jobTime.getDelay());
        je.setRepeatable(jobTime.isRepeatable() ? 1 : 0);
        return je;
    }
    
    public static JobEntity buildSimple(Job job, JobTime jobTime) {
        JobUtils.check(job, jobTime);
        JobEntity je = new JobEntity();
        je.setName(job.getName());
        je.setNextTime(JobUtils.calcNextTime(jobTime, true));
        je.setClassName(job.getClass().getName());
        je.setJobData(job.getJobData());
        je.setInitialDelay(jobTime.getInitialDelay());
        je.setDelay(jobTime.getDelay());
        je.setRepeatable(jobTime.isRepeatable() ? 1 : 0);
        return je;
    }
    
}
