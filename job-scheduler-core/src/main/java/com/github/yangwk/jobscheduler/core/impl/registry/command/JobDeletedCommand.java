package com.github.yangwk.jobscheduler.core.impl.registry.command;

public class JobDeletedCommand extends JobCommand{
    public JobDeletedCommand(String name) {
        super(name);
    }

    @Override
    protected byte getHead() {
        return 0x01;
    }

    
}
