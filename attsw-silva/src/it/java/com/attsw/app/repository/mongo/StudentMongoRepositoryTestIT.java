package com.attsw.app.repository.mongo;

import static com.attsw.app.repository.mongo.StudentMongoRepository.STUDENT_COLLECTION_NAME;
import static com.attsw.app.repository.mongo.StudentMongoRepository.STUDENT_COURSE_COLLECTION_NAME;
import static com.attsw.app.repository.mongo.StudentMongoRepository.STUDYPLAN_DB_NAME;
import static org.junit.Assert.assertEquals;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
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
	private MongoCollection<Document> studentCourseCollection;
	
	
	@Before
	public void setup() {
		
		client = new MongoClient(
			new ServerAddress(
				mongo.getContainerIpAddress(),
				mongo.getFirstMappedPort()));
		
		studentMongoRepository = new StudentMongoRepository(client);
		MongoDatabase database = client.getDatabase(STUDYPLAN_DB_NAME);
		database.drop();
		studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
		studentCourseCollection = database.getCollection(STUDENT_COURSE_COLLECTION_NAME);
		
		studentCollection.insertOne(
            new Document()
                .append("id", "1")
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
		                        .append("cfu", 12)))
		                );
		Document d = studentCollection.find(Filters.eq("id", "1")).first();
		// get the subdocument studyplan from d
		// save d.get("studyPlan") in a list of documents
		List<Document> studyPlan = (List<Document>) d.get("studyPlan");
		// print the list of documents
		for (Document document : studyPlan) {
			System.out.println(document.toJson());
		}
		System.out.println(d.toJson());
		
	}

	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void test() {
		assertEquals(1, studentCollection.countDocuments());
	}
	
	

}
