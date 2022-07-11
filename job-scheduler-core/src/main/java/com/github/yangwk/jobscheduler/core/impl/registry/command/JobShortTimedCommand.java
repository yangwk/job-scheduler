package com.github.yangwk.jobscheduler.core.impl.registry.command;

public class JobShortTimedCommand extends JobCommand{
    public JobShortTimedCommand(String name) {
        super(name);
    }

    @Override
    protected byte getHead() {
        return 0x00;
    }
    
}
