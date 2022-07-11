package com.github.yangwk.jobscheduler.core.impl.registry.command;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class JobCommand implements Command{
    private String value;
    private byte[] content;
    protected JobCommand(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    final public byte[] getContent() {
        if(content != null) {
            return content;
        }
        byte[] a = value.getBytes(StandardCharsets.UTF_8);
        content = new byte[a.length + 1];
        System.arraycopy(a, 0, content, 1, a.length);
        content[0] = getHead();
        return content;
    }
    
    protected abstract byte getHead();
}
