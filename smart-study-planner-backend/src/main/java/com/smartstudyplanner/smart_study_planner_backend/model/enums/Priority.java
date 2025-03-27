package com.smartstudyplanner.smart_study_planner_backend.model.enums;


/**
 * Represents the priority level
 * Used for both tasks and subjects
 */
public enum Priority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get numerical weight for priority sorting
     */
    public int getWeight() {
        switch (this) {
            case LOW: return 1;
            case MEDIUM: return 2;
            case HIGH: return 3;
            default: return 0;
        }
    }
}

