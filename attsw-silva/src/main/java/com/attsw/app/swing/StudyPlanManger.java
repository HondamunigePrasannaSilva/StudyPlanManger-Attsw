package com.attsw.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.attsw.app.controller.StudentController;
import com.attsw.app.repository.mongo.CourseMongoRepository;
import com.attsw.app.repository.mongo.StudentMongoRepository;
import com.attsw.app.view.swing.StudyPlanViewSwing;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(mixinStandardHelpOptions = true)
public class StudyPlanManger implements Callable<Void>{

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	 
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "studyplan";
	
	@Option(names = { "--db-student" }, description = "Collection name")
	private String stcollectionName = "student";
	
	@Option(names = { "--db-course" }, description = "Collection name")
	private String coursecollectionName = "course";
	
	
	public static void main(String[] args) {
		new CommandLine(new StudyPlanManger()).execute(args);
	}
	
	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				
				StudentMongoRepository studentRepository = new StudentMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, stcollectionName);
				
				CourseMongoRepository courseRepository = new CourseMongoRepository(
						new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, coursecollectionName);
			
				StudyPlanViewSwing studyplanview = new StudyPlanViewSwing();
				StudentController schoolController = new StudentController(studentRepository, courseRepository, studyplanview);
				
				studyplanview.setSudyPlanController(schoolController);
				studyplanview.setVisible(true);
			
			} catch (Exception e) {
				Logger.getLogger(getClass().getName())
				.log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}

}
