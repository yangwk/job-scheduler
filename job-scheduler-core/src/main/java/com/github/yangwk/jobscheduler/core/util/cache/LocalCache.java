package com.github.yangwk.jobscheduler.core.util.cache;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface LocalCache<K,V> {
    V computeIfAbsent(K key, Supplier<V> supplier);
    
    V get(K key);
    
    V remove(K key);
    
    V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);
    
    V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);
}
