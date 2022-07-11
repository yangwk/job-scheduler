package com.github.yangwk.jobscheduler.core.util.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.apache.commons.lang3.JavaVersion;

public class LocalCacheMap<K,V> implements LocalCache<K,V>{
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();

    @Override
    public V computeIfAbsent(K key, Supplier<V> supplier) {
        if(org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            return cache.computeIfAbsent(key, (k) -> {
                return supplier.get();
            });
        }
        V v = cache.get(key);
        if(v == null) {
            v = cache.computeIfAbsent(key, (k) -> {
                return supplier.get();
            });
        }
        return v;
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return cache.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return cache.compute(key, remappingFunction);
    }

}
