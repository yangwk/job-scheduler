package com.github.yangwk.jobscheduler.core.impl.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yangwk.jobscheduler.core.impl.JobExecutor;
import com.github.yangwk.jobscheduler.core.impl.JobPoller;
import com.github.yangwk.jobscheduler.core.impl.JobSchedulerConfig;
import com.github.yangwk.jobscheduler.core.impl.JobExecutor.Key;

public class Server implements AutoCloseable{
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    
    private ServerSocket serverSocket;
    private final CommonExecutor commonExecutor = new CommonExecutor();
    private final JobPoller jobPoller;
    private final JobExecutor jobExecutor;
    
    public Server(JobPoller jobPoller, JobExecutor jobExecutor) {
        this.jobPoller = jobPoller;
        this.jobExecutor = jobExecutor;
    }
    
    public void start() {
        if(serverSocket != null) {
            return ;
        }
        int port = JobSchedulerConfig.getRegistryPort();
        if(LOG.isDebugEnabled()) {
            LOG.debug("use port {}", port);
        }
        Thread thread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress((InetAddress)null, port));
            } catch (IOException e) {
                if(LOG.isErrorEnabled()) {
                    LOG.error("serverSocket bind error", e);
                }
            }
            while(serverSocket != null && serverSocket.isBound() && ! serverSocket.isClosed()) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (Throwable t) {
                    // ignore
                }
                if(socket == null) {
                    continue;
                }
                final Socket sk = socket;
                commonExecutor.execute(() -> {
                    try(Socket s = sk;
                        InputStream input = s.getInputStream();) {
                        byte[] b = new byte[221];
                        int n = input.read(b);
                        if(n == -1) {
                            return ;
                        }
                        final byte head = b[0];
                        final String value = extracValue(b,n);
                        if(LOG.isDebugEnabled()) {
                            LOG.debug("command {} {}", head, value);
                        }
                        switch (head) {
                            case 0x00:
                                handleJobShortTimedCommand();
                                break;
                            case 0x01:
                                handleJobDeletedCommand(value);
                                break;
                            case 0x02:
                                handleJobUpdatedCommand(value);
                                break;
                            case 0x03:
                                handleJobUpdatedShortTimedCommand(value);
                                break;
                            
                            default:
                                break;
                        }
                    }catch (Exception e) {
                        if(LOG.isErrorEnabled()) {
                            LOG.error("execute error", e);
                        }
                    }
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    
    private String extracValue(byte[] b, int n) {
        return new String(b, 1, n-1, StandardCharsets.UTF_8);
    }
    
    private void handleJobShortTimedCommand() {
        jobPoller.wakeup();
    }
    
    private void handleJobDeletedCommand(String name) {
        jobExecutor.unschedule(name);
    }
    
    private void handleJobUpdatedCommand(String value) {
        int i = value.indexOf(",");
        Long version = Long.valueOf(value.substring(0, i));
        String name = value.substring(i+1);
        jobExecutor.compareAndUnschedule(new Key(name, version));
    }
    
    private void handleJobUpdatedShortTimedCommand(String value) {
        try {
            handleJobUpdatedCommand(value);
        }finally {
            handleJobShortTimedCommand();
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        }finally {
            commonExecutor.close();
        }
    }
    
    
}
