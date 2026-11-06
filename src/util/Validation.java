package util;

import exception.InvalidInputException;

/**
 * Phase 1 & 2 - Validation Utility Class (CLO1 & CLO2)
 * Demonstrates: String handling, input validation, and throwing custom InvalidInputException.
 * Used for both client and server-side validation.
 */
public class Validation {

    /**
     * Validates that username is not empty and has at least 5 characters.
     */
    public static void validateUsername(String username) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        if (username.trim().length() < 5) {
            throw new InvalidInputException("Username must contain at least 5 characters.");
        }
    }

    /**
     * Validates that password is at least 8 characters and contains at least one numeric digit.
     */
    public static void validatePassword(String password) throws InvalidInputException {
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }
        if (password.length() < 8) {
            throw new InvalidInputException("Password must contain at least 8 characters.");
        }

        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
                break;
            }
        }
        if (!hasDigit) {
            throw new InvalidInputException("Password must contain at least one numeric digit (0-9).");
        }
    }

    /**
     * Validates email address format using string handling.
     */
    public static void validateEmail(String email) throws InvalidInputException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty.");
        }
        String trimmed = email.trim();
        int atIndex = trimmed.indexOf('@');
        int lastDotIndex = trimmed.lastIndexOf('.');
        if (atIndex < 1 || lastDotIndex <= atIndex + 1 || lastDotIndex >= trimmed.length() - 1) {
            throw new InvalidInputException("Invalid email format (e.g., student@example.com).");
        }
    }

    /**
     * Validates student ID.
     */
    public static void validateStudentId(String studentId) throws InvalidInputException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new InvalidInputException("Student ID cannot be empty.");
        }
        if (studentId.trim().length() < 3) {
            throw new InvalidInputException("Student ID must contain at least 3 characters.");
        }
    }

    /**
     * Validates student name.
     */
    public static void validateStudentName(String studentName) throws InvalidInputException {
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new InvalidInputException("Student Name cannot be empty.");
        }
    }

    /**
     * Validates academic course.
     */
    public static void validateCourse(String course) throws InvalidInputException {
        if (course == null || course.trim().isEmpty()) {
            throw new InvalidInputException("Course cannot be empty.");
        }
    }

    /**
     * Validates semester.
     */
    public static void validateSemester(int semester) throws InvalidInputException {
        if (semester < 1 || semester > 8) {
            throw new InvalidInputException("Semester must be between 1 and 8.");
        }
    }

    /**
     * Validates subject marks:
     * - Checks negative marks
     * - Checks overflow marks (> 100)
     */
    public static void validateMarks(double marks) throws InvalidInputException {
        if (marks < 0.0) {
            throw new InvalidInputException("Marks cannot be negative.");
        }
        if (marks > 100.0) {
            throw new InvalidInputException("Marks must be between 0 and 100.");
        }
    }
}
