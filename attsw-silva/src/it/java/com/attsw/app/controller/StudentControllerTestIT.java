package com.attsw.app.controller;

import static com.attsw.app.repository.mongo.StudentMongoRepository.STUDENT_COLLECTION_NAME;
import static com.attsw.app.repository.mongo.CourseMongoRepository.STUDENT_COURSE_COLLECTION_NAME;
import static com.attsw.app.repository.mongo.StudentMongoRepository.STUDYPLAN_DB_NAME;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.testcontainers.containers.MongoDBContainer;

import com.attsw.app.repository.mongo.StudentMongoRepository;
import com.attsw.app.view.StudyPlanView;
import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.mongo.CourseMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class StudentControllerTestIT {
	
	@ClassRule
	public static MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private CourseMongoRepository courseMongoRepository;
	private StudentController studentController;
	
	private MongoCollection<Document> studentCollection;
	private MongoCollection<Document> courseCollection;
	
	@Mock
	private StudyPlanView studyPlanView;
	
	@Before
	public void setup() {
	
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getFirstMappedPort()));
	
		studentMongoRepository = new StudentMongoRepository(client);
		courseMongoRepository = new CourseMongoRepository(client);
		
		MongoDatabase database = client.getDatabase(STUDYPLAN_DB_NAME);
		database.drop();
		
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
		courseCollection = database.getCollection(STUDENT_COURSE_COLLECTION_NAME);
		
		studyPlanView = mock(StudyPlanView.class);
		studentController = new StudentController(studentMongoRepository, courseMongoRepository, studyPlanView);
	}

	
	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindStudentById() {
		Document d  = addStudentWithStudyPlan("1");
		Student student = studentController.find("1");
		assertThat(student.getId()).isEqualTo(d.get("id"));	
	}

	@Test
	public void testInsertNewCourseIntoStudyPlan() {

		Document d = addStudentWithStudyPlan("1");
		Course c = new Course("3", "Sistemi Operativi", 12);
		Document course = addCourseToDB("3", "Sistemi Operativi", "12");
		Student s = fromDocumentToStudent(d);

		studentController.insertCourseIntoStudyPlan(s, c);

		assertThat(studentController.find("1").getStudyPlan()).extracting(Course::getCourseId)
				.containsExactlyInAnyOrder("1", "2", "3");
	}

	@Test
	public void testRemoveCourseFromStudyPlan() {

		Document d = addStudentWithStudyPlan("1");
		Student s = fromDocumentToStudent(d);
		Course c = new Course("1", "Analisi Matematica", 12);

		studentController.removeCourseFromStudyPlan(s, c);

		assertThat(studentController.find("1").getStudyPlan()).extracting(Course::getCourseId)
				.containsExactly("2");
	}

	// ---------------------------------------------------------------------------------
	private Document addCourseToDB(String id, String name, String cfu) {

		Document course = new Document();
		course.append("courseId", id);
		course.append("courseName", name);
		course.append("cfu", cfu);
		courseCollection.insertOne(course);
		
		return course;
	}
	
	private Document addStudentWithStudyPlan(String id){
		
		Document d = new Document()
                .append("id", id)
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
		
		addCourseToDB("1", "Analisi Matematica", "12");
		addCourseToDB("2", "Fisica", "12");
		
		studentCollection.insertOne(d);
		return d;
		
	}
	private Student fromDocumentToStudent(Document d) {

		List<Document> studyPlan = (List<Document>) d.get("studyPlan");
		Student s = new Student(d.get("id").toString(), 
								d.get("name").toString(), 
								d.get("surname").toString(),
								d.get("idCdl").toString());
		ArrayList<Course> studyPlanList = new ArrayList<Course>();
		for (Document document : studyPlan) {
			studyPlanList.add(new Course(document.get("courseId").toString(),
					                     document.get("courseName").toString(),
					                     (int) document.get("cfu")));

		}
		s.setStudyPlan(studyPlanList);

		return s;
	}

}
