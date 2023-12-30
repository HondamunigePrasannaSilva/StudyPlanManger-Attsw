package com.attsw.app.repository.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.repository.StudentRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

import com.mongodb.MongoClient;


public class StudentMongoRepository implements StudentRepository{
	public static final String STUDENT_COLLECTION_NAME = "student";
	public static final String STUDYPLAN_DB_NAME = "studyplan";
	
	private MongoCollection<Document> studentCollection;
	
	public StudentMongoRepository(MongoClient client) {
		studentCollection = client
				.getDatabase(STUDYPLAN_DB_NAME)
				.getCollection(STUDENT_COLLECTION_NAME);
	}
	
	@Override
	public List<Student> findAll() {
		return StreamSupport
				.stream(studentCollection.find().spliterator(), false)
				.map(d -> fromDocumentToStudent(d))
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(String id) {
	
		Document d = studentCollection.find(Filters.eq("id", id)).first();
		
		if (d == null)
            return null;
        else
        	return fromDocumentToStudent(d);
	}

	@Override
	public void updateStudyPlan(Student student) {
		studentCollection.replaceOne(Filters.eq("id", student.getId()), fromStudentToDocument(student));
		
	}
	
	
	
	// -------------------------------------------------------------------
	private Document fromStudentToDocument(Student student) {
		ArrayList<Document> studyPlan = new ArrayList<Document>();
		for (Course course : student.getStudyPlan()) {
			studyPlan.add(new Document("courseId", course.getCourseId()).append("courseName", course.getCourseName())
					.append("cfu", course.getCfu()));
		}
		
		Document d = new Document("id", student.getId()).append("name", student.getName())
				.append("surname", student.getSurname()).append("idCdl", student.getIdCdl())
				.append("studyPlan", studyPlan);
		
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
