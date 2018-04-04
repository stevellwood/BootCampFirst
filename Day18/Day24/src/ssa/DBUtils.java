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
import java.util.Properties;

public class DBUtils {
	private static Connection connection = null;
	public static Properties properties = null;
	
	public static final String DB_NAME = "tiy2";
	
	static {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("common/connection.properties"));			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		String dbUrl = properties.getProperty("dburlStart") + properties.getProperty("db", DB_NAME) +
				properties.getProperty("dburlEnd");
		String user = properties.getProperty("user");		
		String pass = properties.getProperty("password");
		try {
			connection = DriverManager.getConnection(dbUrl, user, pass);					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
	}
	
	public static ResultSet processSelectQuery(String sql, Object... params) throws SQLException {
		PreparedStatement pState = connection.prepareStatement(sql);
		
		for(int idx = 0; idx < params.length; idx++) {
			if(params[idx] instanceof Integer) {
				pState.setInt(idx + 1, (Integer) params[idx]);
			} else if (params[idx] instanceof Double) {
				pState.setDouble(idx + 1, (Double) params[idx]);
			} else if (params[idx] instanceof String) {
				pState.setString(idx + 1, (String) params[idx]);
			}
		}
				
		return pState.executeQuery();
	}
	
	public static void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
