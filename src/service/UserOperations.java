package service;

import exception.InvalidInputException;
import model.User;
import java.util.List;

/**
 * Phase 2 - User Operations Interface (CLO2)
 * Defines contracts for user authentication, registration, credential validation,
 * and session/user lookup.
 * Demonstrates: Java Interface abstraction, polymorphism, and checked exceptions.
 */
public interface UserOperations {

    /**
     * Validates that username is not empty and meets length criteria.
     * @param username The username to validate
     * @throws InvalidInputException if username is invalid
     */
    void validateUsername(String username) throws InvalidInputException;

    /**
     * Validates that password meets length and contains required characters.
     * @param password The password to validate
     * @throws InvalidInputException if password is invalid
     */
    void validatePassword(String password) throws InvalidInputException;

    /**
     * Performs authentication using username and password.
     * @param username The username
     * @param password The password
     * @return Authenticated User object
     * @throws InvalidInputException if credentials are invalid
     */
    User login(String username, String password) throws InvalidInputException;

    /**
     * Registers a new user into the application database.
     * @param user The new User entity to register
     * @return Registered User object
     * @throws InvalidInputException if data is invalid or username already exists
     */
    User register(User user) throws InvalidInputException;

    /**
     * Checks if a username already exists.
     * @param username The username to query
     * @return true if taken, false otherwise
     */
    boolean isUsernameTaken(String username);

    /**
     * Retrieves user by username.
     * @param username The username
     * @return User object or null if not found
     */
    User getUserByUsername(String username);

    /**
     * Retrieves a snapshot of registered users.
     * @return Registered users
     */
    List<User> getAllUsers();
}
