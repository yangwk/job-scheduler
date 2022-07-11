package com.github.yangwk.jobscheduler.core.impl.registry.command;

@FunctionalInterface
public interface Command {
    byte[] getContent();
}
