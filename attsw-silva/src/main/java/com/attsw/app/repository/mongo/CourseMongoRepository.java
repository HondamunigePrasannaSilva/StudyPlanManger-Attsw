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
	
	public static final String STUDENT_COURSE_COLLECTION_NAME = "student_course";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	private MongoCollection<Document> courseCollection;
	
	public CourseMongoRepository(MongoClient client) {
		courseCollection = client
				.getDatabase(STUDYPLAN_DB_NAME)
				.getCollection(STUDENT_COURSE_COLLECTION_NAME);
	}
	@Override
	public List<Course> findAll() {
		return StreamSupport
				.stream(courseCollection.find().spliterator(), false)
				.map(d -> fromDocumentToStudent(d))
				.collect(Collectors.toList());
	}
	@Override
	public Course findById(String id) {
		Document c = courseCollection.find(new Document("courseId", id)).first();
		if (c == null)
			return null;
		else
			return fromDocumentToStudent(c);
		
	}
	
	private Course fromDocumentToStudent(Document d) {
		
		Course course = new Course(d.getString("courseId"),d.getString("courseName"),
				Integer.parseInt(d.getString("cfu")));
		return course;
	}
	
	
	

}
