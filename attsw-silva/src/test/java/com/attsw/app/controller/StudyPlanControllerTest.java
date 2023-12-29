package com.attsw.app.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.CourseRepository;
import com.attsw.app.repository.StudentRepository;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class StudyPlanControllerTest {

	
	private StudentRepository studentRepository;
	private StudentController studentController;
	private CourseRepository courseRepository;
	
	@Before
	public void setup()
	{
		studentRepository = mock(StudentRepository.class);
		courseRepository = mock(CourseRepository.class);
		studentController = new StudentController(studentRepository, courseRepository);
	}
	
	@Test
	public void testFindThrowExceptionWhenStudentIsNotFound()
	{
		Student student = createStudent();
		
		when(studentRepository.findById("1")).thenReturn(null);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> studentController.find("1"));
		assertEquals("Student not found", exception.getMessage());
		
	}

	@Test
	public void testFindStudentWhenStudentIsPresent()
	{
		Student student = createStudent();
		when(studentRepository.findById("1")).thenReturn(student);
		assertThat(studentController.find("1")).isEqualTo(student);		
	}


	// -------------------------INSERT COURSE INTO STUDY PLAN-------------------------
	@Test
	public void testInsertCourseIntoStudyPlan()
	{
		Student student = createStudentWithStudyPlan();
		Course analisi2 = new Course("2", "Analisi 2", 12);
		
		when(courseRepository.findById("2")).thenReturn(analisi2);
		
		studentController.insertCourseIntoStudyPlan(student, analisi2);
		assertTrue(student.getStudyPlan().contains(analisi2));
		
	}
	
	
	@Test
	public void testInsertCourseIntoStudyPlanWhenCourseIsAlreadyPresent() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("1", "Analisi 1", 12);
		
		when(courseRepository.findById("1")).thenReturn(course);
		
		studentController.insertCourseIntoStudyPlan(student, course);
		
		assertFalse(student.getStudyPlan().contains(course));
		
	}
	
	
	@Test
	public void testInsertCourseIntoStudyPlanCallsfindByIdFromRepository() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("2", "Analisi 2", 12);
		
		when(courseRepository.findById("2")).thenReturn(course);
		studentController.insertCourseIntoStudyPlan(student, course);
		
		verify(courseRepository).findById(course.getCourseId());
	}
	
	@Test
	public void testInsertCourseIntoStudyPlanThrowsExceptionWhenCourseIsNotPresent() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("3", "Analisi 3", 12);
		
		when(courseRepository.findById("3")).thenReturn(null);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> studentController.insertCourseIntoStudyPlan(student, course));

		assertEquals("Course not in repository", exception.getMessage());
	}
	@Test
	public void testInsertCourseIntoStudyPlanCallsUpdateStudyPlanFromRepository() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("2", "Analisi 2", 12);

		when(courseRepository.findById("2")).thenReturn(course);
		studentController.insertCourseIntoStudyPlan(student, course);

		verify(studentRepository).updateStudyPlan(student);
	}
	
	
	// -------------------------REMOVE COURSE FROM STUDY PLAN-------------------------
	
	@Test
	public void testRemoveCourseFromStudyPlanWhenCourseIsPresent() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);

		studentController.removeCourseFromStudyPlan(student, analisi1);
		assertTrue(student.getStudyPlan().isEmpty());

	}
	
	@Test
	public void testRemoveCourseFromSPThrowExceptionWhenCourseIsNotPresent() {
		Student student = createStudentWithStudyPlan();
		Course analisi2 = new Course("2", "Analisi 2", 12);
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
			() -> studentController.removeCourseFromStudyPlan(student, analisi2));
		
		assertEquals("Course not in study plan", exception.getMessage());

	}
	@Test
	public void testRemoveCourseFromStudyPlanUpdateStudyPlan() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);
		
		studentController.removeCourseFromStudyPlan(student, analisi1);
		assertTrue(student.getStudyPlan().isEmpty());

	}
	
	@Test
	public void testRemoveCourseFromStudyPlanCallsUpdateStudyPlanFromRepository() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);

		studentController.removeCourseFromStudyPlan(student, analisi1);

		verify(studentRepository).updateStudyPlan(student);
	}
	
	/*
	// ------------------------------UPDATE STUDY PLAN--------------------------------
	
	@Test
	public void testUpdateOneCourseOfStudyPlan()
	{
		Student student = createStudentWithStudyPlan();
		Course course1 = new Course("1", "Analisi 1", 12);
		Course course2 = new Course("1", "Analisi 1", 6);
		
		ArrayList<Course> studyPlan = new ArrayList<Course>();
		studyPlan.add(course1);
		student.setStudyPlan(studyPlan);
		
		studentController.updateStudyPlan(student, course2);
		
		assertTrue(student.getStudyPlan().contains(course2));
		assertFalse(student.getStudyPlan().contains(course1));
	}
	
	@Test
	public void testUpdateCourseItShouldUpdateIfCourseIsPresent()
	{
		Student student = createStudentWithStudyPlan();
		Course course2 = new Course("2", "Analisi 2", 6);

		studentController.updateStudyPlan(student, course2);

		assertFalse(student.getStudyPlan().contains(course2));
	}
	
	@Test
	public void testUpdateCourseCallUpdateStudyPlanFromRepository() {
		Student student = createStudentWithStudyPlan();
		Course course1 = new Course("1", "Analisi 1", 12);
		Course course2 = new Course("1", "Analisi 1", 6);

		ArrayList<Course> studyPlan = new ArrayList<Course>();
		studyPlan.add(course1);
		student.setStudyPlan(studyPlan);

		studentController.updateStudyPlan(student, course2);

		verify(studentRepository).updateStudyPlan(student);
	}*/

	// --------------------------- PRIVATE METHODS ------------------------------------
	
	private Student createStudent() {
        return new Student("1", "Mario", "Rossi", "1234");
    }

	private Student createStudentWithStudyPlan() {
		Student student = new Student("1", "Mario", "Rossi", "1234");

		Course course = new Course("1", "Analisi 1", 12);
		ArrayList<Course> studyPlan = new ArrayList<Course>();
		studyPlan.add(course);
		student.setStudyPlan(studyPlan);
		return student;
	}
	
	
}
