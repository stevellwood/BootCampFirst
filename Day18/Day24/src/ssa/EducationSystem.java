package ssa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class EducationSystem {
	private static java.util.Random rnd = new java.util.Random((new java.util.Date()).getTime());
	
	private static Connection conn = null;
	private static Properties properties = null;
	
	private static Classes classes = new Classes();
	
	// Would put this in another config properties file, but not this time since it's only 1 value
	public static final int NUM_CLASSES_TO_TAKE = 4;
		
	// Pre-stored information regarding the students
	String[] studentFirstNames = {"Adam", "Graham", "Ella", "Stanley", "Lou", "Brock"};
	String[] studentLastNames = {"Zapel", "Krakir", "Vader", "Kupp", "Zar", "Lee"};
	int[] studentSats = {1200, 500, 800, 1350, 950, 1500};
	double[] studentGpas = {3.0, 2.5, 3.0, 3.3, 3.0, 4.0};
	String[] studentMajors = {"Finance", "General Studies", "Accounting", "Engineering", "Education", "Computer Science"};
	
	static {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("common/sql.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public EducationSystem() throws SQLException {
		EducationSystem.conn = DBUtils.getConnection();	
				
		// Split the classes by major
		splitClasses();
		
		enrollStudents();
	}
				
	public void enrollStudents() throws SQLException {
		System.out.println("Education System - Enrollment Process");
		System.out.println("=====================================\n");
		for(int idx = 0; idx < studentFirstNames.length; idx++) {
			Student student = new Student();
			student.setFirst_name(studentFirstNames[idx]);
			student.setLast_name(studentLastNames[idx]);
			student.setSat(studentSats[idx]);
			student.setGpa(studentGpas[idx]);
			student.setMajorDescription(studentMajors[idx]);
				
			// 1) Enroll a new student
			enrollStudent(student);
			
			// 2) Assign a major to the student
			assignMajor(student, student.getMajorDescription());
			
			// 3) Sign students up for classes
			signUpStudent(student);
			
			// 4) Generate the report
			System.out.println(printStudentRecord(student));
		}
	}
	
	// Add the student to the database
	public void enrollStudent(Student student) throws SQLException {		
		PreparedStatement prepareEnroll = conn.prepareStatement(properties.getProperty("sql.enroll"));		
			
		prepareEnroll.setString(1, student.getFirst_name());
		prepareEnroll.setString(2, student.getLast_name());
		prepareEnroll.setInt(3, student.getSat());
		prepareEnroll.setDouble(4, student.getGpa());
			
		prepareEnroll.execute();
		
		// Set the id of the newly enrolled student in the student object to lessen the
		// number of database accesses
		setStudentId(student);
		
		prepareEnroll.close();
	}
	
	private void setStudentId(Student student) throws SQLException {
		PreparedStatement prepareSetId = conn.prepareStatement(properties.getProperty("sql.student.id"));
		
		prepareSetId.setString(1, student.getFirst_name());
		prepareSetId.setString(2, student.getLast_name());
		prepareSetId.setInt(3, student.getSat());
		prepareSetId.setDouble(4, student.getGpa());
		
		ResultSet rs = prepareSetId.executeQuery();
		
		// The student was just added so we know he/she exists
		rs.next();
		
		// Set the student's id
		student.setId(rs.getInt("id"));
		
		prepareSetId.close();
	}
	
	public void assignMajor(Student student, String majorDescription) throws SQLException {
		// First set the instance variables regarding the major id based on the description
		setMajorId(student, majorDescription);
		
		// Assign the major in the database only if the major id was set in the student object
		if(student.getMajor_id() != Student.NULL) {
			PreparedStatement pAssignMajor = conn.prepareStatement(properties.getProperty("sql.update.major"));
			pAssignMajor.setInt(1, student.getMajor_id());
			pAssignMajor.setInt(2, student.getId());
			
			pAssignMajor.execute();
			
			pAssignMajor.close();
		}
	}
	
	//========================================
	private void splitClasses() throws SQLException {
		PreparedStatement pListMajor = conn.prepareStatement(properties.getProperty("sql.list.major"));
		ResultSet rs = pListMajor.executeQuery();
		
		// Process each major
		while(rs.next()) {
			splitClassesByMajor(rs.getInt("id"));
		}
		
		pListMajor.close();
	}
	
	// Gets a list of the classes for the given major
	private void splitClassesByMajor(int majorId) throws SQLException {
		PreparedStatement pSplitMajor = conn.prepareStatement(properties.getProperty("sql.split.major"));
		pSplitMajor.setInt(1, majorId);
		ResultSet rs = pSplitMajor.executeQuery();
		
		ArrayList<Integer> classIds = new ArrayList<Integer>();
		
		while(rs.next()) {
			classIds.add(new Integer(rs.getInt("class_id")));
		}
		classes.put(new Integer(majorId), classIds);
				
		pSplitMajor.close();
	}
	// ============================================
	
	// Three possible cases:
	//   Case 1: The major description matched an id AND the sat requirement was met
	//         = set the student's major id to the corresponding value. intended_major_id = -1.
	//   Case 2: The major description matched an id but the sat requirement was not met
	//         = set the student's intended major id to the corresponding value. major_id = -1.
	//   Case 3: The major description did not match an id
	//         = intended_major_id = -1 and major_id = -1. (default)
	private void setMajorId(Student student, String majorDescription) throws SQLException {
		PreparedStatement pMajorId = conn.prepareStatement(properties.getProperty("sql.major.id"));
		pMajorId.setString(1, majorDescription);
		ResultSet rs = pMajorId.executeQuery();
		
		// Check to see whether the major description matched an id
		if(rs.next()) {
			// If we satisfy the sat requirement, Case 1; otherwise Case 2
			if(student.getSat() >= rs.getInt("req_sat")) {
				student.setMajor_id(rs.getInt("id"));
			} else {
				student.setIntendedMajor_id(rs.getInt("id"));
			}
		}
				
		pMajorId.close();
	}
	
	// Method to sign the student up for classes
	// - If the student's major is set to null, enroll him/her into 4 random classes
	// - If the student's major is not set to null:
	//   - Register the student for 2-4 major classes
	//   - Register the student for the remaining classes
	public void signUpStudent(Student student) throws SQLException {
		if(student.getMajor_id() == Student.NULL) {
			signUpStudentNonMajor(student, NUM_CLASSES_TO_TAKE);
		} else {
			int numMajorClasses = generateRandomNumber(NUM_CLASSES_TO_TAKE / 2, NUM_CLASSES_TO_TAKE);
			signUpStudentMajor(student, numMajorClasses);
						
			if(numMajorClasses < NUM_CLASSES_TO_TAKE) {
				// Assign the remaining classes randomly
				signUpStudentNonMajor(student, NUM_CLASSES_TO_TAKE - numMajorClasses);
			}
		}
	}
		
	// Sign the student up for at least 1/2 the classes in their major
	private void signUpStudentMajor(Student student, int numClasses) throws SQLException {
		ArrayList<Integer> classesToTake = new ArrayList<Integer>();
		
		int classIndex = 0;
		for(int idx = 1; idx <= numClasses; idx++) {
			classesToTake.add(classes.get(student.getMajor_id()).get(classIndex));			
			classIndex++;
		}
		
		registerStudentForClasses(student, classesToTake);
	}
	
	// 1) Pick a random major and pick one class from the major
	// 2) Ensure that the student has not already signed up for the class
	// 3) Sign up the student for the class
	// 4) Increment the major and mod it by the number of majors to wrap around
	private void signUpStudentNonMajor(Student student, int numClasses) throws SQLException {
		int numMajors = classes.keySet().size();
		int major = generateRandomNumber(1, numMajors);
				
		ArrayList<Integer> classesSignedUpFor = listClasses(student.getId());
		ArrayList<Integer> classesToTake = new ArrayList<Integer>();
		
		for(int idx = 1; idx <= numClasses; idx++) {
			boolean classFound = false;
			int classIndex = 0;
			do {		
				// Get the first class id from the major
				int classId = classes.get(major).get(classIndex);
			
				// Check if student is already enrolled in the class
				if(!classesSignedUpFor.contains(classId) && 
				   !classesToTake.contains(classId)) {
					classesToTake.add(classId);
					major = getNextMajor(major, numMajors);
								
					classFound = true;
				} else {
					// If the current major has no more classes left, 
					// increment to the next major
					if(++classIndex >= classes.get(major).size()) {
						major = getNextMajor(major, numMajors);
						classIndex = 0;
					}
				}
				
			} while(!classFound);
		}
		
		registerStudentForClasses(student, classesToTake);
	}
	
	
	private ArrayList<Integer> listClasses(int id) throws SQLException {
		ArrayList<Integer> classesTaken = new ArrayList<Integer>();
		
		ResultSet rs = DBUtils.processSelectQuery(properties.getProperty("sql.list.classes"), new Integer(id));
		
		while(rs.next()) {
			classesTaken.add(rs.getInt("scr.class_id"));
		}
		
		return classesTaken;
	}
	
	private int getNextMajor(int major, int numMajors) {
		// Next major from 1 to 7
		++major;
		if(major == (numMajors+1)) {
			major = 1;
		}
		
		return major;
	}
	
	private void registerStudentForClasses(Student student, ArrayList<Integer> classesSignedUpFor) throws SQLException {
		PreparedStatement pRegister = conn.prepareStatement(properties.getProperty("sql.register"));
		pRegister.setInt(1, student.getId());
		
		for(int classId : classesSignedUpFor) {
			pRegister.setInt(2, classId);
			pRegister.execute();
		}
		
		pRegister.close();
	}
	
	private int generateRandomNumber(int low, int high) {
		return rnd.nextInt(high - low + 1) + low; // from low to high inclusive
	}
	
	private String printStudentRecord(Student student) throws SQLException {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Enrolled " + student.getFullName() + " as a new student.\n").
		   append(student.getFullName() + " has an SAT score of " + student.getSat() + ".\n").toString();
		
		sb.append(printMajorInformation(student));
		
		sb.append(printClassSchedule(student));
		
		return sb.toString();
	}
	
	private String printMajorInformation(Student student) throws SQLException {
		StringBuffer sb = new StringBuffer();
		
		if(student.getMajor_id() != Student.NULL) {
			sb.append("Assigned " + student.getFullName() + " to the " + student.getMajorDescription()).
			   append(" major which requires an SAT score of ").
			   append(getMinSatScore(student.getMajor_id(), "") + ".\n");
		} else if(student.getIntendedMajor_id() != Student.NULL) {
			sb.append("Sorry, but the " + student.getMajorDescription() + " major requires an SAT score of ")
			  .append(getMinSatScore(Student.NULL, student.getMajorDescription() ) + ".\n")
			  .append("With an SAT score of " + student.getSat() + ", you may choose from the following majors:\n");
			
			ResultSet rs = DBUtils.processSelectQuery(properties.getProperty("sql.eligible.major"), student.getSat());
			if(rs.next()) {
				do {
					sb.append("* " + rs.getString("description") + " (" + rs.getInt("req_sat") + ")\n");
				} while(rs.next());
			} else {
				sb.append(student.getFullName() + " is ineligible to choose any major.\n");
			}
		} else {
			sb.append(student.getFullName() + " has not currently declared a major.\n");
		}
		
		return sb.toString();
	}
	
	private int getMinSatScore(int majorId, String description) throws SQLException {
		ResultSet rs = DBUtils.processSelectQuery(properties.getProperty("sql.major.sat"), majorId, description);
		
		// Already know there is a result - we checked earlier
		rs.next();
		
		return rs.getInt("req_sat");
	}
	
	private String printClassSchedule(Student student) throws SQLException{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Enrolled " + student.getFullName() + " in the following classes:\n");
		ResultSet rs = DBUtils.processSelectQuery(properties.getProperty("sql.list.classes"), new Integer(student.getId()));
		
		sb.append(String.format("\n%-8s  %-20s  %-5s  %-20s  %-12s\n", "CRN", "Subject", "Sec.", "Instructor", "Req for Maj?"));
		sb.append("--------  ");
		sb.append("--------------------  ");
		sb.append("-----  ");
		sb.append("--------------------  ");
		sb.append("------------\n");
		
		while(rs.next()) {
			sb.append(String.format("%-8d  %-20s  %-5d  %-20s  ", rs.getInt("scr.class_id"),
					rs.getString("c.subject"), rs.getInt("c.section"), rs.getString("name")));
			
			if(isMajorRequirement(rs.getInt("scr.class_id"), student.getMajor_id())) {
				sb.append("      Y");
			} else {
				sb.append("      N");
			}
			
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	// Class is a major requirement if the query returns a row
	private boolean isMajorRequirement(int classId, int majorId) throws SQLException {
		return DBUtils.processSelectQuery(properties.getProperty("sql.major.req"), classId, majorId).next();
	}
	
//	public String enrollStudent(Student student) throws SQLException {
//		pState = conn.prepareStatement(properties.getProperty("sql.enroll"));
//				
//		pState.setString(1, student.getFirst_name());
//		pState.setString(2, student.getLast_name());
//		pState.setInt(3, student.getSat());
//		pState.setDouble(4, student.getGpa());
//
//		int major_id = getMajorId(student.getMajorDescription(), student.getSat());
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append("Enrolled " + student.getFullName() + " as a new student.\n");
//		sb.append(student.getFullName() + " has an SAT score of " + student.getSat() + ".\n");
//		
//		if(major_id == NOT_FOUND || major_id == LOW_SAT) {
//			pState.setNull(5, java.sql.Types.INTEGER);
//			if(major_id == NOT_FOUND) {
//				sb.append("The major selected by " + student.getFullName() + " was not found.\n");
//			} else {
//				sb.append("Sorry, but a " + student.getMajorDescription() + " major requires an SAT score of ")
//				  .append(getMinSatScore(-1, student.getMajorDescription() ) + ".\n");
//			}
//		} else {
//			pState.setInt(5,  major_id);
//			sb.append("Assigned " + student.getFullName() + " to the " + student.getMajorDescription() + " major ")
//			  .append("which requires an SAT score of " + getMinSatScore(major_id, "") + ".\n");
//		}
//		pState.executeUpdate();
//
//		getStudentId(student);
//		
//		/*
//		if(major_id > 0) {
//			
//			pState = conn.prepareStatement(properties.getProperty("sql.list.major.classes"));
//			pState.setInt(1, major_id);
//			ResultSet rs = pState.executeQuery();
//			
//			// Guaranteed to be at least 2...
//			rs.next();
//			int classId1 = rs.getInt("class_id");
//			int classId2 = rs.getInt("class_id");
//			
//			// Register the student
//			pState = conn.prepareStatement(properties.getProperty("sql.register.student"));
//		} else {
//			for(int idx = 1; idx <= 4; idx++) {
//				
//			}
//		}
//		*/
//		return sb.toString();
//	}
//	
//
//	
//	// Returns a valid major id based on the description or -1 if the major is not found OR
//	// -2 if the minimum SAT score requirement is not met 
//	private int getMajorId(String description, int sat) throws SQLException {
//		int major_id = NOT_FOUND;
//		PreparedStatement pStateMajor = conn.prepareStatement(properties.getProperty("sql.major.id"));
//		pStateMajor.setString(1, description);
//		ResultSet rs = pStateMajor.executeQuery();
//		
//		// Check if the major was found
//		if(rs.next()) {
//			// Check the sat requirement
//			if(rs.getInt("req_sat") > sat) {
//				major_id = LOW_SAT;
//			} 
//			// Set the major_id otherwise
//			else {			
//				major_id = rs.getInt("id");
//			}
//		}
//		
//		pStateMajor.close();
//		return major_id;
//	}
//	
//	private int getMinSatScore(int id, String description) throws SQLException {
//		PreparedStatement pStateMinSat = conn.prepareStatement(properties.getProperty("sql.major.sat"));
//		pStateMinSat.setInt(1, id);
//		pStateMinSat.setString(2, description);
//		ResultSet rs = pStateMinSat.executeQuery();
//		
//		// Query is guaranteed to have a result since either the major code or 
//		// description is present
//		rs.next();
//		
//		int minSat = rs.getInt("req_sat");
//		pStateMinSat.close();
//		
//		return minSat;		
//	}
//	
//	private void getStudentId(Student student) throws SQLException {
//		PreparedStatement pStateId = conn.prepareStatement(properties.getProperty("sql.student.id"));
//		pStateId.setString(1, student.getFirst_name());
//		pStateId.setString(2, student.getLast_name());
//		pStateId.setDouble(3, student.getGpa());
//		pStateId.setInt(4,  student.getSat());
//		
//		ResultSet rs = pStateId.executeQuery();
//		rs.next();
//		student.setId(rs.getInt("id"));
//	}
//	
//	private int getRandomClass() {
//		
//		int randomInt = rnd.nextInt(4) + 1; // 1 - 4
//		return randomInt;
//	}
}
