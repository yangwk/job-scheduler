package com.github.yangwk.jobscheduler.core.jdbc.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.github.yangwk.jobscheduler.core.jdbc.JdbcHelper;
import com.github.yangwk.jobscheduler.core.util.BeanUtils;
import com.github.yangwk.jobscheduler.core.util.Reflector;
import com.github.yangwk.jobscheduler.core.util.SqlUtils;
import com.github.yangwk.jobscheduler.core.util.cache.LocalCache;
import com.github.yangwk.jobscheduler.core.util.cache.LocalCacheImpl;

public abstract class AbstractDAO<T> {
    
    protected final JdbcHelper jdbcHelper;
    private final Class<T> clazz;
    
    private static final LocalCache<Class<?>, Context<?>> contextCache = new LocalCacheImpl<>();
    
    @SuppressWarnings("unchecked")
    protected AbstractDAO(JdbcHelper jdbcHelper) {
        this.jdbcHelper = jdbcHelper;
        this.clazz = (Class<T>)  ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        
        contextCache.computeIfAbsent(this.clazz, () -> {
            Field[] allFields = FieldUtils.getAllFields(this.clazz);
            Reflector<T> reflector = new Reflector<>(this.clazz);
            Pair<String, List<Method>> insertInfo = SqlUtils.buildInsertSqlInfo(reflector, allFields);
            Map<String, String> beanLabelMapping = SqlUtils.buildBeanLabelMapping(this.clazz, allFields);
            return new Context<>(insertInfo.getLeft(), insertInfo.getRight(), beanLabelMapping, reflector);
        });
    }
    
    private static class Context<T>{
        private final String insertSql;
        private final List<Method> insertUseMethods;
        private final Map<String, String> beanLabelMapping;
        private final Reflector<T> reflector;
        private Context(String insertSql, List<Method> insertUseMethods, Map<String, String> beanLabelMapping,
                Reflector<T> reflector) {
            this.insertSql = insertSql;
            this.insertUseMethods = insertUseMethods;
            this.beanLabelMapping = beanLabelMapping;
            this.reflector = reflector;
        }
        
    }
    
    
    public boolean insert(T t) {
        @SuppressWarnings("unchecked")
        Context<T> context = (Context<T>) contextCache.get(clazz);
        String sql = context.insertSql;
        Object[] paras = new Object[context.insertUseMethods.size()];
        for(int r=0; r<paras.length; r++) {
            paras[r] = context.reflector.invokeGetterSimilar(t, context.insertUseMethods.get(r));
        }
        return jdbcHelper.update(sql, paras) > 0;
    }

    public List<T> select(String sql, Integer limit, Object... paras){
        Context<?> context = contextCache.get(clazz);
        List<Map<String,Object>> list = jdbcHelper.query(sql, limit, context.beanLabelMapping, paras);
        List<T> retval = new ArrayList<>();
        for(Map<String,Object> m : list) {
            try {
                T t = clazz.newInstance();
                BeanUtils.populate(t, m);
                retval.add(t);
            } catch (Exception e) {
                throw new IllegalStateException("bean populate error", e);
            }
        }
        return retval;
    }
    
}

