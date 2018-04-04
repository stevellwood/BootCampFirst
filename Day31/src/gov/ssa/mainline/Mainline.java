package gov.ssa.mainline;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import gov.ssa.collections.BaseObjects;
import gov.ssa.collections.Majors;
import gov.ssa.collections.Students;
import gov.ssa.entities.BaseObject;
import gov.ssa.entities.Major;
import gov.ssa.entities.Student;

public class Mainline {

	static SessionFactory factory;
	static Session session;
	
	static {
		factory = new Configuration()
				.configure("hibernate.cfg.xml")
				.addAnnotatedClass(Major.class)
				.addAnnotatedClass(Student.class)
				.buildSessionFactory();
	}
	
	public static void main(String[] args) {
		try {
			displayAllMajors();
			
			insertTest();
						
			updateTest();
		
			deleteTest();
			
			assignMajorTest();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			factory.close();
		}
	}
	
	public static void insertTest() {
		System.out.println("Insert a new major (Computer Science, 1380) into the table\n--------------------\n");
		Major major = new Major("Computer Science", 1380);
		insertMajor(major);
		
		System.out.println("Insert a new major (English, 750) into the table\n--------------------\n");
		major = new Major("English", 750);
		insertMajor(major);
		
		displayAllMajors();
	}
	
	public static void updateTest() {		
		System.out.println("Update Education to require an SAT score of 990\n-----------------\n");
		Major major = (Major) selectMajors("from Major where id = 6").get(0);
		
		major.setReq_sat(990);
		updateMajor(major);
		
		System.out.println("Update General Studies to Undeclared\n-----------------\n");
		major = (Major) selectMajors("from Major where id = 7").get(0);
		
		major.setDescription("Undeclared");
		updateMajor(major);
		
		displayAllMajors();
	}
	
	public static void deleteTest() {
		System.out.println("Delete the Computer Science major\n-------------------------\n");
		Major major = getByDescription("Computer Science");
		
		deleteMajor(major);
		
		System.out.println("Delete the non-existent Statistics major\n--------------------\n");
		major = getByDescription("Statistics");
		
		deleteMajor(major);
		
		displayAllMajors();
	}
	
	public static void assignMajorTest() {
		System.out.println("Assign the Mathematics major to Howard Hess\n");
		
		// Get Howard Hess
		Student student = (Student) selectStudents("from Student where id = 130").get(0);
		
		// Assign the math major to Howard Hess
		assignMajor(student, "Math");
	
		System.out.println("Assign the Accounting major to Doug Dumas\n");
		
		// Get Doug Dumas
		student = (Student) selectStudents("from Student where id = 160").get(0);
		
		// Assign the accounting major to Doug Dumas
		assignMajor(student, "Accounting");
		
		displayAllStudents();
	}
	
	public static Majors selectMajors(String hql) {
		Majors majors = new Majors();
		majors.addAll(select(hql));		
		return majors;
	}
	
	public static Students selectStudents(String hql) {
		Students students = new Students();
		students.addAll(select(hql));	
		return students;
	}
	
	// Base select statement
	public static BaseObjects select(String hql) {
		BaseObjects baseObjects = new BaseObjects();
		
		session = factory.getCurrentSession();
		session.beginTransaction();
		
		baseObjects.addAll(session.createQuery(hql).list());
		
		session.getTransaction().commit();
		
		return baseObjects;
	}
	
	public static Major getByDescription(String description) {
		Majors majors = selectMajors("from Major where description = '" + description + "'");
		
		if(majors.size() > 0) {
			return (Major) majors.get(0);
		} else {
			return null;
		}
	}
	
	public static void insertMajor(Major major) {
		session = factory.getCurrentSession();
		session.beginTransaction();
		session.save(major);
		session.getTransaction().commit();
	}
	
	public static void updateStudent(Student student) {
		session = factory.getCurrentSession();
		session.beginTransaction();
		session.update(student);
		session.getTransaction().commit();
	}
	
	public static void assignMajor(Student student, String description) {
		Major major = getByDescription(description);
		
		if(major != null) {
			student.setMajor_id_fk(major);
			updateStudent(student);
		} else {
			System.out.println("Cannot update the major!\n");
		}
	}
	
	public static void updateMajor(Major major) {
		session = factory.getCurrentSession();
		session.beginTransaction();
		session.update(major);
		session.getTransaction().commit();
	}
	
	public static void deleteMajor(Major major) {		
		session = factory.getCurrentSession();
		session.beginTransaction();

		if(major != null) {
			session.delete(major);
		} else {
			System.out.println("Cannot delete a non-existent major!\n");
		}
		
		session.getTransaction().commit();
	}
	
	public static void displayAllStudents() {
		Students students = selectStudents("from Student");
		
		System.out.println("Print all students\n------------------------------\n");
		display(students);
	}

	public static void displayAllMajors() {
		Majors majors = selectMajors("from Major");
		
		System.out.println("Print all majors\n-------------------\n");
		display(majors);
	}
	
	// Polymorphism at work!
	public static void display(List<BaseObject> baseObjects) {
		for(BaseObject baseObject : baseObjects) {
			System.out.println(baseObject);
		}
		System.out.println();
	}
}
