package com.attsw.app.repository.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.StudentRepository;
import com.mongodb.client.MongoCollection;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

import com.mongodb.MongoClient;


public class StudentMongoRepository implements StudentRepository{
	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String STUDENT_COURSE_COLLECTION_NAME = "student_course";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	private MongoCollection<Document> studentCollection;
	
	public StudentMongoRepository(MongoClient client) {
		studentCollection = client
				.getDatabase(STUDYPLAN_DB_NAME)
				.getCollection(STUDENT_COLLECTION_NAME);
	}
	
	@Override
	public List<Student> findAll() {
		
		// from studentCollection create a list of students, the documents have one subdocument inside
		// the subdocument is the studyplan
		return StreamSupport.
				stream(studentCollection.find().spliterator(), false).
				map(d -> fromDocumentToStudent(d))
				.collect(Collectors.toList());
		
		
	}

	private Student fromDocumentToStudent(Document d) {
		System.out.println(d.toJson());
		
		return new Student();
	}
	
	@Override
	public Student findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Course> getStudyPlan(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStudyPlan(Student student) {
		// TODO Auto-generated method stub
		
	}
	
	

}
