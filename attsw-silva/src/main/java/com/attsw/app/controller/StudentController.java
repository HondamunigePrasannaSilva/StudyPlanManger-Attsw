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
		if (student == null)
			return null;
		
		ArrayList<Course> studyPlan = studentRepository.getStudyPlan(student.getId());
		student.setStudyPlan(studyPlan);
		
		return student;
	}

	public void insertCourseIntoStudyPlan(Student student, Course course) {
		
		if (student.getStudyPlan().stream()
				.filter(c -> c.getCourseId() == course.getCourseId())
				.count() > 0)
			return ;
        

		
		if (courseRepository.findById(course.getCourseId()) == null)
			return;
		
		student.addCourse(course);
		
	}

	public void removeCourseFromStudyPlan(Student student, Course course) {
		
		ArrayList<Course> sp = student.getStudyPlan();
				
		if(sp.removeIf(c -> c.getCourseId() == course.getCourseId()))	
			studentRepository.updateStudyPlan(student);
		else
			throw new IllegalArgumentException("Course not in study plan");
		
		
	}

	public void updateStudyPlan(Student student, Course course) {
		
		ArrayList<Course> sp = student.getStudyPlan();
		
		if(sp.removeIf(c -> c.getCourseId().equals(course.getCourseId())))
		{
			student.addCourse(course);
			studentRepository.updateStudyPlan(student);
		}
			
		
	}

	
	

}
