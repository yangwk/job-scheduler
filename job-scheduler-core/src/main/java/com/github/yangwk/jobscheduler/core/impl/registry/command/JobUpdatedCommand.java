package com.github.yangwk.jobscheduler.core.impl.registry.command;

public class JobUpdatedCommand extends JobCommand{
    public JobUpdatedCommand(Long version, String name) {
        super(String.valueOf(version) + "," + name);
    }

    @Override
    protected byte getHead() {
        return 0x02;
    }

    
}
