package com.github.yangwk.jobscheduler.core.util;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.github.yangwk.jobscheduler.core.jdbc.annotation.Column;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Exclude;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Id;
import com.github.yangwk.jobscheduler.core.jdbc.annotation.Table;

public class SqlUtils {
    
    public static Pair<String, List<Method>> buildInsertSqlInfo(Reflector<?> reflector, Field[] allFields) {
        Class<?> clazz = reflector.getType();
        StringBuilder sqlBuilder = new StringBuilder();
        Table table = clazz.getAnnotation(Table.class);
        if(table == null) {
            throw new IllegalStateException(clazz.getName() + " do not have annotation "+ Table.class.getName());
        }
        sqlBuilder.append("insert into ");
        sqlBuilder.append(table.name());
        StringJoiner columnJoiner = new StringJoiner(", ", "(", ")");
        StringJoiner valueJoiner = new StringJoiner(", ", "(", ")");
        List<Method> useMethods = new ArrayList<>();
        for(Field f : allFields) {
            if(f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(Exclude.class)) {
                continue;
            }
            
            Column column = f.getAnnotation(Column.class);
            Method method;
            if(column != null && StringUtils.isNotBlank(column.insertValueMethod())) {
                method = reflector.obtainGetterSimilar(column.insertValueMethod());
            }else {
                method = reflector.obtainGetter(f.getName());
            }
            useMethods.add( Objects.requireNonNull(method, "no Getter or GetterSimilar method") );
            
            columnJoiner.add(convertCamelCase2UnderScoreCase(f.getName()));
            valueJoiner.add("?");
        }
        if(CollectionUtils.isEmpty(useMethods)) {
            throw new IllegalStateException("insert sql no column");
        }
        sqlBuilder.append(columnJoiner.toString());
        sqlBuilder.append(" values ");
        sqlBuilder.append(valueJoiner.toString());
        return Pair.of(sqlBuilder.toString(), Collections.unmodifiableList(useMethods));
    }
    
    public static Map<String, String> buildBeanLabelMapping(Class<?> clazz, Field[] allFields){
        Map<String, String> beanLabelMapping = new HashMap<>();
        for(Field f : allFields) {
            beanLabelMapping.put(convertCamelCase2UnderScoreCase(f.getName()), f.getName());
        }
        return Collections.unmodifiableMap(beanLabelMapping);
    }
    
    
    private static String convertCamelCase2UnderScoreCase(String strCamelCase) {
        StringBuilder sb = new StringBuilder(strCamelCase);
        for(int r=0; r<sb.length(); r++) {
            char ch = sb.charAt(r);
            if(Character.isUpperCase(ch)) {
                sb.replace(r, r+1, "_" + Character.toLowerCase(ch));
                r ++;
            }
        }
        return sb.toString();
    }
    
    static String convertUnderScoreCase2CamelCase(String strUnderScoreCase) {
        StringBuilder sb = new StringBuilder(strUnderScoreCase);
        for(int r=0; r<sb.length(); r++) {
            if(sb.charAt(r) == '_') {
                if(r+1 < sb.length()) {
                    char ch = sb.charAt(r+1);
                    if(Character.isLowerCase(ch)) {
                        sb.replace(r, r+2, String.valueOf(Character.toUpperCase(ch)));
                    }
                }
            }
        }
        return sb.toString();
    }
    
}
