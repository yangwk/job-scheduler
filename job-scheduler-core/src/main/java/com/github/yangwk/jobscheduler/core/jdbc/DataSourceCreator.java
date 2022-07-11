package com.github.yangwk.jobscheduler.core.jdbc;

import java.util.Properties;

import javax.sql.DataSource;

import com.github.yangwk.jobscheduler.core.impl.JobSchedulerConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceCreator {

	private Properties convert(Properties datasource){
		Properties targetProp = new Properties();
		final String prefix = "job-scheduler.datasource.hikari.";
		datasource.forEach((k,v) -> {
		    String key = k.toString();
		    if(key.startsWith(prefix)) {
		        targetProp.put(key.substring(prefix.length()), v);
		    }
		});
		return targetProp;
	}
	
	public DataSource getDataSource(){
	    Properties prop = convert(JobSchedulerConfig.getDatasource());
	    HikariConfig config = new HikariConfig(prop);
	    HikariDataSource dataSource = new HikariDataSource(config);
	    return dataSource;
	}
	
	
}
