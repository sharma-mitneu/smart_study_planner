package com.smartstudyplanner.smart_study_planner_backend.model.enums;

/**
 * Represents user roles for authorization
 */
public enum UserRole {
    STUDENT("Student"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

