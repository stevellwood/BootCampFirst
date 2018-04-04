package ssa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBUtils {
	private static Connection conn = null;
	private static PreparedStatement pState = null;
	private static Properties connProperties = null;
	private static Properties sqlProperties = null;
	
	static {
		try {
			connProperties = new Properties();
			sqlProperties = new Properties();
			
			connProperties.load(new FileInputStream("common/connection.properties"));
			sqlProperties.load(new FileInputStream("common/sql.properties"));
			
			String dbUrl = connProperties.getProperty("dbUrlStart") + connProperties.getProperty("db", "tiy2") +
					connProperties.getProperty("dbUrlEnd");
			String user = connProperties.getProperty("user");
			String pass = connProperties.getProperty("pass");
			
			conn = DriverManager.getConnection(dbUrl, user, pass);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static Connection getConnection() {
		return conn;
	}
	
	public static Properties getSqlProperties() {
		return sqlProperties;
	}
	
	public static ResultSet executeSelect(String sql, Object... params) {
		ResultSet rs = null;
		
		try {
			prepareSqlStatement(sql, params);
			rs = pState.executeQuery();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
				
		return rs;
	}
	
	public static ResultSet executeSelectWhere(String sql, String where, String orderBy, Object...params) {
		if(where != null && orderBy != null) {
			return executeSelect(sql + where + orderBy, params);
		} else if(where == null && orderBy == null) {
			return executeSelect(sql, params);
		} else if(orderBy == null) {
			return executeSelect(sql + where, params);
		} else {
			return executeSelect(sql + orderBy, params);
		}
	}
			
	public static void executeUpdate(String sql, Object...params) {
		try {
			prepareSqlStatement(sql, params);
			pState.execute();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void prepareSqlStatement(String sql, Object... params) {
		try {	
			pState = conn.prepareStatement(sql);
		
			for(int idx = 1; idx <= params.length; idx++) {
				pState.setObject(idx, params[idx-1]);
			}
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	// Gets the last inserted id
	public static int getLastInsertId() {
		ResultSet rs = executeSelect(sqlProperties.getProperty("last.id.students"));
		int lastId = 0;
		
		// Will always get a value back
		try {
			rs.next();
			lastId = rs.getInt("id");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
			
		return lastId;
	}
	
	public static void close() {
		closeConn();
		closePState();
	}
	
	public static void closeConn() {	
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) { }
		}
	}
	
	private static void closePState() {
		if(pState != null) {
			try {
				pState.close();
			} catch (SQLException e) { }
		}
	}
}
