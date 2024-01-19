
package com.attsw.app.repository;

import java.util.List;

import com.attsw.app.model.Student;

public interface StudentRepository {
	
	public List<Student> findAll();
	
	public Student findById(String id);
		
	public void updateStudyPlan(Student student);
	

}
