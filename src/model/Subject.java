package model;

/**
 * Module 1 - Subject Entity
 * Represents an academic subject and scored marks.
 * Belongs to package 'model' with proper encapsulation.
 */
public class Subject {

    // Private attributes
    private String subjectName;
    private double marks;

    // Default Constructor
    public Subject() {
    }

    // Parameterized Constructor
    public Subject(String subjectName, double marks) {
        this.subjectName = subjectName;
        this.marks = marks;
    }

    // Getter for subjectName
    public String getSubjectName() {
        return subjectName;
    }

    // Setter for subjectName
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    // Getter for marks
    public double getMarks() {
        return marks;
    }

    // Setter for marks
    public void setMarks(double marks) {
        this.marks = marks;
    }

    // Display subject details
    public void displaySubject() {
        System.out.printf("%-25s : %.2f / 100%n", subjectName, marks);
    }
}

