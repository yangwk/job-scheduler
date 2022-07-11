package com.github.yangwk.jobscheduler.core.impl;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.yangwk.jobscheduler.core.util.cache.LocalCache;
import com.github.yangwk.jobscheduler.core.util.cache.LocalCacheImpl;

public class JobExecutor implements AutoCloseable{
    
    private final ScheduledExecutorService executorService;
    private final LocalCache<Key, Value<ScheduledFuture<?>>> localCache = new LocalCacheImpl<>();

    public JobExecutor() {
        int corePoolSize = JobSchedulerConfig.getJobExecutorSize();
        this.executorService = new ScheduledThreadPoolExecutor(corePoolSize, new AbortPolicy());
    }

    
    public boolean schedule(Key key, Runnable command, long delay) {
        localCache.computeIfPresent(key, (k,value) -> {
            if(! k.version.equals(value.version)) {
                value.v.cancel(false);
                return null;
            }
            return value;
        });
        
        localCache.computeIfAbsent(key, () -> {
            ScheduledFuture<?> future = executorService.schedule(command, delay, TimeUnit.SECONDS);
            return new Value<>(future, key.version);
        });
        return true;
    }
    
    public boolean scheduleWithFixedDelay(Key key, Runnable command, long initialDelay, long delay) {
        localCache.computeIfPresent(key, (k,value) -> {
            if(! k.version.equals(value.version)) {
                value.v.cancel(false);
                return null;
            }
            return value;
        });
        
        localCache.computeIfAbsent(key, () -> {
            ScheduledFuture<?> future = executorService.scheduleWithFixedDelay(command, initialDelay, delay, TimeUnit.SECONDS);
            return new Value<>(future, key.version);
        });
        return true;
    }
    
    public boolean unschedule(String name) {
        Value<ScheduledFuture<?>> value = localCache.remove(new Key(name, null));
        if(value != null && value.v != null) {
            return value.v.cancel(false);
        }
        return false;
    }
    
    public boolean compareAndUnschedule(Key key) {
        return localCache.computeIfPresent(key, (k,value) -> {
            if(k.version.equals(value.version)) {
                value.v.cancel(false);
                return null;
            }
            return value;
        }) == null;
    }
    
    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }
    
    
    public static final class Key {
        private final String name;
        private final Long version;
        public Key(String name, Long version) {
            this.name = Objects.requireNonNull(name);
            this.version = Objects.requireNonNull(version == null ? 0 : version);
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(name).toHashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) {
              return false;
            }
            Key rhs = (Key) obj;
            return new EqualsBuilder()
                          .append(name, rhs.name)
                          .isEquals();

        }

    }
    
    
     static final class Value<V> {
        private final V v;
        private final Long version;
        public Value(V v, Long version) {
            this.v = v;
            this.version = Objects.requireNonNull(version == null ? 0 : version);
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(v).toHashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) {
              return false;
            }
            Value<?> rhs = (Value<?>) obj;
            return new EqualsBuilder()
                          .append(v, rhs.v)
                          .isEquals();

        }
        
    }
    
}


