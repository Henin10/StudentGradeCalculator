package model;

import java.io.Serializable;

/**
 * Phase 1 - User Entity (CLO1)
 * Represents an application user (e.g., student/admin) with login credentials.
 * Implements encapsulation, comprehensive constructors, getters/setters,
 * and JSON data representation as required by Phase 1 & Phase 3.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Private attributes (Encapsulation)
    private String username;
    private String password;
    private String email;
    private String studentId;
    private String studentName;
    private String role; // "Student" or "Admin"
    private String course = "MCA";
    private int semester = 1;

    // Default Constructor
    public User() {
        this.role = "Student";
    }

    // Two-parameter Constructor (legacy & basic login)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "Student";
    }

    // Full Parameterized Constructor
    public User(String username, String password, String email, String studentId, String studentName, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.studentId = studentId;
        this.studentName = studentName;
        this.role = (role != null && !role.trim().isEmpty()) ? role : "Student";
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    // Display user details (masks password)
    public void displayUser() {
        System.out.println("------------------------------------");
        System.out.println("USER DETAILS");
        System.out.println("------------------------------------");
        System.out.println("Username     : " + username);
        System.out.println("Student Name : " + (studentName != null ? studentName : "N/A"));
        System.out.println("Student ID   : " + (studentId != null ? studentId : "N/A"));
        System.out.println("Email        : " + (email != null ? email : "N/A"));
        System.out.println("Role         : " + (role != null ? role : "Student"));
        System.out.println("Password     : ********");
        System.out.println("------------------------------------");
    }

    /**
     * Phase 3 Requirement: Represent user data format using JSON (simple structure).
     * Serializes user attributes into a valid JSON string without external libraries.
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"username\":\"").append(escapeJson(username)).append("\",");
        sb.append("\"studentId\":\"").append(escapeJson(studentId != null ? studentId : "")).append("\",");
        sb.append("\"studentName\":\"").append(escapeJson(studentName != null ? studentName : "")).append("\",");
        sb.append("\"email\":\"").append(escapeJson(email != null ? email : "")).append("\",");
        sb.append("\"role\":\"").append(escapeJson(role != null ? role : "Student")).append("\",");
        sb.append("\"course\":\"").append(escapeJson(course != null ? course : "MCA")).append("\",");
        sb.append("\"semester\":").append(semester);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Helper to escape special JSON characters
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
