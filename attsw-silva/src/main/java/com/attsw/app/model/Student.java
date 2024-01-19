package com.attsw.app.model;
import java.util.List;

public class Student {
	
	
	private String id;
	private String name;
	private String surname;
	private String idCdl;
	
	private List<Course> studyPlan;
	
	public Student(String id, String name, String surname, String idCdl) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.idCdl = idCdl;
		
	}



	public String getId() {
		return id;
	}

	
	public List<Course> getStudyPlan() {
		return studyPlan;
	}
	

	public void setStudyPlan(List<Course> studyPlan) {
		this.studyPlan = studyPlan;
	}
		

	public void addCourse(Course course) {
		this.studyPlan.add(course);
		 
	}

	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public String getIdCdl() {
		return idCdl;
	}
	
	
	

}
