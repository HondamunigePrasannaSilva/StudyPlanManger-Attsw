package com.attsw.app.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.CourseRepository;
import com.attsw.app.repository.StudentRepository;
import com.attsw.app.view.StudyPlanView;

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
	private StudyPlanView studyPlanView;
	
	@Before
	public void setup()
	{
		studentRepository = mock(StudentRepository.class);
		courseRepository = mock(CourseRepository.class);
		studyPlanView = mock(StudyPlanView.class);
		studentController = new StudentController(studentRepository, courseRepository, studyPlanView);
	}
	
	@Test
	public void testFindShowErrorMsgWhenStudentIsNotFound()
	{
		when(studentRepository.findById("1")).thenReturn(null);
		studentController.find("1");
		verify(studyPlanView).showError("Student not found");
	} 

	@Test
	public void testFindStudentWhenStudentIsPresent()
	{
		Student student = createStudent();
		when(studentRepository.findById("1")).thenReturn(student);
		studentController.find("1");
		assertThat(studentController.find("1")).isEqualTo(student);		
	}

	@Test
	public void testFindCallsShowStudyPlanFromViewIfStudentIsPresent() {
		Student student = createStudent();
		when(studentRepository.findById("1")).thenReturn(student);
		studentController.find("1");
		verify(studyPlanView).showStudyPlan(student.getStudyPlan());
	}
	

	// -------------------------INSERT COURSE INTO STUDY PLAN-------------------------
	@Test
	public void testInsertCourseIntoStudyPlan()
	{
		Student student = createStudentWithStudyPlan();
		Course analisi2 = new Course("2", "Analisi 2", 12);
		
		when(courseRepository.findByNameAndCfu("Analisi 2", 12)).thenReturn(analisi2);
		studentController.insertCourseIntoStudyPlan(student, "Analisi 2", 12);
		assertTrue(student.getStudyPlan().contains(analisi2));
		verify(studyPlanView).courseAdded(analisi2);
		verify(studyPlanView).showStudyPlan(student.getStudyPlan());
	}

	@Test
	public void testInsertCourseIntoStudyPlanWhenCourseIsAlreadyPresent() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("1", "Analisi 1", 12);

		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(course);
		
		studentController.insertCourseIntoStudyPlan(student, "Analisi 1", 12);
		
		assertThat(student.getStudyPlan()).extracting(Course::getCourseId).containsExactlyInAnyOrder("1");
			
	}

	@Test
	public void testInsertCourseIntoStudyPlanCallsfindByNameAndCfuCourseRepository() {

		Student student = createStudentWithStudyPlan();
		Course course = new Course("2", "Analisi 2", 12);

		when(courseRepository.findByNameAndCfu("Analisi 2", 12)).thenReturn(course);
		studentController.insertCourseIntoStudyPlan(student, "Analisi 2", 12);

		verify(courseRepository).findByNameAndCfu("Analisi 2", 12);
	}

	@Test
	public void testInsertCourseIntoStudyPlanShowErrorMsg() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("3", "Analisi 3", 12);
		
		when(courseRepository.findByNameAndCfu("Analisi 3", 12)).thenReturn(null);
		
		studentController.insertCourseIntoStudyPlan(student, "Analisi 3", 12);
		verify(studyPlanView).showError("Course not found");
	}
	
	@Test
	public void testInsertCourseIntoStudyPlanCallsUpdateStudyPlanFromRepository() {
		Student student = createStudentWithStudyPlan();
		Course course = new Course("2", "Analisi 2", 12);

		when(courseRepository.findByNameAndCfu("Analisi 2", 12)).thenReturn(course);
		studentController.insertCourseIntoStudyPlan(student, "Analisi 2", 12);

		verify(studentRepository).updateStudyPlan(student);
	}

	// -------------------------REMOVE COURSE FROM STUDY PLAN-------------------------

	@Test
	public void testRemoveCourseFromStudyPlanWhenCourseIsPresent() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);
		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(analisi1);
		studentController.removeCourseFromStudyPlan(student,"Analisi 1", 12);
		assertTrue(student.getStudyPlan().isEmpty());
	}
	@Test
	public void testRemoveCourseFromStudyPlanWhenCourseIsNotPresentInRepository() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);
		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(null);
		studentController.removeCourseFromStudyPlan(student,"Analisi 1", 12);
		verify(studyPlanView).showError("Course not found");
	}
	
	@Test
	public void testRemoveCourseFromSPShowErrorMsg() {
		Student student = createStudentWithStudyPlan();
		Course analisi2 = new Course("2", "Analisi 2", 12);

		when(courseRepository.findByNameAndCfu("Analisi 2", 12)).thenReturn(analisi2);
		studentController.removeCourseFromStudyPlan(student, "Analisi 2", 12);
		verify(studyPlanView).showError("Course not in study plan");
		 

	}
	@Test
	public void testRemoveCourseFromStudyPlanUpdateStudyPlan() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);		
		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(analisi1);
		studentController.removeCourseFromStudyPlan(student, "Analisi 1", 12);
		assertTrue(student.getStudyPlan().isEmpty());
		verify(studyPlanView).courseRemoved(analisi1);
		verify(studyPlanView).showStudyPlan(student.getStudyPlan());
	}

	@Test
	public void testRemoveCourseFromStudyPlanCallsUpdateStudyPlanFromRepository() {
		Student student = createStudentWithStudyPlan();
		Course analisi1 = new Course("1", "Analisi 1", 12);
		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(analisi1);
		studentController.removeCourseFromStudyPlan(student,"Analisi 1", 12);
		verify(studentRepository).updateStudyPlan(student);
	}
	
	// -------------------------UPDATE COURSE FROM STUDY PLAN-------------------------
	@Test
	public void testUpdateOneCourseOfStudyPlan()
	{

		Student student = createStudentWithStudyPlan();
		Course course1 = new Course("1", "Analisi 1", 12);
		Course course2 = new Course("2", "Analisi 1", 6);

		when(courseRepository.findByNameAndCfu("Analisi 1", 6)).thenReturn(course2);
		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(course1);
		
		Student s = studentController.updateStudyPlan(student,"Analisi 1",12 ,"Analisi 1", 6);
		
		assertTrue(s.getStudyPlan().contains(course2));
		assertFalse(s.getStudyPlan().contains(course1));
		
		verify(studentRepository).updateStudyPlan(student);
		verify(studyPlanView).courseRemoved(course1);
		verify(studyPlanView).courseAdded(course2);
		verify(studyPlanView).showStudyPlan(s.getStudyPlan());
		

	}
	
	@Test
	public void testUpdateOneCourseOfStudyPlanWhenCourseIsNotPresentInRepository()
	{

		Student student = createStudentWithStudyPlan();
		Course course1 = new Course("1", "Analisi 1", 12);
		Course course2 = new Course("2", "Analisi 1", 6);

		when(courseRepository.findByNameAndCfu("Analisi 1", 6)).thenReturn(null);
		when(courseRepository.findByNameAndCfu("Analisi 1", 12)).thenReturn(course1);
		
		Student s = studentController.updateStudyPlan(student,"Analisi 1", 12,"Analisi 1", 6);

		assertThat(s.getStudyPlan())
			.extracting(Course::getCourseId)
			.containsExactly("1");
		
		verify(studyPlanView).showError("Course not found");
	}
	
	@Test
	public void testUpdateStudyPlanWhenCourseIsNotPresentInStudyPlan() {

		Student student = createStudentWithStudyPlan();
		Course course1 = new Course("3", "Analisi 2", 12);
		Course course2 = new Course("4", "Analisi 2", 6);

		when(courseRepository.findByNameAndCfu("Analisi 2", 6)).thenReturn(course2);
		when(courseRepository.findByNameAndCfu("Analisi 2", 12)).thenReturn(course1);

		Student s = studentController.updateStudyPlan(student, "Analisi 2", 12, "Analisi 2", 6);

		assertThat(s.getStudyPlan()).extracting(Course::getCourseId).containsExactly("1");

		verify(studyPlanView).showError("Course not in study plan");
	}
	

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
