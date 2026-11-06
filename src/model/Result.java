package model;

/**
 * Module 1 - Result Entity
 * Encapsulates complete academic result for a student.
 * Belongs to package 'model' with proper encapsulation.
 */
public class Result {

    // Private attributes
    private Student student;
    private Subject[] subjects;
    private double totalMarks;
    private double maxMarks;
    private double percentage;
    private String grade;
    private String status;

    // Default Constructor
    public Result() {
    }

    // Parameterized Constructor
    public Result(Student student, Subject[] subjects, double totalMarks,
                  double maxMarks, double percentage, String grade, String status) {
        this.student = student;
        this.subjects = subjects;
        this.totalMarks = totalMarks;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.grade = grade;
        this.status = status;
    }

    // Getters and Setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject[] getSubjects() {
        return subjects;
    }

    public void setSubjects(Subject[] subjects) {
        this.subjects = subjects;
    }

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Method to display official academic result card
    public void displayResult() {
        System.out.println("\n===============================================================");
        System.out.println("                 OFFICIAL ACADEMIC REPORT CARD                 ");
        System.out.println("===============================================================");
        if (student != null) {
            student.displayStudent();
        }

        System.out.println("SUBJECT-WISE PERFORMANCE:");
        System.out.println("---------------------------------------------------------------");
        if (subjects != null) {
            for (Subject sub : subjects) {
                if (sub != null) {
                    sub.displaySubject();
                }
            }
        }
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-25s : %.2f / %.2f%n", "Total Marks Obtained", totalMarks, maxMarks);
        System.out.printf("%-25s : %.2f%%%n", "Overall Percentage", percentage);
        System.out.printf("%-25s : %s%n", "Assigned Grade", grade);
        System.out.printf("%-25s : %s%n", "Academic Status", status);
        System.out.println("===============================================================");
    }
}

