package com.github.yangwk.jobscheduler.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;


public class Reflector<T> {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    private final Class<T> clazz;
    private Map<String, Method> getterMethods;
    private Map<String, Method> setterMethods;
    
    public Reflector(Class<T> clazz) {
        this.clazz = clazz;
        init();
    }

    private void init() {
        Map<String, Method> getterMap = new HashMap<>();
        Map<String, Method> setterMap = new HashMap<>();
        Field[] fields = FieldUtils.getAllFields(clazz);
        Method[] methods = clazz.getMethods();
        for(Field f : fields) {
            String fieldName = f.getName();
            int found = 0;
            for(Method m : methods) {
                if(isGetter(m, fieldName)) {
                    getterMap.put(fieldName, m);
                    found ++;
                }else if(isSetter(m, fieldName)) {
                    setterMap.put(fieldName, m);
                    found ++;
                }
                
                if(found >= 2) {
                    break;
                }
            }
        }
        getterMethods = Collections.unmodifiableMap(getterMap);
        setterMethods = Collections.unmodifiableMap(setterMap);
    }
    
    private boolean isGetter(Method method, String fieldName) {
        return method != null &&
                Modifier.isPublic(method.getModifiers()) && 
                !Modifier.isStatic(method.getModifiers()) && 
                !method.getReturnType().equals(Void.TYPE) && 
                method.getParameterCount() == 0 && 
                ( method.getName().equals("get" + upperCaseFirstLetter(fieldName)) 
                        || method.getName().equals("is" + upperCaseFirstLetter(fieldName)) );
    }
    
    private boolean isSetter(Method method, String fieldName) {
        return method != null &&
                Modifier.isPublic(method.getModifiers()) &&
                !Modifier.isStatic(method.getModifiers()) && 
                method.getReturnType().equals(Void.TYPE) && 
                method.getParameterCount() == 1 && 
                method.getName().equals("set" + upperCaseFirstLetter(fieldName));
    }
    
    private String upperCaseFirstLetter(String name){
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    public Method obtainGetter(String fieldName) {
        return getterMethods.get(fieldName);
    }
    
    public Method obtainSetter(String fieldName) {
        return setterMethods.get(fieldName);
    }
    
    public Method obtainGetterSimilar(String methodName) {
        try {
            return clazz.getMethod(methodName, EMPTY_CLASS_ARRAY);
        } catch (Exception e) {
            throw new IllegalStateException("getMethod error", e);
        }
    }
    
    public Class<T> getType(){
        return clazz;
    }
    

    public Object invokeGetterSimilar(T t, Method method) {
        try {
            return method.invoke(t, EMPTY_OBJECT_ARRAY);
        } catch (Exception e) {
            throw new IllegalStateException("invoke error", e);
        }
    }

}
