package ssa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Mainline {

	static List<Student> studentCollection = new ArrayList<Student>();
	static List<Major> majorCollection = new ArrayList<Major>();
	
	static Student student = new Student();
	static Major major = new Major();
	
	static Students students = new Students();
	static Majors majors = new Majors();
	
	public static void main(String[] args) throws SQLException {
		// Retrieve a single student with id = 1 - will fail!
		retrieveByIdTest(1);
		
		// Retrieve a single student with id = 193
		retrieveByIdTest(193);
		
	    // Retrieve all students
		retrieveAllStudentsTest();
			    
	    // Insert a student
		insertStudentTest();
		
		// Retrieve all majors
	    // retrieveAllMajorsTest();
		
	    // Update the newly inserted student
		updateStudentTest();
			    
	    // Delete the recently inserted student
		deleteStudentTest();
			    
	    // Retrieve all students with gpas between 2.0 and 2.9 unordered
		whereUnorderedTest();
			    
	    // Retrieve all students with gpas between 2.0 and 2.9 in ascending order
		whereOrderedAscTest();
		
	    // Retrieve all students with gpas between 2.0 and 2.9 in descending order
	    whereOrderedDescTest();
	    
	    // Retrieve all students and order them by gpa asc
	    orderedByOnlyTest();
	   	    
	    // Update major test
	    updateMajorTest();
	    
	    
		DBUtils.close();
	}
	
	public static void retrieveByIdTest(int id) {
	    // retrieve a single student
	    student = students.getById(id);
	    
	    System.out.println("Retrieve by id = " + id + "\n--------------");
	    
	    // display the student	    
	    if(student != null) {
	    	System.out.println(student + "\n"); // displays the student data in a formatted way
	    } else {
	    	System.out.println("Student does not exist!");
	    }
	}
	
	public static void retrieveAllStudentsTest() {
		// retrieve all the students into a collection
	    studentCollection = students.getAllStudents();
	    
	    System.out.println("Retrieve all students\n-----------------------");
	    printStudent(studentCollection);
	}
	
	public static void retrieveAllMajorsTest() {
		// retrieve all the majors into a collection
	    majorCollection = majors.getAllMajors();
	    
	    System.out.println("Retrieve all majors\n-----------------------");
	    printMajor(majorCollection);
	}
	
	public static void insertStudentTest() {
		student = new Student("Hercule", "Poirot", 1600, 4.0);
	    students.insertStudent(student);
	    studentCollection = students.getAllStudents();
	    
	    System.out.println("Insert a student\n---------------------");
	    printStudent(studentCollection);
	}
	
	public static void updateStudentTest() {
		student.setSat(1325);
	    student.setGpa(3.8);
	    students.updateStudent(student);
	    studentCollection = students.getAllStudents();
	    
	    System.out.println("Update a student\n---------------------");
	    printStudent(studentCollection);
	}
	
	public static void deleteStudentTest() {
		students.deleteById(student.getId());	    
	    studentCollection = students.getAllStudents();
	    
	    System.out.println("Delete a student\n---------------------");
	    printStudent(studentCollection);
	}
	
	public static void whereUnorderedTest() {
		studentCollection = students.getStudents(DBUtils.executeSelectWhere("select * from student ", 
	    		"where gpa between 2.0 and 2.9 ", null));
		
		System.out.println("Test the where clause without orderby\n---------------------");
		printStudent(studentCollection);
	}
	
	public static void whereOrderedAscTest() {
		studentCollection = students.getStudents(DBUtils.executeSelectWhere("select * from student ", 
	    		"where gpa between 2.0 and 2.9 ", "order by gpa"));
		
		System.out.println("Test the where clause and order by asc\n-------------------------");
		printStudent(studentCollection);
	}
	
	public static void whereOrderedDescTest() {
		studentCollection = students.getStudents(DBUtils.executeSelectWhere("select * from student ", 
	    		"where gpa between 2.0 and 2.9 ", "order by gpa"));
		
		System.out.println("Test the where clause and order by desc\n------------------------");
		printStudent(studentCollection);
	}
	
	public static void orderedByOnlyTest() {
		studentCollection = students.getStudents(DBUtils.executeSelectWhere("select * from student ", 
	    		null, "order by gpa"));
		
		System.out.println("Test the orderby clause with no where clause\n------------------------");
		printStudent(studentCollection);
	}
	
	public static void updateMajorTest() {
		
	}

	public static void printStudent(List<Student> students) {
		for(Student student : students) {
			System.out.println(student);
		}
		System.out.println();
	}
	
	public static void printMajor(List<Major> majors) {
		for(Major major : majors) {
			System.out.println(major);
		}
		System.out.println();
	}
	
	
}
