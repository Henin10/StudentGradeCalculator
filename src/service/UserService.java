package service;

import exception.InvalidInputException;
import model.User;
import util.PasswordUtil;
import util.Validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Phase 2 & 4 - User Service Implementation (CLO2 & CLO3)
 * Implements the UserOperations interface.
 * Provides a thread-safe account cache backed by a persistent JSON store,
 * credential format validation, authentication, and registration.
 * Demonstrates: Interface implementation, polymorphism, @Override, and custom exceptions.
 */
public class UserService implements UserOperations {

    // Thread-safe in-memory user repository
    private static final Map<String, User> USER_DATABASE = new ConcurrentHashMap<>();

    // Load persisted accounts and create the built-in accounts on first run.
    static {
        try {
            for (User user : UserJsonStore.load()) {
                USER_DATABASE.put(user.getUsername().trim().toLowerCase(), user);
            }
            boolean changed = addDefaultAccount(new User(
                    "rahul101", "Rahul@123", "rahul@college.edu", "MCA101", "Rahul Kumar", "Student"));
            changed |= addDefaultAccount(new User(
                    "admin", "Admin@123", "admin@college.edu", "ADMIN01", "System Administrator", "Admin"));
            if (changed) {
                UserJsonStore.save(USER_DATABASE.values());
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Unable to load account data: " + e.getMessage());
        }
    }

    @Override
    public void validateUsername(String username) throws InvalidInputException {
        Validation.validateUsername(username);
    }

    @Override
    public void validatePassword(String password) throws InvalidInputException {
        Validation.validatePassword(password);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        if (username == null) return false;
        return USER_DATABASE.containsKey(username.trim().toLowerCase());
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) return null;
        return USER_DATABASE.get(username.trim().toLowerCase());
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>(USER_DATABASE.values());
        users.sort(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER));
        return users;
    }

    @Override
    public User register(User user) throws InvalidInputException {
        if (user == null) {
            throw new InvalidInputException("User details cannot be empty.");
        }

        // Validate fields
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());

        Validation.validateEmail(user.getEmail());
        Validation.validateStudentId(user.getStudentId());
        Validation.validateStudentName(user.getStudentName());

        String key = user.getUsername().trim().toLowerCase();
        synchronized (USER_DATABASE) {
            if (USER_DATABASE.containsKey(key)) {
                throw new InvalidInputException("Username '" + user.getUsername() + "' is already registered. Please choose another username.");
            }

            for (User existingUser : USER_DATABASE.values()) {
                if (existingUser.getEmail().equalsIgnoreCase(user.getEmail().trim())) {
                    throw new InvalidInputException("That email address is already registered.");
                }
                if (existingUser.getStudentId().equalsIgnoreCase(user.getStudentId().trim())) {
                    throw new InvalidInputException("That student ID is already registered.");
                }
            }

            User registeredUser = storedUser(new User(
                    user.getUsername().trim(),
                    user.getPassword(),
                    user.getEmail().trim(),
                    user.getStudentId().trim(),
                    user.getStudentName().trim(),
                    "Student"
            ));
            registeredUser.setCourse(user.getCourse());
            registeredUser.setSemester(user.getSemester());
            USER_DATABASE.put(key, registeredUser);
            try {
                UserJsonStore.save(USER_DATABASE.values());
            } catch (IOException e) {
                USER_DATABASE.remove(key);
                throw new IllegalStateException("Unable to persist the registered account.", e);
            }
            return registeredUser;
        }
    }

    @Override
    public User login(String username, String password) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username or Student ID cannot be empty.");
        }

        String identifier = username.trim();
        String key = identifier.toLowerCase();
        User existingUser = USER_DATABASE.get(key);
        if (existingUser == null) {
            for (User candidate : USER_DATABASE.values()) {
                if (candidate.getStudentId().equalsIgnoreCase(identifier)) {
                    existingUser = candidate;
                    break;
                }
            }
        }

        if (existingUser != null && existingUser.getStudentId().equalsIgnoreCase(identifier)
                && !existingUser.getUsername().equalsIgnoreCase(identifier)) {
            Validation.validateStudentId(identifier);
        } else {
            validateUsername(identifier);
        }
        validatePassword(password);

        if (existingUser == null || !PasswordUtil.verify(password, existingUser.getPassword())) {
            throw new InvalidInputException("Authentication failed: Invalid username or password.");
        }

        return existingUser;
    }

    private static User storedUser(User user) {
        User stored = new User(
                user.getUsername(),
                PasswordUtil.hash(user.getPassword()),
                user.getEmail(),
                user.getStudentId(),
                user.getStudentName(),
                user.getRole()
        );
        stored.setCourse(user.getCourse());
        stored.setSemester(user.getSemester());
        return stored;
    }

    private static boolean addDefaultAccount(User user) {
        String key = user.getUsername().toLowerCase();
        if (USER_DATABASE.containsKey(key)) {
            return false;
        }
        USER_DATABASE.put(key, storedUser(user));
        return true;
    }
}
