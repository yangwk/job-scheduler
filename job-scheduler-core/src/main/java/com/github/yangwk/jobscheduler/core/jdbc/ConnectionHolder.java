package com.github.yangwk.jobscheduler.core.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

final class ConnectionHolder {
	private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
	
	private final DataSource dataSource;
	
	public ConnectionHolder(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource);
	}
	
	
	public Connection get(){
		Connection threadLocalConn = threadLocal.get();
		if(threadLocalConn == null){
			try {
                threadLocalConn = dataSource.getConnection();
            } catch (SQLException e) {
                throw new IllegalStateException("get Connection error");
            }
			threadLocal.set(threadLocalConn);
		}
		return threadLocalConn;
	}
	
	public void close() {
		Connection threadLocalConn = threadLocal.get();
		threadLocal.remove();
		
		if(threadLocalConn != null) {
		    try { 
	            threadLocalConn.close(); 
	        } catch (SQLException e) { 
	            throw new IllegalStateException("Connection close error", e); 
	        }
		}
		
	}
	
}



