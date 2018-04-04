package ssa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class Students extends HashMap<Integer, Student> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Properties sqlProperties = null;
	
	static {		
		sqlProperties = DBUtils.getSqlProperties();
	}
	
	public List<Student> getAllStudents() {
		ResultSet rs = DBUtils.executeSelect(sqlProperties.getProperty("select.students.sql"));			
		return getStudents(rs);
	}
	
	public Student getById(int id) {
		List<Student> students = new ArrayList<Student>();
		
		ResultSet rs = DBUtils.executeSelectWhere(sqlProperties.getProperty("select.students.sql"), "where id = ?", 
				null, id);
		students = getStudents(rs);
				
		// Check to see whether a result exists
		if(!students.isEmpty()) {
			return students.get(0);
		}
		
		return null;
	}
	
	public List<Student> getStudents(ResultSet rs) {
		List<Student> students = new ArrayList<Student>();
				
		try {
			while(rs.next()) {
				students.add(new Student(
						rs.getInt("id"), 
						rs.getString("first_name"),
						rs.getString("last_name"),
						rs.getInt("sat"), 
						rs.getDouble("gpa")
						));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return students;
	}
	
	public void insertStudent(Student student) {
		DBUtils.executeUpdate(sqlProperties.getProperty("insert.students.sql"), 
				student.getFirstName(), student.getLastName(), student.getSat(), student.getGpa());
		student.setId(DBUtils.getLastInsertId());
	}
	
	public void updateStudent(Student student) {
		DBUtils.executeUpdate(sqlProperties.getProperty("update.students.sql"), 
				student.getFirstName(), student.getLastName(), student.getSat(), student.getGpa(), 
				student.getId());
	}
	
	public void deleteById(int id) {
		DBUtils.executeUpdate(sqlProperties.getProperty("delete.students.sql"), id);
	}
}
