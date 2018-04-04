package ssa;

// Allows a blank Student object to be created. Values can
// then be set before sending to the DB.
public class Student {
	public static final int NULL = -1;
	
	private int id;
	private String first_name;
	private String last_name;
	private int sat;
	private double gpa;
	
	private int intended_major_id = NULL;
	private int major_id = NULL;
	private String major_description;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public String getFullName() {
		return getFirst_name() + " " + getLast_name();
	}
	
	public int getSat() {
		return sat;
	}
	
	// Ensure that SAT is between 400 and 1600
	public boolean setSat(int sat) {
		if(sat >= 400 && sat <= 1600) {		
			this.sat = sat;
			return true;
		} 
					
		return false;	
	}
	
	public double getGpa() {
		return gpa;
	}
	
	// Ensure that GPA is between 0 and 5
	public boolean setGpa(double gpa) {
		if(gpa >= 0.0 && gpa <= 5.0) {
			this.gpa = gpa;
			return true;
		}
		
		return false;
	}
	
	public int getIntendedMajor_id() {
		return intended_major_id;
	}
	
	public void setIntendedMajor_id(int major_id) {
		this.intended_major_id = major_id;
	}
	
	public int getMajor_id() {
		return major_id;
	}
	
	public void setMajor_id(int major_id) {
		this.major_id = major_id;
	}
	
	public String getMajorDescription() {
		return major_description;
	}
	
	public void setMajorDescription(String major_description) {
		this.major_description = major_description;
	}
}
