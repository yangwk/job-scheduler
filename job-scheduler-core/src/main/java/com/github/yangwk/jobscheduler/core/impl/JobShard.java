package com.github.yangwk.jobscheduler.core.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.tuple.Pair;

public final class JobShard {
    private static final AtomicReference<Pair<Integer, Integer>> SHARD = new AtomicReference<>(Pair.of(1,0));
    
    private JobShard() {}
    
    public static void update(Pair<Integer, Integer> v) {
        SHARD.set(v);
    }
    
    public static Pair<Integer, Integer> get() {
        return SHARD.get();
    }
    
}
