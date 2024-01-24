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

	
	private MongoCollection<Document> studentCollection;

	public static final String MNUMBER = "mnumber";
	public static final String NAME = "name";
	public static final String SURNAME = "surname";
	public static final String ID_CDL = "idCdl";
	public static final String STUDY_PLAN = "studyPlan";
	
	public StudentMongoRepository(MongoClient client, String dbName, String collectionName) {
		studentCollection = client
				.getDatabase(dbName)
				.getCollection(collectionName);
	}
	
	@Override
	public List<Student> findAll() {
		return StreamSupport
				.stream(studentCollection.find().spliterator(), false)
				.map(this::fromDocumentToStudent)
				.collect(Collectors.toList());
	}

	@Override
	public Student findById(String id) {
	
		Document d = studentCollection.find(Filters.eq(MNUMBER, id)).first();
		
		if (d == null)
            return null;
        else
        	return fromDocumentToStudent(d);
	}

	@Override
	public void updateStudyPlan(Student student) {
		studentCollection.replaceOne(Filters.eq(MNUMBER, student.getId()), fromStudentToDocument(student));
		
	}
	
	
	
	// -------------------------------------------------------------------
	private Document fromStudentToDocument(Student student) {
		ArrayList<Document> studyPlan = new ArrayList<>();
		for (Course course : student.getStudyPlan()) {
			studyPlan.add(new Document("courseId", course.getCourseId()).append("courseName", course.getCourseName())
					.append("cfu", course.getCfu()));
		}
		
		return new Document(MNUMBER, student.getId()).append(NAME, student.getName())
				.append(SURNAME, student.getSurname()).append(ID_CDL, student.getIdCdl())
				.append(STUDY_PLAN, studyPlan);
		 
		
	}


	@SuppressWarnings("unchecked")
	private Student fromDocumentToStudent(Document d) {
		
		List<Document> studyPlan = (List<Document>) d.get(STUDY_PLAN);
		Student s = new Student(d.get(MNUMBER).toString(), 
								d.get(NAME).toString(), 
								d.get(SURNAME).toString(),
								d.get(ID_CDL).toString());
		ArrayList<Course> studyPlanList = new ArrayList<>();
		for (Document document : studyPlan) {
			studyPlanList.add(new Course(document.get("courseId").toString(),
					                     document.get("courseName").toString(),
					                     Integer.parseInt(document.get("cfu").toString())));
		}
		s.setStudyPlan(studyPlanList);
		
		return s;
	}

}
