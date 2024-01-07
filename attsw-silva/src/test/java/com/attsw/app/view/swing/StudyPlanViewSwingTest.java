


package com.attsw.app.view.swing;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
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

		window.textBox("txtCourseName").requireDisabled();
		window.textBox("txtcfu").requireDisabled();
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
		when(studentcontroller.find("1")).thenReturn(s);
		
		window.button("btnLogin").click();

		verify(studentcontroller).find("1");
		
		window.textBox("txtStudentId").requireDisabled();
		window.button("btnLogin").requireDisabled();
		
		window.textBox("txtcfu").requireEnabled();
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
	// TODO
	// 
	//
	//
	//
	
	

}
