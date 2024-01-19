package com.attsw.app.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.attsw.app.model.Course;
import com.attsw.app.repository.CourseRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class CourseMongoRepository implements CourseRepository {
	
	public static final String CFU = "cfu";
	public static final String COURSE_NAME = "courseName";
	public static final String COURSE_ID = "courseId";
	
	
	
	private MongoCollection<Document> courseCollection;
	
	public CourseMongoRepository(MongoClient client, String dbName, String collectionName) {
		courseCollection = client.getDatabase(dbName).getCollection(collectionName);
	}
	@Override
	public List<Course> findAll() {
		return StreamSupport
				.stream(courseCollection.find().spliterator(), false)
				.map(this::fromDocumentToStudent)
				.collect(Collectors.toList());
	}
	@Override
	public Course findById(String id) {
		Document c = courseCollection.find(new Document(COURSE_ID, id)).first();
		if (c == null)
			return null;
		else
			return fromDocumentToStudent(c);
		
	}
	
	private Course fromDocumentToStudent(Document d) {
		
		return new Course(d.getString(COURSE_ID),d.getString(COURSE_NAME),
				Integer.parseInt(d.getString(CFU)));
		
	}
	@Override
	public Course findByNameAndCfu(String name, int cfu) {
		return StreamSupport.stream(courseCollection.find().spliterator(), false)
				.map(this::fromDocumentToStudent).filter(c -> c.getCourseName().equals(name) && c.getCfu() == cfu)
				.findFirst().orElse(null);
	    
	    
		
	}
	
	
	

}
