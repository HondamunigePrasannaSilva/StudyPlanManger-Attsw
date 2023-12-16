package com.attsw.app.repository;

import java.util.List;

import com.attsw.app.model.Course;


public interface CourseRepository {

	public List<Course> findAll();
	
	public Course findById(String id);
	
}
