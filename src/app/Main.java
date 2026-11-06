package app;

import exception.InvalidInputException;
import model.Result;
import model.Student;
import model.Subject;
import model.User;
import service.ResultService;
import service.UserOperations;
import service.UserService;
import util.Validation;

import java.util.Scanner;

/**
 * Module 7 - Main Application
 * Belongs to package 'app'.
 * Demonstrates: Package organization, Interface polymorphism (UserOperations),
 * Custom exception handling (try-catch with InvalidInputException),
 * and separation of concerns via service layers.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Service instances demonstrating Interface Polymorphism
        UserOperations userOperations = new UserService();
        ResultService resultService = new ResultService();

        // 1. Display Header Banner
        System.out.println("===============================================================");
        System.out.println("         STUDENT GRADE CALCULATOR MANAGEMENT SYSTEM            ");
        System.out.println("              Phase 2 - Structured Architecture                ");
        System.out.println("===============================================================\n");

        // 2. Authentication with Strict Credential & Format Validation
        System.out.println(">>> STEP 1: USER AUTHENTICATION");
        User loggedInUser = null;

        while (loggedInUser == null) {
            System.out.println("1. Sign In (Existing Account)");
            System.out.println("2. Register New User Account");
            System.out.print("Choose option (1 or 2): ");
            String choice = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                System.out.println("\n--- NEW USER REGISTRATION ---");
                try {
                    System.out.print("Enter Full Name        : ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Student ID       : ");
                    String sId = scanner.nextLine().trim();
                    System.out.print("Enter Email            : ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter Username (min 5) : ");
                    String uName = scanner.nextLine().trim();
                    System.out.print("Enter Password (min 8) : ");
                    String pWord = scanner.nextLine().trim();

                    User newUser = new User(uName, pWord, email, sId, name, "Student");
                    loggedInUser = userOperations.register(newUser);
                    System.out.println("\n[SUCCESS] User registered successfully!");
                    loggedInUser.displayUser();
                } catch (InvalidInputException e) {
                    System.out.println("[REGISTRATION ERROR] " + e.getMessage() + "\n");
                }
            } else {
                System.out.print("Enter Username (e.g. rahul101) : ");
                String username = scanner.nextLine().trim();

                System.out.print("Enter Password (e.g. Rahul@123): ");
                String password = scanner.nextLine().trim();

                try {
                    loggedInUser = userOperations.login(username, password);
                    System.out.println("\n[SUCCESS] Authentication successful!");
                    loggedInUser.displayUser();
                } catch (InvalidInputException e) {
                    System.out.println("[INPUT ERROR] " + e.getMessage());
                    System.out.println("Please try again with valid credentials.\n");
                }
            }
        }

        // 3. Student Information Entry with Exception Handling
        System.out.println("\n>>> STEP 2: STUDENT INFORMATION ENTRY");

        String studentId = "";
        while (true) {
            try {
                System.out.print("Enter Student ID (e.g., MCA101 or 101) : ");
                studentId = scanner.nextLine().trim();
                Validation.validateStudentId(studentId);
                break;
            } catch (InvalidInputException e) {
                System.out.println("[INPUT ERROR] " + e.getMessage() + "\n");
            }
        }

        String studentName = "";
        while (true) {
            try {
                System.out.print("Enter Student Name : ");
                studentName = scanner.nextLine().trim();
                Validation.validateStudentName(studentName);
                break;
            } catch (InvalidInputException e) {
                System.out.println("[INPUT ERROR] " + e.getMessage() + "\n");
            }
        }

        String course = "";
        while (true) {
            try {
                System.out.print("Enter Course (e.g., MCA, B.Tech) : ");
                course = scanner.nextLine().trim();
                Validation.validateCourse(course);
                break;
            } catch (InvalidInputException e) {
                System.out.println("[INPUT ERROR] " + e.getMessage() + "\n");
            }
        }

        int semester = 0;
        while (true) {
            try {
                System.out.print("Enter Semester (1 to 8) : ");
                String semInput = scanner.nextLine().trim();
                try {
                    semester = Integer.parseInt(semInput);
                } catch (NumberFormatException nfe) {
                    throw new InvalidInputException("Semester must be a valid number.");
                }
                Validation.validateSemester(semester);
                break;
            } catch (InvalidInputException e) {
                System.out.println("[INPUT ERROR] " + e.getMessage() + "\n");
            }
        }

        // Create Student entity
        Student student = new Student(studentId, studentName, course, semester);

        // 4. Subject Marks Entry with Exception Handling
        System.out.println("\n>>> STEP 3: SUBJECT MARKS ENTRY (Out of 100)");
        String[] subjectNames = {
            "Java Programming",
            "Data Structures",
            "Database Management",
            "Computer Networks",
            "Web Technology"
        };

        Subject[] subjects = new Subject[subjectNames.length];

        for (int i = 0; i < subjectNames.length; i++) {
            while (true) {
                try {
                    System.out.printf("Enter marks for %-22s (0 - 100) : ", subjectNames[i]);
                    String markInput = scanner.nextLine().trim();
                    double mark;
                    try {
                        mark = Double.parseDouble(markInput);
                    } catch (NumberFormatException nfe) {
                        throw new InvalidInputException("Marks must be a valid numerical value.");
                    }

                    // Validate marks range (throws InvalidInputException if < 0 or > 100)
                    Validation.validateMarks(mark);

                    // Successfully validated
                    subjects[i] = new Subject(subjectNames[i], mark);
                    break;

                } catch (InvalidInputException e) {
                    System.out.println("[INPUT ERROR] " + e.getMessage() + "\n");
                }
            }
        }

        // 5. Result Calculation via ResultService
        Result result = resultService.generateResult(student, subjects);

        // 6. Display Academic Report Card
        result.displayResult();

        System.out.println("Phase 2 structured execution completed successfully.");
        scanner.close();
    }
}
