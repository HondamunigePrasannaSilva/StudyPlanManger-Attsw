package com.attsw.app.view;

import java.util.List;

import com.attsw.app.model.Course;

public interface StudyPlanView {
	
	void showStudyPlan(List<Course> courses);
	void showError(String message);
	void courseAdded(Course course);
	void courseRemoved(Course course);

}

