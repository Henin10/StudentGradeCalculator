package model;

/**
 * Module 1 - Student Entity
 * Represents student academic details.
 * Belongs to package 'model' with proper encapsulation.
 */
public class Student {

    // Private attributes
    private String studentId;
    private String studentName;
    private String course;
    private int semester;

    // Default Constructor
    public Student() {
    }

    // Parameterized Constructor
    public Student(String studentId, String studentName, String course, int semester) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.course = course;
        this.semester = semester;
    }

    // Getter for studentId
    public String getStudentId() {
        return studentId;
    }

    // Setter for studentId
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    // Getter for studentName
    public String getStudentName() {
        return studentName;
    }

    // Setter for studentName
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    // Getter for course
    public String getCourse() {
        return course;
    }

    // Setter for course
    public void setCourse(String course) {
        this.course = course;
    }

    // Getter for semester
    public int getSemester() {
        return semester;
    }

    // Setter for semester
    public void setSemester(int semester) {
        this.semester = semester;
    }

    // Display student information
    public void displayStudent() {
        System.out.println("------------------------------------");
        System.out.println("STUDENT INFORMATION");
        System.out.println("------------------------------------");
        System.out.println("Student ID   : " + studentId);
        System.out.println("Student Name : " + studentName);
        System.out.println("Course       : " + course);
        System.out.println("Semester     : " + semester);
        System.out.println("------------------------------------");
    }
}