package com.github.yangwk.jobscheduler.core.test;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.yangwk.jobscheduler.core.jdbc.BusinessException;

public class ExTest {
    
    static boolean beforeImpl() {
        if(System.currentTimeMillis() / 2 > 0) {
            throw new BusinessException();
        }
        return true;
    }
    
    static boolean before() {
        Boolean keep = Boolean.FALSE;
        try {
            keep = beforeImpl();
        }catch (Exception e) {
            boolean isSQL = (ExceptionUtils.getRootCause(e) instanceof BusinessException)
                    || (e instanceof BusinessException);
            if(isSQL) {
                keep = Boolean.TRUE;
            }
            throw new IllegalStateException(e);
        }finally {
            if(! keep) {
                System.out.println("don't keep , will remove");
            }
        }
        return keep;
    }
    
    public static void main(String[] args) {
        boolean ok = before();
        System.out.println(ok);
    }
    
}
