package com.github.yangwk.jobscheduler.core;

public final class JobTime {
    private final boolean repeatable;
    private final Integer initialDelay;
    private final Integer delay;
    private JobTime(boolean repeatable, Integer initialDelay, Integer delay) {
        this.repeatable = repeatable;
        this.initialDelay = initialDelay;
        this.delay = delay;
    }
    
    public boolean isRepeatable() {
        return repeatable;
    }
    public Integer getInitialDelay() {
        return initialDelay;
    }
    public Integer getDelay() {
        return delay;
    }
    
    public static JobTime buildOneShot(Integer delay) {
        if(delay == null) {
            throw new IllegalArgumentException("no delay");
        }
        return new JobTime(false, null, delay);
    }
    
    
    public static JobTime buildRepeatable(Integer initialDelay, Integer delay) {
        if(initialDelay == null) {
            throw new IllegalArgumentException("no initialDelay");
        }
        if(delay == null) {
            throw new IllegalArgumentException("no delay");
        }
        return new JobTime(true, initialDelay, delay);
    }
    
}
