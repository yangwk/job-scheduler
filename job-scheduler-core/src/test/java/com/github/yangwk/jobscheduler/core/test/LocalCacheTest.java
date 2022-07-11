package com.github.yangwk.jobscheduler.core.test;

import java.util.Collections;
import java.util.Map;

import com.github.yangwk.jobscheduler.core.util.cache.LocalCache;

public class LocalCacheTest {

    public static void main(String[] args) {
        LocalCache<String, Object> localCache = new com.github.yangwk.jobscheduler.core.util.cache.LocalCacheImpl<>();
//        LocalCache<String, Object> localCache = new com.github.yangwk.jobscheduler.core.util.cache.LocalCacheMap<>();
        Map<String, Object> map1 = Collections.singletonMap("1", new Object());
        Map<String, Object> map2 = Collections.singletonMap("2", new Object());
        
        Object retval1 = localCache.computeIfAbsent("one", () -> map1);
        System.out.println(retval1 == map1);
        Object retval2 = localCache.computeIfAbsent("one", () -> map2);
        System.out.println(retval2 == map1);
        System.out.println(retval2 != map2);
        
        Object retval3 = localCache.get("one");
        System.out.println(retval3 == map1);
        Object retval4 = localCache.get("two");
        System.out.println(retval4 == null);
        
        Object retval5 = localCache.remove("three");
        System.out.println(retval5 == null);
        Object retval6 = localCache.remove("one");
        System.out.println(retval6 == map1);
        
        Object retval8 = localCache.computeIfAbsent("four", () -> map1);
        System.out.println(retval8 == map1);
        Object retval9 = localCache.get("four");
        System.out.println(retval9 == map1);

        Object retval10 = localCache.computeIfPresent("four", (k,v) -> {
           return map2; 
        });
        System.out.println(retval10 == map2);
        Object retval11 = localCache.get("four");
        System.out.println(retval11 == map2);
        
        
        Object retval12 = localCache.computeIfPresent("four", (k,v) -> {
            return null; 
         });
        System.out.println(retval12 == null);
        Object retval13 = localCache.get("four");
        System.out.println(retval13 == null);
        
        Object retval14 = localCache.compute("five", (k,v) -> {
            return map1; 
         });
        System.out.println(retval14 == map1);
        Object retval15 = localCache.get("five");
        System.out.println(retval15 == map1);
        
        Object retval16 = localCache.compute("five", (k,v) -> {
            return null; 
         });
        System.out.println(retval16 == null);
        Object retval17 = localCache.get("five");
        System.out.println(retval17 == null);
    }
}
