package com.newland.iotshow.util;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.newland.iotshow.conf.Constant;

/**
 * 数据库连接管理
 * @author sj
 *
 */
public class DBConnectionManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(DBConnectionManager.class);
	
	private static DruidDataSource druidDataSource = null;
	
	private DBConnectionManager() {
		System.out.println("init DBConnection");
		Properties properties = new Properties();
		InputStream is = DBConnectionManager.class.getClassLoader().getResourceAsStream(Constant.DB_CONF_FILE);
		try {
			properties.load(is);
			druidDataSource = (DruidDataSource)DruidDataSourceFactory.createDataSource(properties); 
		} catch (Exception e) {
			LOG.error("init db connection fail",e);
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 获取一个连接
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException{
		return druidDataSource.getConnection();
	}
	
	/**
	 * 数据库连接池单例
	 * @return
	 */
	public static DBConnectionManager getInstance(){
		return DBConnectionManagerHolder.instance;
	}
	
	private static class DBConnectionManagerHolder{
		private static DBConnectionManager instance = new DBConnectionManager();
	}
}
