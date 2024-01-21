package com.attsw.app.controller;

import java.util.List;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.CourseRepository;
import com.attsw.app.repository.StudentRepository;
import com.attsw.app.view.StudyPlanView;


 public class StudentController {

	private StudentRepository studentRepository;
	private CourseRepository courseRepository;
	private StudyPlanView studyPlanView;
	
	// create a constant 
	public static final String ERROR_COURSE_NOT_FOUND = "Course not found";
	public static final String ERROR_COURSE_NOT_IN_STUDY_PLAN = "Course not in study plan";
	public static final String ERROR_STUDENT_NOT_FOUND = "Student not found";
 
	public StudentController(StudentRepository studentRepository, CourseRepository courseRepository, StudyPlanView studyPlanView) {
		
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.studyPlanView = studyPlanView;
	}

	public Student find(String studentId) {
		
		Student student = studentRepository.findById(studentId);
		
		if (student == null)
			studyPlanView.showError(ERROR_STUDENT_NOT_FOUND);
		else
			studyPlanView.showStudyPlan(student.getStudyPlan());
		
		return student;
	}

	public void insertCourseIntoStudyPlan(Student student,String addNameCourse, int addCfu) {
		
		Course course = courseRepository.findByNameAndCfu(addNameCourse, addCfu);
		if (course == null)
		{
			studyPlanView.showError(ERROR_COURSE_NOT_FOUND);
			return;
		}
		
		if (student.getStudyPlan().stream()
				.filter(c -> c.getCourseId().equals(course.getCourseId()))
				.count() > 0)
			return;
		
		student.addCourse(course);
		studentRepository.updateStudyPlan(student);
		studyPlanView.courseAdded(course);
		studyPlanView.showStudyPlan(student.getStudyPlan());
		
		
	}

	public void removeCourseFromStudyPlan(Student student, String delNameCourse, int delCfu) {
		
		
		Course course = courseRepository.findByNameAndCfu(delNameCourse, delCfu);
		
		if (course == null) {
			studyPlanView.showError(ERROR_COURSE_NOT_FOUND);
			return;
		}
		List<Course> sp = student.getStudyPlan();
		
		if(sp.removeIf(c -> c.getCourseId().equals(course.getCourseId())))
		{
			studentRepository.updateStudyPlan(student);
			studyPlanView.courseRemoved(course);
			studyPlanView.showStudyPlan(sp);
		}
		else
			studyPlanView.showError(ERROR_COURSE_NOT_IN_STUDY_PLAN);
	}


	public Student updateStudyPlan(Student student,String delNameCourse, int delCfu, String addNameCourse, int addCfu) {
		
		Course courseToAdd = courseRepository.findByNameAndCfu(addNameCourse, addCfu);
		Course coursetoUpdate = courseRepository.findByNameAndCfu(delNameCourse, delCfu);
		
		if (courseToAdd == null)
		{
			studyPlanView.showError(ERROR_COURSE_NOT_FOUND);
			return student;
		}
		List<Course> sp = student.getStudyPlan();
		
		if(sp.removeIf(c -> c.getCourseId().equals(coursetoUpdate.getCourseId())))	
		{	
			student.addCourse(courseToAdd);					
			studentRepository.updateStudyPlan(student);
			studyPlanView.courseRemoved(coursetoUpdate);
			studyPlanView.courseAdded(courseToAdd);
			studyPlanView.showStudyPlan(sp);
			
		} 
		else
			studyPlanView.showError(ERROR_COURSE_NOT_IN_STUDY_PLAN);
		return student; 
		
	} 
	




}
