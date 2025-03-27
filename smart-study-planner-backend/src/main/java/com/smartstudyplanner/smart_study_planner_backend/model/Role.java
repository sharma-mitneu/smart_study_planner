package com.smartstudyplanner.smart_study_planner_backend.model;

/**
 * Enumeration for user roles in the system
 */
public enum Role {
    ADMIN,
    STUDENT;

    /**
     * Convert string to Role enum safely
     *
     * @param value String representation of role
     * @return Role enum or default STUDENT if invalid
     */
    public static Role fromString(String value) {
        try {
            return value != null ? valueOf(value.toUpperCase()) : STUDENT;
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}

