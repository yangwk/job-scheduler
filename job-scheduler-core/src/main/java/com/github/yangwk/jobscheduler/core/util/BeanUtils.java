package com.github.yangwk.jobscheduler.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

public final class BeanUtils {
    private static final BeanUtilsBean BEAN_UTILS_BEAN = init();
    
    private BeanUtils() {}
    
    private static BeanUtilsBean init() {
        BeanUtilsBean instance = BeanUtilsBean.getInstance();
        ConvertUtilsBean convertUtilsBean = instance.getConvertUtils();
        convertUtilsBean.register(false, true, -1);
        // registerPrimitives
        convertUtilsBean.register(new BooleanConverter()   , Boolean.TYPE);
        convertUtilsBean.register(new ByteConverter()      , Byte.TYPE);
        convertUtilsBean.register(new CharacterConverter() , Character.TYPE);
        convertUtilsBean.register(new DoubleConverter()    , Double.TYPE);
        convertUtilsBean.register(new FloatConverter()     , Float.TYPE);
        convertUtilsBean.register(new IntegerConverter()   , Integer.TYPE);
        convertUtilsBean.register(new LongConverter()      , Long.TYPE);
        convertUtilsBean.register(new ShortConverter()     , Short.TYPE);
        return instance;
    }
    
    public static void populate(final Object bean, final Map<String, ? extends Object> properties)
            throws IllegalAccessException, InvocationTargetException {
        BEAN_UTILS_BEAN.populate(bean, properties);
    }
    
}
