package com.wemarklinks.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.wemarklinks.jdbcUtil.DBUtil;
import com.wemarklinks.pojo.Record;

public class RecordDao implements Serializable {
    
    private static final long serialVersionUID = 5897996051157844783L;
    
    public boolean saveRecord(Record record) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            String sql = "    insert into record (userId, status, time, name) values (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,record.getUserId());
            /*
             * setInt(),setDouble() 
             * 不允许传入null值
             * 但是实际业务中该字段却可能为null值
             * 并且数据库也支持null值,
             * 可以将这样的字段当作object处理 
             * setObject()
             * */
            ps.setString(2, record.getStatus());
            ps.setTimestamp(3, record.getTime());
            ps.setString(4, record.getName());
            if(ps.executeUpdate()>0){
                conn.commit();  // -这里没有提交,找了好久的错 - - --- -
                return true;
            }else{
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            DBUtil.rollback(conn);
            return false;
        } finally {
            DBUtil.close(conn);
        }
    }
    
    public List<Record> selectBySelective(Map<String, Object> map){
        
        
        
        return null;
    }
    
    public int countBySelective(Map<String, Object> map){
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "  select count(*) from record ";
            String name = map.get("name").toString();
            Long startTime = (Long)map.get("startTime");
            Long endTime = (Long)map.get("endTime");
//            if(name != null || startTime != null){
//                sql = sql + "  where ";
//                if(name != null && startTime != null){
//                    sql = sql + " name = ? ";
//                    sql = sql + " and "+" time between ? and  ? ";
//                }else if(name != null){
//                    sql = sql + " name = ? ";
//                }else if(startTime != null){
//                    sql = sql + " time between ? and  ? ";
//                }
//            }
            sql = sql + getSql(map, sql);
            PreparedStatement ps = conn.prepareStatement(sql);
            if(name != null && startTime != null){
                ps.setString(1, name);
                ps.setTimestamp(2, new Timestamp(startTime));
                ps.setTimestamp(3, new Timestamp(endTime));
            }else if(startTime != null){
                ps.setTimestamp(1, new Timestamp(startTime));
                ps.setTimestamp(2, new Timestamp(endTime));
            }else if(name != null ){
                ps.setString(1, name);
            }
//            ResultSet resultSet = ps.executeQuery();
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn);
        }
        return 0;
    }
    private String getSql(Map<String, Object> map, String sql){
        String name = map.get("name").toString();
        Long startTime = (Long)map.get("startTime");
        if(name != null || startTime != null){
            sql = sql + "  where ";
            if(name != null && startTime != null){
                sql = sql + " name = ? ";
                sql = sql + " and "+" time between ? and  ? ";
            }else if(name != null){
                sql = sql + " name = ? ";
            }else if(startTime != null){
                sql = sql + " time between ? and  ? ";
            }
        }
        return sql;
    }
}
