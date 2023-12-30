package com.attsw.app.controller;

import java.util.ArrayList;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.CourseRepository;
import com.attsw.app.repository.StudentRepository;


 public class StudentController {

	private StudentRepository studentRepository;
	private CourseRepository courseRepository;

	public StudentController(StudentRepository studentRepository, CourseRepository courseRepository) {
		
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
	}

	public Student find(String studentId) {
		
		Student student = studentRepository.findById(studentId);
		if (student != null)
			return student;
		else
			throw new IllegalArgumentException("Student not found");
		
		// deve chiamare la view
		
	}

	public void insertCourseIntoStudyPlan(Student student, Course course) {
		
		if (courseRepository.findById(course.getCourseId()) == null)
			throw new IllegalArgumentException("Course not in repository");
		
		if (student.getStudyPlan().stream()
				.filter(c -> c.getCourseId() == course.getCourseId())
				.count() > 0)
			return ;
		
		student.addCourse(course);
		studentRepository.updateStudyPlan(student);
		// deve chiamare anche la view 
		
	}

	public void removeCourseFromStudyPlan(Student student, Course course) {
		
		ArrayList<Course> sp = student.getStudyPlan();
				
		if(sp.removeIf(c -> c.getCourseId() == course.getCourseId()))	
			studentRepository.updateStudyPlan(student);
		else
			throw new IllegalArgumentException("Course not in study plan");
		
		// deve chiama la view
	}

	/*public void updateStudyPlan(Student student, Course course) {
		
		ArrayList<Course> sp = student.getStudyPlan();
		
		if(sp.removeIf(c -> c.getCourseId().equals(course.getCourseId())))
		{
			student.addCourse(course);
			studentRepository.updateStudyPlan(student);
		}
			
		
	}*/

	
	

}
