package exception;

/**
 * Module 4 - Custom Exception
 * Thrown when any user or student input violates business validation rules.
 * Extends the standard java.lang.Exception class (Checked Exception).
 */
public class InvalidInputException extends Exception {

    // Default Constructor
    public InvalidInputException() {
        super("Invalid input provided.");
    }

    // Parameterized Constructor accepting custom error message
    public InvalidInputException(String message) {
        super(message);
    }
}

