package com.github.yangwk.jobscheduler.core.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcHelper {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcHelper.class);
    
    private final ConnectionHolder connectionHolder;
    
    public JdbcHelper(DataSource dataSource) {
        this.connectionHolder = new ConnectionHolder(dataSource);
    }
    
    
    public void beginTransaction(){
        try {
            Connection conn = connectionHolder.get();
            if(conn.getAutoCommit()) {
                conn.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("begin transaction error", e);
        }
    }
    
    
    public void commitTransaction(){
        try{
            Connection conn = connectionHolder.get();
            conn.commit();
        }catch(SQLException e){
            throw new IllegalStateException("commit transaction error", e);
        }
    }
    
    
    public void rollbackTransaction(){
        try{
            Connection conn = connectionHolder.get();
            conn.rollback();
        }catch(SQLException e){
            throw new IllegalStateException("rollback transaction error", e);
        }
    }
    
    // in finally
    public void recover() {
        try {
            Connection conn = connectionHolder.get();
            if(!conn.getAutoCommit()) {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("recover Connection error", e);
        } finally {
            connectionHolder.close();
        }
    }
    
    
    public int update(String sql, Object... paras){
        int result = -1;
        try {
            Connection conn = connectionHolder.get();
            try(PreparedStatement pst = conn.prepareStatement(sql);){
                for (int i=0; paras !=null && i<paras.length; i++) {
                    pst.setObject(i + 1, paras[i]);
                }
                result = pst.executeUpdate();
            } 
        }catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("update sql: {}", sql);
        }
        return result;
    }
    
    public List<Map<String,Object>> query(String sql, Integer limit, Map<String, String> labelMapping, Object... paras) {
        List<Map<String,Object>> result = new ArrayList<>();
        try{
            Connection conn = connectionHolder.get();
            try(PreparedStatement pst = conn.prepareStatement(sql);){
                for (int i=0; paras !=null && i<paras.length; i++) {
                    pst.setObject(i + 1, paras[i]);
                }
                if(limit != null) {
                    pst.setMaxRows(limit);
                }
                try(ResultSet rs = pst.executeQuery();){
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();
                    String[] columnLabels = new String[columnCount];
                    int[] columnTypes = new int[columnCount];
                    for (int i=0; i<columnCount; i++) {
                        String label = rsmd.getColumnLabel(i + 1);
                        if(labelMapping != null) {
                            label = labelMapping.getOrDefault(label, label);
                        }
                        columnLabels[i] = label;
                        columnTypes[i] = rsmd.getColumnType(i + 1);
                    }
                    while (rs.next()) {
                        Map<String,Object> dataMap = new HashMap<>();
                        for (int col=0; col<columnCount; col++) {
                            dataMap.put(columnLabels[col], getObject(rs,columnTypes[col],col + 1) );
                        }
                        result.add(dataMap);
                    }
                }
            }
        }catch(SQLException e){
            throw new IllegalStateException(e);
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("query sql: {}", sql);
        }
        return result;
    }
    
    private Object getObject(ResultSet rs, int columnType, int column) throws SQLException{
        Object value = null;
        if (columnType == Types.BLOB 
                || columnType == Types.BINARY || columnType == Types.VARBINARY || columnType == Types.LONGVARBINARY ) {
            value = handleBlob(rs.getBlob(column));
        }else {
            value = rs.getObject(column);
        }
        return value;
    }
    
    
    private byte[] handleBlob(Blob blob) throws SQLException {
        if (blob == null)
            // XXX BeanUtilsBean can not populate null for byte[]
            return new byte[0];
        
        try(InputStream is = blob.getBinaryStream();) {
            if (is == null)
                return null;
            long len = blob.length();
            if(len > Integer.MAX_VALUE) {
                throw new IllegalStateException("blob too large");
            }
            byte[] data = new byte[(int)len];
            is.read(data);
            return data;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
