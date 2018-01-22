package com.wemarklinks.jdbcUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBTool {
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;
	
	//只在开始时读取一次参数
	static {
		Properties p = new Properties();
		try {
			//一：加载配置文件
			p.load(DBTool.class.getClassLoader().getResourceAsStream("db.properties"));
			//读取四个连接参数,通过key取value
			driver = p.getProperty("driver");
			url = p.getProperty("url");
			user = p.getProperty("user");
			pwd = p.getProperty("pwd");
			//二：注册驱动
			Class.forName(driver);
			
			
		} catch (IOException e) {
			//TODO :将来要做的要完成的事
			e.printStackTrace();
			throw new RuntimeException("加载db.properties失败",e);
		} catch (ClassNotFoundException e) {
			    //无法找到该类的异常，一：驱动写错了。二：包找不着.
			e.printStackTrace();
			throw new RuntimeException("找不到这个驱动",e);
		}
	}
	
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(url,user,pwd);
		//强制抛异常，提醒调用者try catch，避免调用者忘记
	}
	
	//关闭连接的方法
	public static void close(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭连接失败",e);
			}	
		}
	}
	
	
	
}









