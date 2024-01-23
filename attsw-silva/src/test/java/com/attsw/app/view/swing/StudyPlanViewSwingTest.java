


package com.attsw.app.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
		window.button("btnLogout").requireDisabled();
		window.scrollPane("scrollPane").requireDisabled();
			
		assertThat(window.list("CourseList").contents()).isEmpty();

	}
	
	
	@Test
	public void testLoginButtonEnableWhenStudentIdIsNotEmpty() {

		window.textBox("txtStudentId").enterText("1");
		assertThat(window.button("btnLogin").isEnabled()).isTrue();
	}

	@Test
	public void testLoginButtonDisabledWhenStudentIdisBlank() {

		window.textBox("txtStudentId").enterText("   ");
		
		assertThat(window.button("btnLogin").isEnabled()).isFalse();
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
		
		window.scrollPane("scrollPane").requireEnabled();
		
		window.button("btnLogout").requireEnabled();
		window.button("btnLogin").requireDisabled();
		
	}
	@Test
	public void testInsertCourseButtonShouldBeDisabledIfStudentIsNotLogged()
	{
		window.textBox("txtCourseName").enterText("Analisi");
		window.textBox("txtcfu").enterText("12");
		
		assertThat(window.button("btnInsertNewCourse").isEnabled()).isFalse();
		
	}
	
	@Test
	public void testLogoutButton()
	{
		window.textBox("txtStudentId").enterText("1");
		Student s = new Student("1", "Mario", "Rossi", "123");
		ArrayList<Course> sp = new ArrayList<Course>();
		Course c = new Course("1", "Analisi", 12);
		
		sp.add(c);
		s.setStudyPlan(sp);
		
		when(studentcontroller.find("1")).thenReturn(s);
		
		window.button("btnLogin").click();
		
		window.button("btnLogout").click();
		
		window.textBox("txtStudentId").requireEnabled();
		window.button("btnLogin").requireEnabled();
		
		window.button("btnRemoveSelectedCourse").requireDisabled();
		window.scrollPane("scrollPane").requireDisabled();
		
		window.button("btnLogout").requireDisabled();
		
		window.textBox("txtCourseName").requireText("");
		window.textBox("txtcfu").requireText("");
		
		
		assertThat(window.scrollPane("scrollPane").isEnabled()).isFalse();

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

		assertThat(window.button("btnRemoveSelectedCourse").isEnabled()).isFalse();
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		
		GuiActionRunner.execute(
			() -> studyplanview.showError("error message")
		);
		
		assertThat(window.label("lbErrorMsg").text()).isEqualTo("error message");
	}
	
	@Test
	public void testUpdateButtonShouldBeEnabledOnlyWhenACourseIsSelected() {

		GuiActionRunner.execute(() -> studyplanview.getCourseList().addElement(new Course("1", "Analisi", 12)));

		window.list("CourseList").selectItem(0);
       
		JButtonFixture updateButton = window.button(JButtonMatcher.withText("UPDATE COURSE"));
		updateButton.requireEnabled();
		window.list("CourseList").clearSelection();
		assertThat(window.button("btnUpdateCourse").isEnabled()).isFalse();
	}
	
	@Test
	public void testFillCourseFieldsWhenACourseIsSelected() {

		GuiActionRunner.execute(() -> studyplanview.getCourseList().addElement(new Course("1", "Analisi", 12)));

		window.list("CourseList").selectItem(0);
		window.list("CourseList");
		window.textBox("txtcfu").requireText("12");
		assertThat(window.textBox("txtCourseName").text()).isEqualTo("Analisi");
	}
	
	@Test
	public void testInsertButtonShouldBeEnabledOnlyWhenCourseNameAndCfuAreNotEmptyAndLoggedIsTrue() {
		
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu= window.textBox("txtcfu");
		studyplanview.setLogged(true);
		
		txtCourseName.enterText("Analisi");
		txtcfu.enterText("12");
		
		
		assertThat(window.button("btnInsertNewCourse").isEnabled()).isTrue();
		
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

		assertThat(window.button("btnInsertNewCourse").isEnabled()).isFalse();

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
		
		
		window.button("btnInsertNewCourse").click();
		verify(studentcontroller).insertCourseIntoStudyPlan(s, "Analisi", 12);
	}
	
	@Test
	public void testUpdateCourseShouldCallStudentControllUpdate() {
		Student s = new Student("1", "Mario", "Rossi", "123");
		Course c = new Course("1", "Analisi 1", 12);
		
		ArrayList<Course> sp = new ArrayList<Course>();
		sp.add(c);
		s.setStudyPlan(sp);
		
		window.textBox("txtStudentId").enterText("1");
		when(studentcontroller.find("1")).thenReturn(s);
		window.button("btnLogin").click();
		window.list("CourseList").selectItem(0);
        
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");
		
		txtCourseName.setText("");
		txtcfu.setText("");
		
		txtCourseName.enterText("Analisi 2");
		txtcfu.enterText("12");
		
		when(studentcontroller.updateStudyPlan(s, "Analisi 1", 12, "Analisi 2", 12)).thenReturn(s);

		window.button("btnUpdateCourse").click();
		verify(studentcontroller).updateStudyPlan(s, "Analisi 1", 12, "Analisi 2", 12);
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
			studyplanview.showStudyPlan(sp);
		});

		window.list("CourseList").selectItem(0);
		
		window.button("btnRemoveSelectedCourse").click();
		verify(studentcontroller).removeCourseFromStudyPlan(s,  "Analisi 1", 12);
	}
	
	@Test
	public void testCourseInsertDescriptionToTheList() {
	
		Course c1 = new Course("1", "Analisi 1", 12);
		Course c2 = new Course("2", "Analisi 2", 12);
		GuiActionRunner.execute(() -> {
			ArrayList<Course> sp = new ArrayList<Course>();
			sp.add(c1);
			sp.add(c2);
			studyplanview.showStudyPlan(sp);
		});
		String[] listContents = window.list("CourseList").contents();
		assertThat(listContents).containsExactly("Course: 1 Analisi 1 12", "Course: 2 Analisi 2 12");
	}
	
	@Test
	public void testCourseRemoveDescriptionFromList()
	{
		Course c1 = new Course("1", "Analisi 1", 12);
		Course c2 = new Course("2", "Analisi 2", 12);
		GuiActionRunner.execute(() -> { 
			ArrayList<Course> sp = new ArrayList<Course>();
			sp.add(c1);
			sp.add(c2);
			studyplanview.showStudyPlan(sp);
			studyplanview.courseRemoved(c1);
		});
		
		
		
		String[] listContents = window.list("CourseList").contents();
		
		assertThat(listContents).containsExactly("Course: 2 Analisi 2 12");
	}


	
}
