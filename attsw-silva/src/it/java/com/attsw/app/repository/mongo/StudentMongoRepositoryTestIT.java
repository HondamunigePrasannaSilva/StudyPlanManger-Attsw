package com.attsw.app.repository.mongo;


import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentMongoRepositoryTestIT {
	
	@ClassRule
	public static MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private StudentMongoRepository studentMongoRepository;
	private MongoCollection<Document> studentCollection;
	
	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	@Before
	public void setup() {
	
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getFirstMappedPort()));
	
		studentMongoRepository = new StudentMongoRepository(client, STUDYPLAN_DB_NAME, STUDENT_COLLECTION_NAME);
		
		MongoDatabase database = client.getDatabase(STUDYPLAN_DB_NAME);
		database.drop();
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
	} 

	
	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindById() {
	
		Document d1 = addStudentWithStudyPlan("1");
		addStudentWithStudyPlan("2");
		
		
		assertEquals(fromDocumentToStudent(d1).getId(), studentMongoRepository.findById("1").getId());
	}
	
	@Test
	public void testFindByIdWhenIdDoesNotExist() {
		assertNull(studentMongoRepository.findById("1"));
	}
	
	@Test
	public void testFindAll() {
		
		addStudentWithStudyPlan("3");
		addStudentWithStudyPlan("4");
		
		assertThat(studentMongoRepository.findAll())
		    .extracting(Student::getId)
		    .containsExactlyInAnyOrder("3","4");
	}
	@Test
	public void testFindAllWhenDbIsEmpty() {
		assertThat(studentMongoRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testUpdateStudyPlan() {

		Document d1 = addStudentWithStudyPlan("5");

		Student s = fromDocumentToStudent(d1);
		s.addCourse(new Course("3", "Sistemi Operativi", 12));
		studentMongoRepository.updateStudyPlan(s);
		Document d2 = studentCollection.find(Filters.eq("mnumber", "5")).first();
		assertEquals(3, d2.get("studyPlan", List.class).size());

	}
	
	

	// ------------------------- PRIVATE METHODS ------------------------------
	private Document addStudentWithStudyPlan(String id){
		
		Document d = new Document()
                .append("mnumber", id)
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
		return d;
		
	}


	@SuppressWarnings("unchecked")
	private Student fromDocumentToStudent(Document d) {
		
		List<Document> studyPlan = (List<Document>) d.get("studyPlan");
		Student s = new Student(d.get("mnumber").toString(), 
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
