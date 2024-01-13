package com.attsw.app.controller;

import java.util.ArrayList;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.CourseRepository;
import com.attsw.app.repository.StudentRepository;
import com.attsw.app.view.StudyPlanView;


 public class StudentController {

	private StudentRepository studentRepository;
	private CourseRepository courseRepository;
	private StudyPlanView studyPlanView;

	public StudentController(StudentRepository studentRepository, CourseRepository courseRepository, StudyPlanView studyPlanView) {
		
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.studyPlanView = studyPlanView;
	}

	public Student find(String studentId) {
		
		Student student = studentRepository.findById(studentId);

		if (student == null)
			studyPlanView.showError("Student not found");
		else
			studyPlanView.showStudyPlan(student.getStudyPlan());
		
		return student;
	}

	public Student insertCourseIntoStudyPlan(Student student, Course course) {
		
		if (courseRepository.findById(course.getCourseId()) == null)
			studyPlanView.showError("Course not found");
		
		if (student.getStudyPlan().stream()
				.filter(c -> c.getCourseId() == course.getCourseId())
				.count() > 0)
			return null;
		
		student.addCourse(course);
		studentRepository.updateStudyPlan(student);
		studyPlanView.CourseAdded(course);
		
		return student;
		
	}

	public void removeCourseFromStudyPlan(Student student, Course course) {
		
		ArrayList<Course> sp = student.getStudyPlan();

		if(sp.removeIf(c -> c.getCourseId() == course.getCourseId()))
			studentRepository.updateStudyPlan(student);
		else
			studyPlanView.showError("Course not in study plan");

	}

	public Student updateStudyPlan(Student student, Course course1, Course course2) {
		
		Course courseToAdd = courseRepository.findById(course2.getCourseId());
		if (courseToAdd == null)
		{
			studyPlanView.showError("Course not found");
			return student;
		}
		ArrayList<Course> sp = student.getStudyPlan();

		if(sp.removeIf(c -> c.getCourseId() == course1.getCourseId()))	
		{	
			student.addCourse(courseToAdd);		
			
			studentRepository.updateStudyPlan(student);
			studyPlanView.CourseRemoved(course1);
			studyPlanView.CourseAdded(courseToAdd);
			
		}
		
		return studentRepository.findById(student.getId());
		
	}
	
	public Course findCourseByNameAndCfu(String name, int cfu) {
		return courseRepository.findByNameAndCfu(name, cfu);
	}



}
