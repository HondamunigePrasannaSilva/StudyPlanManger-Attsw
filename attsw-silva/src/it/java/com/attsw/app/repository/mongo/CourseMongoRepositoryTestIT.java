package com.attsw.app.repository.mongo;

import static com.attsw.app.repository.mongo.CourseMongoRepository.STUDENT_COURSE_COLLECTION_NAME;
import static com.attsw.app.repository.mongo.CourseMongoRepository.STUDYPLAN_DB_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	
	@Before
	public void setup() {
	
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getFirstMappedPort()));
	
		CourseMongoRepository = new CourseMongoRepository(client);
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
		Document c1 = addCourseToCollection("1", "Course1", "6");
		Document c2 = addCourseToCollection("2", "Course2", "9");
		
		assertTrue(CourseMongoRepository.findById("1")
			.getCourseId().equals("1"));
		
	}
	@Test
	public void testFindByIdWhenIdDoesNotExist() {
        assertTrue(CourseMongoRepository.findById("1") == null);
    }

	private Document addCourseToCollection(String id, String name, String cfu) {
		Document course = new Document();
		course.append("courseId", id);
		course.append("courseName", name);
		course.append("cfu", cfu);
		CourseCollection.insertOne(course);
		
		Document c = CourseCollection.find(new Document("courseId", id)).first();
		System.out.println(c);
		return course;
	}
	

}
