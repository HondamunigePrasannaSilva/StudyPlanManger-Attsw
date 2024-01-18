package com.attsw.app.view.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.attsw.app.controller.StudentController;
import com.attsw.app.model.Course;
import com.attsw.app.model.Student;
import com.attsw.app.view.StudyPlanView;

import javax.swing.border.BevelBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
	
	private JScrollPane scrollPane;
	private StudentController studentcontroller;
	private JLabel lbErrorMsg;
	private DefaultListModel<Course> courseList;
	private JList<Course> clist;
	
	private Student student;
	
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					StudyPlanViewSwing frame = new StudyPlanViewSwing();
					// set the size of the window
					
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public StudyPlanViewSwing() {
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
		txtStudentId.setBounds(184, 180, 151, 40);
		contentPane.add(txtStudentId);
		txtStudentId.setColumns(10);
		txtStudentId.setName("txtStudentId");
		
		btnLogin = new JButton("Login");
		// add action listner when the button is clicked print hello world
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
			}
			else
				lbErrorMsg.setText("Student not found");
		
			
		});
		
		btnLogin.setBounds(382, 179, 117, 40);
		contentPane.add(btnLogin);
		btnLogin.setName("btnLogin");
		
		JLabel lblStudyPlan = new JLabel("STUDY PLAN MANAGEMENT");
		lblStudyPlan.setFont(new Font("Dialog", Font.BOLD, 25));
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
		btnInsertNewCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	
				Course c = studentcontroller.findCourseByNameAndCfu(txtCourseName.getText(), Integer.parseInt(txtcfu.getText()));
				studentcontroller.insertCourseIntoStudyPlan(student, c);
				//if (c == null)
				//	showError("Course not found");
				//else
				//{	
					
				//}
					
				
				
			}
		});
		
	
		
		btnUpdateCourse = new JButton("UPDATE COURSE");
		btnUpdateCourse.setEnabled(false);
		btnUpdateCourse.setBounds(319, 543, 166, 25);
		contentPane.add(btnUpdateCourse);
		btnUpdateCourse.setName("btnUpdateCourse");
		btnUpdateCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Student s = studentcontroller.updateStudyPlan(student, clist.getSelectedValue().getCourseName(),
							clist.getSelectedValue().getCfu(), txtCourseName.getText(),
							Integer.parseInt(txtcfu.getText()));
					
					showStudyPlan(s.getStudyPlan());
				
				

			}
		});
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(604, 142, 18, 461);
		contentPane.add(separator_1);
		
		JLabel lblManagement = new JLabel("Management");
		lblManagement.setHorizontalAlignment(SwingConstants.CENTER);
		lblManagement.setFont(new Font("Dialog", Font.BOLD, 21));
		lblManagement.setBounds(144, 276, 313, 67);
		contentPane.add(lblManagement);
		lblManagement.setName("lblManagement");
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setFont(new Font("Dialog", Font.BOLD, 21));
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
		lblStudyPlan_1.setFont(new Font("Dialog", Font.BOLD, 21));
		lblStudyPlan_1.setBounds(739, 101, 313, 67);
		contentPane.add(lblStudyPlan_1);
		lblStudyPlan_1.setName("lblStudyPlan_1");
		
		lbErrorMsg = new JLabel(" ");
		lbErrorMsg.setFont(new Font("Dialog", Font.BOLD, 14));
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
		btnRemoveSelectedCourse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// get the selected item from the scrol pane
				String coursename = clist.getSelectedValue().getCourseName();
				String cfu = String.valueOf(clist.getSelectedValue().getCfu());
				Course c = studentcontroller.findCourseByNameAndCfu(coursename, Integer.parseInt(cfu));
			    studentcontroller.removeCourseFromStudyPlan(student, c);
				
				
				
			}
		});
	}

	public void setSudyPlanController(StudentController studentcontroller) {
		// TODO Auto-generated method stub
		this.studentcontroller = studentcontroller;
		
	}
	
	

	DefaultListModel<Course> getCourseList() {
		
		return courseList;
	}

	@Override
	public void showStudyPlan(List<Course> courses) {
		// TODO Auto-generated method stub
		// add each course to the list
		courseList.clear();
		courses.forEach(courseList::addElement);
		//resetLabel();
		
	}

	@Override
	public void showError(String message) {
		lbErrorMsg.setText(message);
		
	}

	@Override
	public void CourseAdded(Course course) {
		
		courseList.addElement(course);
		resetLabel();
		
	}

	@Override
	public void CourseRemoved(Course course) {
		
		courseList.removeElement(course);
		resetLabel();
		
	}
	private void resetLabel()
	{
		lbErrorMsg.setText(" ");
	}
	
}
