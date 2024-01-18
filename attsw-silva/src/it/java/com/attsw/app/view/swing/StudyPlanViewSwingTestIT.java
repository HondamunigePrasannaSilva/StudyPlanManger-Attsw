package com.attsw.app.view.swing;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.util.Arrays;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.attsw.app.controller.StudentController;
import com.attsw.app.model.Course;
import com.attsw.app.repository.CourseRepository;
import com.attsw.app.repository.mongo.CourseMongoRepository;
import com.attsw.app.repository.mongo.StudentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@RunWith(GUITestRunner.class)
public class StudyPlanViewSwingTestIT extends AssertJSwingJUnitTestCase{
	
	@ClassRule
	public static MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	private FrameFixture window;
	
	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private CourseRepository courseRepository;
	
	private StudentController studentController;
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	
	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String STUDENT_COURSE_COLLECTION_NAME = "student_course";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	@Override
	public void onSetUp() {
		client = new MongoClient(
				new ServerAddress(
					mongo.getContainerIpAddress(),
					mongo.getFirstMappedPort()));
		
			studentMongoRepository = new StudentMongoRepository(client, STUDYPLAN_DB_NAME, STUDENT_COLLECTION_NAME);
			courseRepository = new CourseMongoRepository(client, STUDYPLAN_DB_NAME, STUDENT_COURSE_COLLECTION_NAME);

			MongoDatabase database = client.getDatabase(STUDYPLAN_DB_NAME);
			database.drop();
			studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
			courseCollection = database.getCollection(STUDENT_COURSE_COLLECTION_NAME);
			
			addStudentWithCourse();
			
			window = new FrameFixture(robot(), GuiActionRunner.execute(() -> {
				StudyPlanViewSwing studyPlanView = new StudyPlanViewSwing();
                studentController = new StudentController(studentMongoRepository, courseRepository, studyPlanView);
                studyPlanView.setSudyPlanController(studentController);
                studyPlanView.setPreferredSize(new Dimension(1200, 700));
                
                return studyPlanView;
			}));
			
			window.show();
			
	}

	
	@Override
	public void onTearDown() {
		client.close();
	}

	
	@Test @GUITest
	public void testInsertCourseInList() {
		
		window.textBox("txtStudentId").enterText("1");
		window.button("btnLogin").click();
		
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");
		
		txtCourseName.enterText("Fisica 2");
		txtcfu.enterText("12");
		
		window.button("btnInsertNewCourse").click();
		
		assertThat(studentMongoRepository.findById("1").getStudyPlan()).extracting(Course::getCourseId).containsExactlyInAnyOrder("1","2","3");
		
		}

	@Test @GUITest
	public void testRemoveCourseFromList() {

		window.textBox("txtStudentId").enterText("1");
		window.button("btnLogin").click();

		window.list("CourseList").selectItem(0);
		window.button("btnRemoveSelectedCourse").click();

		assertThat(studentMongoRepository.findById("1").getStudyPlan()).extracting(Course::getCourseId)
				.containsExactlyInAnyOrder("2");

	}
	
	@Test @GUITest
	public void testShowStudyPlan() {

		window.textBox("txtStudentId").enterText("1");
		window.button("btnLogin").click();
		Course course1 = new Course("1", "Analisi Matematica", 12);
		Course course2 = new Course("2", "Fisica", 12);
		
		assertThat(window.list("CourseList").contents()).containsExactlyInAnyOrder(course1.toString(), course2.toString());

	}


	@Test @GUITest
	public void testUpdateStudyPlan()
	{
		window.textBox("txtStudentId").enterText("1");
		window.button("btnLogin").click();
		
		Course course1 = new Course("1", "Analisi Matematica", 12);
		Course course2 = new Course("2", "Fisica", 12);
		Course course3 = new Course("3", "Fisica 2", 12);
		
		window.list("CourseList").selectItem(1);
		
		JTextComponentFixture txtCourseName = window.textBox("txtCourseName");
		JTextComponentFixture txtcfu = window.textBox("txtcfu");
		
		txtCourseName.setText("");
		txtcfu.setText("");
		
		txtCourseName.enterText("Fisica 2");
		txtcfu.enterText("12");
		
		window.button("btnUpdateCourse").click();

		assertThat(window.list("CourseList").contents()).containsExactlyInAnyOrder(course1.toString(), course3.toString());
		assertThat(studentMongoRepository.findById("1").getStudyPlan()).extracting(Course::getCourseId).containsExactlyInAnyOrder("1","3");
	}
	
	
	private void addStudentWithCourse()
	{
		Document d = new Document()
                .append("mnumber", "1")
                .append("name", "Mario")
                .append("surname", "Rossi")
                .append("idCdl", "1")
                .append("studyPlan", Arrays.asList(
		                    new Document()
		                        .append("courseId", "1")
		                        .append("courseName", "Analisi Matematica")
		                        .append("cfu", 12),
		                    new Document()
		                        .append("courseId", "2")
		                        .append("courseName", "Fisica")
		                        .append("cfu", 12)));
		studentCollection.insertOne(d);
		
		addCourseToDB("1", "Analisi Matematica", "12");
		addCourseToDB("2", "Fisica", "12");
		addCourseToDB("3", "Fisica 2", "12");
		
	}
	private Document addCourseToDB(String id, String name, String cfu) {

		Document course = new Document();
		course.append("courseId", id);
		course.append("courseName", name);
		course.append("cfu", cfu);
		courseCollection.insertOne(course);
		
		return course;
	}
	
	

}
