package ssa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Majors extends HashMap<Integer, Major> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Properties sqlProperties = null;
	
	static {		
		sqlProperties = DBUtils.getSqlProperties();
	}
	
	public List<Major> getAllMajors() {
		ResultSet rs = DBUtils.executeSelect(sqlProperties.getProperty("select.majors.sql"));			
		return getMajors(rs);
	}
	
	public Major getById(int id) {
		List<Major> majors = new ArrayList<Major>();
		
		ResultSet rs = DBUtils.executeSelectWhere(sqlProperties.getProperty("select.majors.sql"), "where id = ?", 
				null, id);
		majors = getMajors(rs);
				
		// Check to see whether a result exists
		if(!majors.isEmpty()) {
			return majors.get(0);
		}
		
		return null;
	}
	
	public void insertMajor(Major major) {
		DBUtils.executeUpdate(sqlProperties.getProperty("insert.majors.sql"), 
				major.getDescription(), major.getReqSat());
		major.setId(DBUtils.getLastInsertId());
	}
	
	public void updateMajor(Major major) {
		DBUtils.executeUpdate(sqlProperties.getProperty("update.majors.sql"), 
				major.getDescription(), major.getReqSat(), major.getId());
	}
	
	public void deleteById(int id) {
		DBUtils.executeUpdate(sqlProperties.getProperty("delete.majors.sql"), id);
	}
	
	public List<Major> getMajors(ResultSet rs) {
		List<Major> majors = new ArrayList<Major>();
				
		try {
			while(rs.next()) {
				majors.add(new Major(
						rs.getInt("id"), 
						rs.getString("description"),
						rs.getInt("req_sat")
						));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return majors;
	}
}
