package ssa;

public class Student {
	// Table fields of Student
	private int id;
	private String firstName;
	private String lastName;
	private int sat;
	private double gpa;
	private Major majorId;
	
	public Student() { }
	
	public Student(String firstName, String lastName, int sat, double gpa) {
		this(-1, firstName, lastName, sat, gpa);
	}
	
	public Student(int id, String firstName, String lastName, int sat, double gpa) {
		setId(id);
		setFirstName(firstName);
		setLastName(lastName);
		setSat(sat);
		setGpa(gpa);
	}
		
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getSat() {
		return sat;
	}
	public void setSat(int sat) {
		if(sat >= 400 && sat <= 1600) {
			this.sat = sat;
		}
	}
	public double getGpa() {
		return gpa;
	}
	public void setGpa(double gpa) {
		this.gpa = gpa;
	}
	
	public Major getMajorId() {
		return majorId;
	}

	public void setMajorId(Major majorId) {
		if(getSat() >= majorId.getReqSat()) {
			this.majorId = majorId;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(String.format("%-6d   %-15s   %-15s   %4.2f   %4d", getId(), getFirstName(), getLastName(),
				getGpa(), getSat()));
		
		return sb.toString();
	}
}
