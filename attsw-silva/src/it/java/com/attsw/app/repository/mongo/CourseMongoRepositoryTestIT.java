package com.attsw.app.repository.mongo;


import static com.attsw.app.repository.mongo.CourseMongoRepository.COURSE_ID;
import static com.attsw.app.repository.mongo.CourseMongoRepository.COURSE_NAME;
import static com.attsw.app.repository.mongo.CourseMongoRepository.CFU;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.attsw.app.model.Course;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CourseMongoRepositoryTestIT {
	
	@ClassRule
	public static MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private CourseMongoRepository CourseMongoRepository;
	private MongoCollection<Document> CourseCollection;
	
	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String STUDENT_COURSE_COLLECTION_NAME = "student_course";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	@Before
	public void setup() {
	
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getFirstMappedPort()));
	
		CourseMongoRepository = new CourseMongoRepository(client, STUDYPLAN_DB_NAME, STUDENT_COURSE_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(STUDYPLAN_DB_NAME);
		database.drop();
		CourseCollection = database.getCollection(STUDENT_COURSE_COLLECTION_NAME);
	}
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testFindAll() {
		addCourseToCollection("1", "Course1", "6");
		addCourseToCollection("2", "Course2", "9");
		
		assertThat(CourseMongoRepository.findAll())
			.extracting(Course::getCourseId)
			.contains("1", "2");
	}
	
	@Test
	public void testFindAllWhenDbIsEmpty() {
		assertThat(CourseMongoRepository.findAll()).isEmpty();
	}
	@Test
	public void testFindById() {
		addCourseToCollection("1", "Course1", "6");
		addCourseToCollection("2", "Course2", "9");
		
		assertEquals("1", CourseMongoRepository.findById("1").getCourseId());
		
	}
	
	@Test 
	public void testFineByNameAndCfu() {
		addCourseToCollection("1", "Course1", "6");
		addCourseToCollection("2", "Course2", "9");

		assertEquals("1", CourseMongoRepository.findByNameAndCfu("Course1", 6).getCourseId());
	}
	@Test
	public void testFindByNameAndCfuWhenNameIsIncorrect() {
		addCourseToCollection("1", "Course1", "6");
		assertNull(CourseMongoRepository.findByNameAndCfu("Course 1", 6));
	}
	@Test
	public void testFindByNameAndCfuWhenCfuIsIncorrect() {
		addCourseToCollection("1", "Course1", "6");
		assertNull(CourseMongoRepository.findByNameAndCfu("Course1", 9));
	}
	@Test
	public void testFindByNameAndCfuWhenCfuAndNameAreIncorrect() {
		addCourseToCollection("1", "Course1", "6");
		assertNull(CourseMongoRepository.findByNameAndCfu("Course 1", 9));
	}
	@Test
	public void testFindByIdWhenIdDoesNotExist() {
        assertNull(CourseMongoRepository.findById("1"));
    }

	private Document addCourseToCollection(String id, String name, String cfu) {

		Document course = new Document();
		course.append(COURSE_ID, id);
		course.append(COURSE_NAME, name);
		course.append(CFU, cfu);
		CourseCollection.insertOne(course);
		
		return course;
	}
	

}
