package service;

import model.Result;
import model.Student;
import model.Subject;

/**
 * Module 6 - Result Service
 * Handles calculation of total marks, percentage, grade, and academic status.
 * Constructs and returns complete Result objects.
 * Demonstrates: Encapsulating business logic in dedicated service classes.
 */
public class ResultService {

    /**
     * Calculates the sum of marks from an array of subjects.
     */
    public double calculateTotal(Subject[] subjects) {
        double total = 0.0;
        if (subjects != null) {
            for (Subject sub : subjects) {
                if (sub != null) {
                    total += sub.getMarks();
                }
            }
        }
        return total;
    }

    /**
     * Calculates the percentage obtained.
     */
    public double calculatePercentage(double totalMarks, int totalSubjects) {
        if (totalSubjects <= 0) {
            return 0.0;
        }
        return (totalMarks / (totalSubjects * 100.0)) * 100.0;
    }

    /**
     * Determines letter grade based on percentage:
     * 90–100 -> A+
     * 80–89.99 -> A
     * 70–79.99 -> B
     * 60–69.99 -> C
     * 50–59.99 -> D
     * Below 50 -> F
     */
    public String calculateGrade(double percentage) {
        if (percentage >= 90.0) {
            return "A+";
        } else if (percentage >= 80.0) {
            return "A";
        } else if (percentage >= 70.0) {
            return "B";
        } else if (percentage >= 60.0) {
            return "C";
        } else if (percentage >= 50.0) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Determines pass/fail status:
     * Passes if every subject mark >= 40.0 and aggregate percentage >= 50.0%.
     */
    public String determinePassFail(Subject[] subjects, double percentage) {
        if (subjects != null) {
            for (Subject sub : subjects) {
                if (sub != null && sub.getMarks() < 40.0) {
                    return "FAIL (Marks < 40 in " + sub.getSubjectName() + ")";
                }
            }
        }

        if (percentage >= 50.0) {
            return "PASS";
        } else {
            return "FAIL (Overall percentage below 50%)";
        }
    }

    /**
     * Generates a complete Result entity for a student.
     */
    public Result generateResult(Student student, Subject[] subjects) {
        int totalSubjects = (subjects != null) ? subjects.length : 0;
        double totalMarks = calculateTotal(subjects);
        double maxMarks = totalSubjects * 100.0;
        double percentage = calculatePercentage(totalMarks, totalSubjects);
        String grade = calculateGrade(percentage);
        String status = determinePassFail(subjects, percentage);

        return new Result(student, subjects, totalMarks, maxMarks, percentage, grade, status);
    }
}

