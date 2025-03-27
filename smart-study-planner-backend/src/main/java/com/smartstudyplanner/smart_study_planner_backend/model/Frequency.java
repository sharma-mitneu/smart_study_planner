package com.smartstudyplanner.smart_study_planner_backend.model;

/**
 * Enumeration for recurring task frequency types
 */
public enum Frequency {
    DAILY,
    WEEKLY,
    MONTHLY;

    /**
     * Convert string to Frequency enum safely
     *
     * @param value String representation of frequency
     * @return Frequency enum or default WEEKLY if invalid
     */
    public static Frequency fromString(String value) {
        try {
            return value != null ? valueOf(value.toUpperCase()) : WEEKLY;
        } catch (IllegalArgumentException e) {
            return WEEKLY;
        }
    }
}

