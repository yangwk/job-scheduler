package com.github.yangwk.jobscheduler.core.impl.registry.command;

public class JobUpdatedShortTimedCommand extends JobCommand{
    public JobUpdatedShortTimedCommand(Long version, String name) {
        super(String.valueOf(version) + "," + name);
    }
    
    @Override
    protected byte getHead() {
        return 0x03;
    }
    
}
