


package com.attsw.app.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.attsw.app.controller.StudentController;
import com.attsw.app.model.Course;
import com.attsw.app.model.Student;



@RunWith(GUITestRunner.class)
public class StudyPlanViewSwingTest extends AssertJSwingJUnitTestCase{
	
	private FrameFixture window;
	
	private StudyPlanViewSwing studyplanview;
	
	@Mock
	private StudentController studentcontroller;
	
	private AutoCloseable closeable;


	@Override
	protected void onSetUp() throws Exception {
		
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			studyplanview = new StudyPlanViewSwing();
			studyplanview.setSudyPlanController(studentcontroller);
			studyplanview.setPreferredSize(new Dimension(1200, 700));

			return studyplanview;
		});

		window = new FrameFixture(robot(), studyplanview);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test @GUITest
	public void testControlsInitialStates() {

		window.textBox("txtStudentId").requireEnabled();
		window.button("btnLogin").requireDisabled();

		window.button("btnInsertNewCourse").requireDisabled();
		window.button("btnUpdateCourse").requireDisabled();
		window.button("btnRemoveSelectedCourse").requireDisabled();
	
		window.scrollPane("scrollPane").requireDisabled();

		window.label("lbErrorMsg").requireText(" ");

	}

	@Test
	public void testLoginButtonEnableWhenStudentIdIsNotEmpty() {

		window.textBox("txtStudentId").enterText("1");
		window.button("btnLogin").requireEnabled();
	}
	
	@Test
	public void testLoginButtonDisabledWhenStudentIdisBlank() {

		window.textBox("txtStudentId").enterText("   ");
		window.button("btnLogin").requireDisabled();
	}

	@Test @GUITest
	public void testEnableButtonsAndTxtFieldWhenLoginButtonIsPressed() {

		window.textBox("txtStudentId").enterText("1");
		Student s = new Student("1", "Mario", "Rossi", "123");
		ArrayList<Course> sp = new ArrayList<Course>();
		Course c = new Course("1", "Analisi", 12);
		
		sp.add(c);
		s.setStudyPlan(sp);
		
		when(studentcontroller.find("1")).thenReturn(s);
		
		window.button("btnLogin").click();

		verify(studentcontroller).find("1");
		
		window.textBox("txtStudentId").requireDisabled();
		window.button("btnLogin").requireDisabled();
		
		window.button("btnRemoveSelectedCourse").requireEnabled();
		window.scrollPane("scrollPane").requireEnabled();
	}
	
	@Test @GUITest
	public void testShowErrorWhenStudentIsNotFound() {

		window.textBox("txtStudentId").enterText("1");
		when(studentcontroller.find("1")).thenReturn(null);

		window.button("btnLogin").click();

		verify(studentcontroller).find("1");
		window.label("lbErrorMsg").requireText("Student not found");
	}
	
	@Test
	public void testDeleteButtonShouldBeEnabledOnlyWhenACourseIsSelected() {

		GuiActionRunner.execute(() -> studyplanview.getCourseList().addElement(new Course("1", "Analisi", 12)));
		
		window.list("CourseList").selectItem(0);
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("REMOVE SELECTED COURSE"));
		deleteButton.requireEnabled();
		window.list("CourseList").clearSelection();
		deleteButton.requireDisabled();
	}
	
	@Test
	public void testUpdateButtonShouldBeEnabledOnlyWhenACourseIsSelected() {

		GuiActionRunner.execute(() -> studyplanview.getCourseList().addElement(new Course("1", "Analisi", 12)));

		window.list("CourseList").selectItem(0);
       
		JButtonFixture updateButton = window.button(JButtonMatcher.withText("UPDATE COURSE"));
		updateButton.requireEnabled();
		window.list("CourseList").clearSelection();
		updateButton.requireDisabled();
	}
	
	@Test
	public void testFillCourseFieldsWhenACourseIsSelected() {

		GuiActionRunner.execute(() -> studyplanview.getCourseList().addElement(new Course("1", "Analisi", 12)));

		window.list("CourseList").selectItem(0);
		window.list("CourseList");
		window.textBox("txtCourseName").requireText("Analisi");
		window.textBox("txtcfu").requireText("12");
	}
	
	@Test
	public void testRemoveCourseFromListWhenRemoveButtonIsPressed() {
		
		Course c = new Course("1", "Analisi 1", 12);
		Course c1 = new Course("2", "Analisi 2", 12);
		GuiActionRunner.execute(
				() ->  {
						DefaultListModel<Course> courseList = studyplanview.getCourseList();
						courseList.addElement(c);
						courseList.addElement(c1);						
				});
		
		window.list("CourseList").selectItem(0);
		when(studentcontroller.findCourseByNameAndCfu("Analisi 1", 12)).thenReturn(c);
		window.button("btnRemoveSelectedCourse").click();
		
		String[] listContents = window.list("CourseList").contents();
		assertThat(listContents).containsExactly("Course: 2 Analisi 2 12");
		window.label("lbErrorMsg").requireText(" ");
		
	}
	
	@Test
	public void testInsertButtonShouldBeEnabledOnlyWhenCourseNameAndCfuAreNotEmpty() {
		
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu= window.textBox("txtcfu");
		
		txtCourseName.enterText("Analisi");
		txtcfu.enterText("12");
		
		window.button("btnInsertNewCourse").requireEnabled();
		
	}
	
	@Test 
	public void testInsertButtonShouldBeDisabledWhenCourseNameIsBlank() {

		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");

		txtCourseName.enterText(" ");
		txtcfu.enterText("12");

		window.button("btnInsertNewCourse").requireDisabled();
		
		txtCourseName.setText("");
		txtcfu.setText("");

		txtCourseName.enterText("Analisi");
		txtcfu.enterText(" ");

		
		window.button("btnInsertNewCourse").requireDisabled();

	}
	
	@Test
	public void testInsertCourseInListWhenInsertButtonIsPressed() {
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");
		
		txtCourseName.enterText("Analisi");
		txtcfu.enterText("12");
		when(studentcontroller.findCourseByNameAndCfu("Analisi", 12)).thenReturn(new Course("1", "Analisi", 12));
		
		window.button("btnInsertNewCourse").click();
		String[] listContents = window.list("CourseList").contents();
		assertThat(listContents).containsExactly("Course: 1 Analisi 12");
		window.label("lbErrorMsg").requireText(" ");
	}
	
	@Test
	public void testUpdateCourseListWhenButtonIsPressed()
	{
		Course c = new Course("1", "Analisi 1", 12);
		Course c1 = new Course("2", "Analisi 2", 12);
		Course c2 = new Course("3", "Analisi 3", 12);
	
		GuiActionRunner.execute(() -> {
			DefaultListModel<Course> courseList = studyplanview.getCourseList();
			courseList.addElement(c);
			courseList.addElement(c1);
		});

		window.list("CourseList").selectItem(0);
		when(studentcontroller.findCourseByNameAndCfu("Analisi 1", 12)).thenReturn(c);
		when(studentcontroller.findCourseByNameAndCfu("Analisi 3", 12)).thenReturn(c2);
				
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");
		
		txtCourseName.setText("");
		txtcfu.setText("");
		
		txtCourseName.enterText("Analisi 3");
		txtcfu.enterText("12");

		window.button("btnUpdateCourse").click();
		
		String[] listContents = window.list("CourseList").contents();
		assertThat(listContents).containsExactly("Course: 2 Analisi 2 12", "Course: 3 Analisi 3 12");
		window.label("lbErrorMsg").requireText(" ");
	}
	
	@Test
	public void testInsertCourseShouldCallStudentControllInsert()
	{
		Student s = new Student("1", "Mario", "Rossi", "123");
		Course c = new Course("1", "Analisi 1", 12);
		
		window.textBox("txtStudentId").enterText("1");
		when(studentcontroller.find("1")).thenReturn(s);
		ArrayList<Course> sp = new ArrayList<Course>();
		sp.add(c);
		s.setStudyPlan(sp);
		
		window.button("btnLogin").click();

		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");

		txtCourseName.enterText("Analisi");
		txtcfu.enterText("12");
		when(studentcontroller.findCourseByNameAndCfu("Analisi", 12)).thenReturn(c);
		
		when(studentcontroller.insertCourseIntoStudyPlan(s, c)).thenReturn(s);
		
		window.button("btnInsertNewCourse").click();
		verify(studentcontroller).insertCourseIntoStudyPlan(s, c);
	}
	
	@Test
	public void testUpdateCourseShouldCallStudentControllUpdate() {
		Student s = new Student("1", "Mario", "Rossi", "123");
		Course c = new Course("1", "Analisi 1", 12);
		Course c1 = new Course("2", "Analisi 2", 12);
		ArrayList<Course> sp = new ArrayList<Course>();
		sp.add(c);
		sp.add(c1);
		s.setStudyPlan(sp);
		
		window.textBox("txtStudentId").enterText("1");
		when(studentcontroller.find("1")).thenReturn(s);
		window.button("btnLogin").click();
		
		GuiActionRunner.execute(() -> {
			DefaultListModel<Course> courseList = studyplanview.getCourseList();
			courseList.addElement(c);
			
		});
        
		window.list("CourseList").selectItem(0);
        
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");
		
		txtCourseName.setText("");
		txtcfu.setText("");
		
		txtCourseName.enterText("Analisi 2");
		txtcfu.enterText("12");
		when(studentcontroller.findCourseByNameAndCfu("Analisi 1", 12)).thenReturn(c);
		when(studentcontroller.findCourseByNameAndCfu("Analisi 2", 12)).thenReturn(c1);

		when(studentcontroller.updateStudyPlan(s, c, c1)).thenReturn(s);

		window.button("btnUpdateCourse").click();
		verify(studentcontroller).updateStudyPlan(s, c, c1);
	}
	
	@Test
	public void testRemoveCourseShouldCallStudentControllRemove() {
		
		Student s = new Student("1", "Mario", "Rossi", "123");
		Course c = new Course("1", "Analisi 1", 12);
		Course c1 = new Course("2", "Analisi 2", 12);
		ArrayList<Course> sp = new ArrayList<Course>();
		sp.add(c);
		sp.add(c1);
		s.setStudyPlan(sp);
		
		window.textBox("txtStudentId").enterText("1");
		when(studentcontroller.find("1")).thenReturn(s);
		window.button("btnLogin").click();

		GuiActionRunner.execute(() -> {
			DefaultListModel<Course> courseList = studyplanview.getCourseList();
			courseList.addElement(c);
			courseList.addElement(c1);
		});

		window.list("CourseList").selectItem(0);
		when(studentcontroller.findCourseByNameAndCfu("Analisi 1", 12)).thenReturn(c);
		when(studentcontroller.findCourseByNameAndCfu("Analisi 2", 12)).thenReturn(c1);

		window.button("btnRemoveSelectedCourse").click();
		verify(studentcontroller).removeCourseFromStudyPlan(s, c);
	}

	@Test
	public void testShowStudyPlanWhenStudentIsLogged()
	{
		Student s = new Student("1", "Mario", "Rossi", "123");
		Course c = new Course("1", "Analisi 1", 12);
		Course c1 = new Course("2", "Analisi 2", 12);
		ArrayList<Course> sp = new ArrayList<Course>();
		sp.add(c);
		sp.add(c1);
		s.setStudyPlan(sp);
		
		window.textBox("txtStudentId").enterText("1");
		when(studentcontroller.find("1")).thenReturn(s);
		
		window.button("btnLogin").click();
		
		// assert that the list contains the same elements in the same order.
		String[] listContents = window.list("CourseList").contents();
		assertThat(listContents).containsExactly("Course: 1 Analisi 1 12", "Course: 2 Analisi 2 12");
		
		
	}
	
}
