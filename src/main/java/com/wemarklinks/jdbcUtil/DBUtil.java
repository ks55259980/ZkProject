package com.wemarklinks.jdbcUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.dbcp.BasicDataSource;

public class DBUtil {
	// 连接池对象，由DBCP提供的类BasicDataSource，实现了DataSource
	//class BasicDataSource implements DataSource
	private static BasicDataSource ds;
	
	//初始化
	static {
		//1：p中存储的都是STRING的key和value
		Properties p = new Properties();
		try {
			//2：加载配置文件
			p.load(DBUtil.class.getClassLoader().getResourceAsStream("db.properties"));
			//3：读取参数,
			String driver = p.getProperty("driver");
			String url = p.getProperty("url");
			String user = p.getProperty("user");
			String pwd = p.getProperty("pwd");
			String initsize = p.getProperty("initsize");
			String maxsize = p.getProperty("maxsize");
			
			//4：创建连接池
			ds = new BasicDataSource();
			
			//5:设置参数
			ds.setDriverClassName(driver);   //连接池.setDriverClassName(driver),注册驱动
			ds.setUrl(url);                  //路径 连接池使用url,user,pwd创建连接
			ds.setUsername(user);            //帐号
			ds.setPassword(pwd);             //密码 
			ds.setInitialSize(new Integer(initsize));  //池内最小连接数量
			ds.setMaxActive(new Integer(maxsize));     //池内最大连接数量
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("加载db.properties失败：",e);
		};
		
	}
	
	//获取连接
	public static Connection getConnection() throws SQLException{
		//抛出异常提醒调用者写finally关闭池
		return ds.getConnection();
	}
	
	//关闭连接
	/**
	 * 由BasicDataSource创建的Connection，
	 *   该Connection的close()被BasicDataSource重写
	 *   即：连接池会将连接的状态设置为空闲，
	 *       并清空连接中包含的任何数据
	 * @param conn
	 */
	public static void close(Connection conn){
		//归还连接给连接池
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("归还Connection失败",e);
			}
		}
	}
	
	/**
	 * 
	 */
	public static void rollback(Connection conn){
		if(conn!=null)
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("回滚失败",e);
			}
	}
	
}

















