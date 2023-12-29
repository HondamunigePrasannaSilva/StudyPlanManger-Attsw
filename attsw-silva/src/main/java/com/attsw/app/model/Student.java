package com.attsw.app.model;
import java.util.ArrayList;

public class Student {
	
	
	private String id;
	private String name;
	private String surname;
	private String idCdl;
	
	private ArrayList<Course> StudyPlan;
	
	public Student(String id, String name, String surname, String idCdl) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.idCdl = idCdl;
		
	}

	public Student() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	
	public ArrayList<Course> getStudyPlan() {
		return StudyPlan;
	}
	

	public void setStudyPlan(ArrayList<Course> StudyPlan) {
		this.StudyPlan = StudyPlan;
	}
		

	public void addCourse(Course course) {
		this.StudyPlan.add(course);
		 
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
