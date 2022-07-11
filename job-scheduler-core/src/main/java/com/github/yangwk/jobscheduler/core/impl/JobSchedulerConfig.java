package com.github.yangwk.jobscheduler.core.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;

import com.github.yangwk.jobscheduler.core.impl.registry.HostInfo;
import com.github.yangwk.jobscheduler.core.util.InetUtils;

public final class JobSchedulerConfig {
    private static final Properties CONFIG = load();
    
    private static final HostInfo hostInfo = loadHostInfo();
    
    private JobSchedulerConfig() {}

    private static Properties load(){
        Properties prop = new Properties();
        try {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("job-scheduler.properties");
            if(input != null) {
                try {
                    prop.load( input );
                }finally {
                    input.close();
                }
            }
        } catch (IOException e) {
            // ignore
        }
        
        Properties systemProp = System.getProperties();
        if(systemProp != null) {
            systemProp.forEach((k,v) -> {
                String key = k.toString();
                if(key.startsWith("job-scheduler.")) {
                    prop.put(k, v);
                }
            });
        }
        
        check(prop);
        return prop;
    }
    
    private static void check(Properties prop) {
        if(getRegistryPeriod(prop) <= 0) {
            throw new IllegalArgumentException();
        }
        if(getPollTimeWindow(prop) <= 0) {
            throw new IllegalArgumentException();
        }
        if(getPollLimit(prop) <= 0) {
            throw new IllegalArgumentException();
        }
    }
    
    public static Properties getDatasource() {
        Properties prop = new Properties();
        final String prefix = "job-scheduler.datasource.";
        CONFIG.forEach((k,v) -> {
            String key = k.toString();
            if(key.startsWith(prefix)) {
                prop.put(k, v);
            }
        });
        return prop;
    }
    
    
    public static boolean getRegistryUseIp() {
        return Boolean.valueOf(CONFIG.getOrDefault("job-scheduler.config.registry.useIp", "true").toString());
    }
    
    public static int getRegistryPort() {
        return Integer.valueOf(CONFIG.getOrDefault("job-scheduler.config.registry.port", "9750").toString());
    }
    
    private static long getRegistryPeriod(Properties prop) {
        return Integer.valueOf(prop.getOrDefault("job-scheduler.config.registry.period", "30000").toString());
    }
    public static long getRegistryPeriod() {
        return getRegistryPeriod(CONFIG);
    }
    
    
    private static HostInfo loadHostInfo() {
        Pair<String, String> pair = InetUtils.findFirstNonLoopbackHostInfo();
        return new HostInfo(getRegistryUseIp() ? pair.getRight() : pair.getLeft(), getRegistryPort());
    }
    
    public static HostInfo getHostInfo() {
        return hostInfo;
    }
    
    private static int getPollTimeWindow(Properties prop){
        return Integer.valueOf(prop.getOrDefault("job-scheduler.config.poll.timeWindow", "35").toString());
    }
    public static int getPollTimeWindow(){
        return getPollTimeWindow(CONFIG);
    }
    
    
    private static int getPollLimit(Properties prop){
        return Integer.valueOf(prop.getOrDefault("job-scheduler.config.poll.limit", "35").toString());
    }
    public static int getPollLimit(){
        return getPollLimit(CONFIG);
    }
    
    public static int getJobExecutorSize(){
        return Integer.valueOf(CONFIG.getOrDefault("job-scheduler.config.job.executor.size", "8").toString());
    }
    
}
