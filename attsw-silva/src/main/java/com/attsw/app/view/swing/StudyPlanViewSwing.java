package com.attsw.app.view.swing;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.attsw.app.controller.StudentController;
import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.view.StudyPlanView;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class StudyPlanViewSwing extends JFrame implements StudyPlanView {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtStudentId;
	private JTextField txtCourseName;
	private JTextField txtcfu;
	private JButton btnLogin;
	private JButton btnRemoveSelectedCourse;
	private JButton btnInsertNewCourse;
	private JButton btnUpdateCourse;
	private JButton btnLogout;
	
	private JScrollPane scrollPane;
	private StudentController studentcontroller;
	private JLabel lbErrorMsg;
	private DefaultListModel<Course> courseList;
	private JList<Course> clist;
	
	private Student student;
	
	
	public static final String FONT = "Dialog";
	public StudyPlanViewSwing() {
		setTitle("Study Plan Manager");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1213, 726);
		// set windows size  
		setSize(1200, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		txtStudentId = new JTextField();
		txtStudentId.addKeyListener(new KeyAdapter() {
			@Override 
			public void keyReleased(KeyEvent e) {
				btnLogin.setEnabled(!txtStudentId.getText().isEmpty() && txtStudentId.getText().trim().length() > 0);
			}
		}); 
		txtStudentId.setHorizontalAlignment(SwingConstants.CENTER);
		txtStudentId.setBounds(144, 180, 151, 40);
		contentPane.add(txtStudentId);
		txtStudentId.setColumns(10);
		txtStudentId.setName("txtStudentId");
		
		btnLogin = new JButton("Login");

		btnLogin.setEnabled(false);
		
		btnLogin.addActionListener(e -> {
			student = studentcontroller.find(txtStudentId.getText());
			if (student != null) 
			{
				
				btnRemoveSelectedCourse.setEnabled(true);
				scrollPane.setEnabled(true);
				btnLogin.setEnabled(false);
				txtStudentId.setEnabled(false);
				showStudyPlan(student.getStudyPlan());
				btnLogout.setEnabled(true);
			}
			else
				lbErrorMsg.setText("Student not found");
		});
		
		btnLogin.setBounds(319, 179, 117, 40);
		contentPane.add(btnLogin);
		btnLogin.setName("btnLogin");
		
		JLabel lblStudyPlan = new JLabel("STUDY PLAN MANAGEMENT");
		lblStudyPlan.setFont(new Font(FONT, Font.BOLD, 25));
		lblStudyPlan.setHorizontalAlignment(SwingConstants.CENTER);
		lblStudyPlan.setBounds(395, 12, 464, 67);
		contentPane.add(lblStudyPlan);
		lblStudyPlan.setName("lblStudyPlan");
		
		txtCourseName = new JTextField();
		txtCourseName.setEnabled(true);
		txtCourseName.setBounds(239, 355, 208, 33);
		contentPane.add(txtCourseName);
		txtCourseName.setColumns(10);
		txtCourseName.setName("txtCourseName");
		
		JLabel lblStudentId = new JLabel("Student ID");
		lblStudentId.setBounds(55, 185, 111, 28);
		contentPane.add(lblStudentId);
		lblStudentId.setName("lblStudentId");
		
		JLabel lblCourseName = new JLabel("Course Name");
		lblCourseName.setBounds(104, 364, 117, 15);
		contentPane.add(lblCourseName);
		lblCourseName.setName("lblCourseName");
		
		JSeparator separator = new JSeparator();
		separator.setBounds(55, 262, 526, 2);
		contentPane.add(separator);
		
		JLabel lblCfu = new JLabel("CFU");
		lblCfu.setBounds(137, 439, 70, 15);
		contentPane.add(lblCfu);
		lblCfu.setName("lblCfu");
		KeyAdapter btnAddEnabler = new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnInsertNewCourse.setEnabled(
					!txtCourseName.getText().trim().isEmpty() && !txtcfu.getText().trim().isEmpty()
				);
			}
		};
		txtCourseName.addKeyListener(btnAddEnabler);
		
		
		txtcfu = new JTextField();
		txtcfu.setEnabled(true);
		
		txtcfu.setBounds(239, 430, 208, 33);
		contentPane.add(txtcfu);
		txtcfu.setColumns(10);
		txtcfu.setName("txtcfu");
		txtcfu.addKeyListener(btnAddEnabler);
		btnInsertNewCourse = new JButton("INSERT NEW COURSE");
		btnInsertNewCourse.setEnabled(false);
		btnInsertNewCourse.setBounds(104, 543, 186, 25);
		contentPane.add(btnInsertNewCourse);
		btnInsertNewCourse.setName("btnInsertNewCourse");
		btnInsertNewCourse.addActionListener(e ->  	
				studentcontroller.insertCourseIntoStudyPlan(student,txtCourseName.getText(), Integer.parseInt(txtcfu.getText()))
		);
		
	
		
		btnUpdateCourse = new JButton("UPDATE COURSE");
		btnUpdateCourse.setEnabled(false);
		btnUpdateCourse.setBounds(319, 543, 166, 25);
		contentPane.add(btnUpdateCourse);
		btnUpdateCourse.setName("btnUpdateCourse");
		btnUpdateCourse.addActionListener(e -> {

				Student s = studentcontroller.updateStudyPlan(student, clist.getSelectedValue().getCourseName(),
							clist.getSelectedValue().getCfu(), txtCourseName.getText(),
							Integer.parseInt(txtcfu.getText()));					
				showStudyPlan(s.getStudyPlan());
		});
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(613, 140, 18, 461);
		contentPane.add(separator_1);
		
		JLabel lblManagement = new JLabel("Management");
		lblManagement.setHorizontalAlignment(SwingConstants.CENTER);
		lblManagement.setFont(new Font(FONT, Font.BOLD, 21));
		lblManagement.setBounds(144, 276, 313, 67);
		contentPane.add(lblManagement);
		lblManagement.setName("lblManagement");
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setFont(new Font(FONT, Font.BOLD, 21));
		lblLogin.setBounds(157, 101, 313, 67);
		contentPane.add(lblLogin);
		lblLogin.setName("lblLogin");
		
		
		scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		scrollPane.setBounds(664, 162, 470, 400);
		contentPane.add(scrollPane);
		scrollPane.setName("scrollPane");
		
		courseList = new DefaultListModel<>();
		clist = new JList<>(courseList);
		clist.addListSelectionListener(e -> {
			btnRemoveSelectedCourse.setEnabled(!clist.isSelectionEmpty());
			btnUpdateCourse.setEnabled(!clist.isSelectionEmpty());
			if (clist.isSelectionEmpty()) {
				txtCourseName.setText("");
				txtcfu.setText("");
			} else {
				txtCourseName.setText(clist.getSelectedValue().getCourseName());
				txtcfu.setText(String.valueOf(clist.getSelectedValue().getCfu()));
				txtCourseName.setEnabled(true);
				txtcfu.setEnabled(true);
			}
			
		});
		
		
		clist.setName("CourseList");
		scrollPane.setViewportView(clist);
		
		
		
		JLabel lblStudyPlan_1 = new JLabel("Study Plan");
		lblStudyPlan_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblStudyPlan_1.setFont(new Font(FONT, Font.BOLD, 21));
		lblStudyPlan_1.setBounds(739, 101, 313, 67);
		contentPane.add(lblStudyPlan_1);
		lblStudyPlan_1.setName("lblStudyPlan_1");
		
		lbErrorMsg = new JLabel(" ");
		lbErrorMsg.setFont(new Font(FONT, Font.BOLD, 14));
		lbErrorMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lbErrorMsg.setForeground(new Color(255, 0, 0));
		lbErrorMsg.setBounds(184, 625, 873, 15);
		contentPane.add(lbErrorMsg);
		lbErrorMsg.setName("lbErrorMsg");
		
		btnRemoveSelectedCourse = new JButton("REMOVE SELECTED COURSE");
		btnRemoveSelectedCourse.setEnabled(false);
		btnRemoveSelectedCourse.setBounds(768, 574, 246, 25);
		contentPane.add(btnRemoveSelectedCourse);
		btnRemoveSelectedCourse.setName("btnRemoveSelectedCourse");
		
		btnLogout = new JButton("Logout");
		btnLogout.setEnabled(false);
		btnLogout.setBounds(453, 179, 117, 41);
		contentPane.add(btnLogout);
		btnLogout.setName("btnLogout");
		btnLogout.addActionListener(e ->{
				student = null;
				btnRemoveSelectedCourse.setEnabled(false);
				
				scrollPane.setEnabled(false);
				btnLogin.setEnabled(true);
				txtStudentId.setEnabled(true);
				txtStudentId.setText("");
				txtCourseName.setText("");
				txtcfu.setText("");
				courseList.clear();
				lbErrorMsg.setText(" ");
				btnLogout.setEnabled(false);
			
		});
		btnRemoveSelectedCourse.addActionListener(e ->{
				String coursename = clist.getSelectedValue().getCourseName();
				int cfu = clist.getSelectedValue().getCfu();
				
			    studentcontroller.removeCourseFromStudyPlan(student,coursename, cfu);
			
		});
	}

	public void setSudyPlanController(StudentController studentcontroller) {
		this.studentcontroller = studentcontroller;
		
	}
	
	

	DefaultListModel<Course> getCourseList() {
		
		return courseList;
	}

	@Override
	public void showStudyPlan(List<Course> courses) {
		courseList.clear();
		courses.forEach(courseList::addElement);

		
	}

	@Override
	public void showError(String message) {
		lbErrorMsg.setText(message);
		
	}

	@Override
	public void courseAdded(Course course) {
		
		courseList.addElement(course);
		resetLabel();
		
	}

	@Override
	public void courseRemoved(Course course) {
		
		courseList.removeElement(course);
		resetLabel();
		
	}
	private void resetLabel()
	{
		lbErrorMsg.setText(" ");
	}
}
