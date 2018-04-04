package gov.ssa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="major")
public class Major extends BaseObject {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="description")
	private String description;
	
	@Column(name="req_sat")
	private int req_sat;

	public Major() {}
	
	public Major(String description, int req_sat) {
		super();
		this.description = description;
		this.req_sat = req_sat;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getReq_sat() {
		return req_sat;
	}

	public void setReq_sat(int req_sat) {
		this.req_sat = req_sat;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%-2d   %-20s   %4d", getId(), getDescription(), getReq_sat()));
		return sb.toString();
	}
}
