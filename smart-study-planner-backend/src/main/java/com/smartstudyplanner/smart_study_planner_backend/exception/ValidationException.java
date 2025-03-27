package com.smartstudyplanner.smart_study_planner_backend.exception;

/**
 * Exception for data validation errors
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
