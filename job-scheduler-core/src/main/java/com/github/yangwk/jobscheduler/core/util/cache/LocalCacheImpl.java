package com.github.yangwk.jobscheduler.core.util.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LocalCacheImpl<K,V> implements LocalCache<K,V>{
    private final ConcurrentHashMap<K, Future<V>> cache = new ConcurrentHashMap<>();
    
    // don't use java 8 computeIfAbsent
    @Override
    public V computeIfAbsent(K key, Supplier<V> supplier) {
        Future<V> f = cache.get(key);
        if (f == null) {
            Callable<V> eval = () -> supplier.get();
            FutureTask<V> ft = new FutureTask<V>(eval);
            f = cache.putIfAbsent(key, ft);
            if (f == null) {
                f = ft;
                ft.run();
            }
        }
        try {
            return f.get();
        } catch (CancellationException e) {
            cache.remove(key, f);
        } catch (InterruptedException e) {
            // ignore
        } catch (ExecutionException e) {
            throw new IllegalStateException("Not unchecked", e);
        } 
        
        return null;
    }
    
    private V obtain(Future<V> f) {
        if(f != null) {
            try {
                return f.get();
            } catch (InterruptedException e) {
                // ignore
            } catch (ExecutionException e) {
                throw new IllegalStateException("Not unchecked", e);
            }
        }
        return null;
    }
    
    @Override
    public V get(K key) {
        return obtain(cache.get(key));
    }
    
    @Override
    public V remove(K key) {
        return obtain(cache.remove(key));
    }
    
    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Future<V> future = cache.computeIfPresent(key, (k,f) -> {
            V v = remappingFunction.apply(k, obtain(f));
            if(v == null) {
                return null;
            }
            Callable<V> eval = () -> v;
            return new FutureTask<V>(eval);
        });
        if(future != null) {
            ((FutureTask<?>)future).run();
        }
        return obtain(future);
    }
    
    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Future<V> future = cache.compute(key, (k,f) -> {
            V v = remappingFunction.apply(k, obtain(f));
            if(v == null) {
                return null;
            }
            Callable<V> eval = () -> v;
            return new FutureTask<V>(eval);
        });
        if(future != null) {
            ((FutureTask<?>)future).run();
        }
        return obtain(future);
    }
    
}
