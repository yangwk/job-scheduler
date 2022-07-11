package com.github.yangwk.jobscheduler.core.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
    
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(LogTest.class);
        log.info("abc");
        log.error("error", new RuntimeException("test ex"));
        log.debug("sql {}", "select 1");
    }
}
