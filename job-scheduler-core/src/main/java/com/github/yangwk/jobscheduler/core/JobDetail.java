package com.github.yangwk.jobscheduler.core;

import java.util.Date;

public interface JobDetail {
    Long getVersion();
    Integer getEnabled();
    Date getNextTime();
    Date getStartTime();
    Date getEndTime();
    Integer getState();
    Job getJob() throws Exception;
    JobTime getJobTime();
}
