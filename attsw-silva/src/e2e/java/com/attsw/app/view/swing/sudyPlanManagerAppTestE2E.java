package com.attsw.app.view.swing;

import java.util.Arrays;
import static org.assertj.swing.launcher.ApplicationLauncher.*;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;
import org.assertj.swing.finder.WindowFinder;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static org.assertj.core.api.Assertions.*;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;


@RunWith(GUITestRunner.class)
public class sudyPlanManagerAppTestE2E extends AssertJSwingJUnitTestCase {

	
	@ClassRule
	public static MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String STUDENT_COURSE_COLLECTION_NAME = "student_course";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	private MongoClient client;
	
	private FrameFixture window;
	
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	
	@Override
	protected void onSetUp() throws Exception {
		// TODO Auto-generated method stub
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer firstMappedPort = mongo.getFirstMappedPort();
		
		client = new MongoClient(containerIpAddress, firstMappedPort);
		
		MongoDatabase database = client.getDatabase(STUDYPLAN_DB_NAME);
		database.drop();
		
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
		courseCollection = database.getCollection(STUDENT_COURSE_COLLECTION_NAME);
		
		insertStudent("1", "Mario", "Rossi", "1");
		insertStudent("2", "Luigi", "Verdi", "1");
		
		addCourseToDB("1", "Analisi Matematica", "12");
		addCourseToDB("2", "Fisica", "12");
		addCourseToDB("3", "Informatica", "6");
		addCourseToDB("4", "Sistemi Operativi", "9");
		addCourseToDB("4", "Sistemi Operativi", "9");
		addCourseToDB("5", "Programmazione 1", "9");
		addCourseToDB("6", "Programmazione 2", "9");
		

		application("com.attsw.app.swing.StudyPlanManger")
				.withArgs(
						"--mongo-host=" + containerIpAddress,
						"--mongo-port=" + firstMappedPort.toString(),
						"--db-name=" + STUDYPLAN_DB_NAME,
						"--db-student=" + STUDENT_COLLECTION_NAME,
						"--db-course=" + STUDENT_COURSE_COLLECTION_NAME
				
						
						).start();
		
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			protected boolean isMatching(JFrame frame) {
				return "Study Plan Manager".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
		
		
			
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}
	
	@Test @GUITest
	public void testLoginButtonSucces()
	{	
		window.textBox("txtStudentId").enterText("1");
		window.button(JButtonMatcher.withText("Login")).click();
		assertThat(window.list("CourseList").contents())
		.anySatisfy(item -> assertThat(item).contains("Course: 1 Analisi Matematica 12"));
	}
	
	@Test @GUITest
	public void testInsertCourseInList() {
		window.textBox("txtStudentId").enterText("1");
		window.button(JButtonMatcher.withText("Login")).click();

		window.textBox("txtCourseName").enterText("Fisica");
		window.textBox("txtcfu").enterText("12");

		window.button("btnInsertNewCourse").click();

		assertThat(window.list("CourseList").contents())
				.anySatisfy(item -> assertThat(item).contains("Course: 1 Analisi Matematica 12"))
				.anySatisfy(item -> assertThat(item).contains("Course: 2 Fisica 12"));
	}
	
    @Test @GUITest
    public void testRemoveCourseFromList() {
    	window.textBox("txtStudentId").enterText("1");
    	window.button(JButtonMatcher.withText("Login")).click();
    	
    	window.textBox("txtCourseName").enterText("Fisica");
		window.textBox("txtcfu").enterText("12");

		window.button("btnInsertNewCourse").click();

    	window.list("CourseList").selectItem(0);
    	window.button("btnRemoveSelectedCourse").click();
    	
    	assertThat(window.list("CourseList").contents())
    		.anySatisfy(item -> assertThat(item).contains("Course: 2 Fisica 12"));
    	
    }
    
    @Test @GUITest
    public void testUpdateCourseFromList() {
    	window.textBox("txtStudentId").enterText("1");
    	window.button(JButtonMatcher.withText("Login")).click();
    	
    	
    	window.list("CourseList").selectItem(0);
    	window.textBox("txtCourseName").deleteText();
    	window.textBox("txtCourseName").enterText("Fisica");
    	
    
    	window.button("btnUpdateCourse").click();
    	
		assertThat(window.list("CourseList").contents())
				.anySatisfy(item -> assertThat(item).contains("Course: 2 Fisica 12"));
		
    }
    
	private void insertStudent(String studentId, String studentName, String studentSurname, String idCdl)
	{
		Document d = new Document()
                .append("mnumber", studentId)
                .append("name", studentName)
                .append("surname", studentSurname)
                .append("idCdl", idCdl)
                .append("studyPlan", Arrays.asList(
		                    new Document()
		                        .append("courseId", "1")
		                        .append("courseName", "Analisi Matematica")
		                        .append("cfu", 12)
		                 ));
		
		studentCollection.insertOne(d);
    }
	
	private void addCourseToDB(String courseId, String courseName, String cfu) {
		Document d = new Document().append("courseId", courseId).append("courseName", courseName).append("cfu", cfu);
		courseCollection.insertOne(d);
	}

	

}
