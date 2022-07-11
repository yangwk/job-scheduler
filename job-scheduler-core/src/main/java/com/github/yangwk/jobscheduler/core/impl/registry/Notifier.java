package com.github.yangwk.jobscheduler.core.impl.registry;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.github.yangwk.jobscheduler.core.impl.registry.command.Command;

public class Notifier implements AutoCloseable{
    private final AtomicReference<List<HostInfo>> sendList = new AtomicReference<>(null);
    private final CommonExecutor commonExecutor = new CommonExecutor();
    
    public void update(List<HostInfo> target) {
        this.sendList.set(target);
    }

    public void broadcast(Command command) {
        commonExecutor.execute(() -> {
            Optional.ofNullable(sendList.get()).ifPresent(list -> {
                list.forEach(h -> {
                    new Client(h.hostIp, h.port).send(command.getContent());
                });
            });
        });
    }

    @Override
    public void close() throws Exception {
        commonExecutor.close();
    }
    
}
