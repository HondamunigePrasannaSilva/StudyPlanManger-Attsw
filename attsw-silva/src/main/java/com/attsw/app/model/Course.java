package com.attsw.app.model;

public class Course {

	
	
	private String courseName;
	private String courseId;
	private int cfu;
	

	public Course(String courseId, String courseName, int cfu) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.cfu = cfu;
		
	}

	public String getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}
	
	public int getCfu() {
		return cfu;
	}
	
	@Override
	public String toString() {
		return "Course: " + courseId + " " + courseName + " " + cfu;
	}
	
	
	
}
