package com.github.yangwk.jobscheduler.core.impl.registry;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

public class CommonExecutor implements Executor, AutoCloseable{

    private final ExecutorService executorService;
    
    public CommonExecutor() {
        int size = 1;
        this.executorService = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new DiscardPolicy());
    }
    
    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }
    
    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }
}
