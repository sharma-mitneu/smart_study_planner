package com.smartstudyplanner.smart_study_planner_backend.model.enums;

public enum RecurrenceFrequency {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    BIWEEKLY("Bi-weekly"),
    MONTHLY("Monthly");

    private final String displayName;

    RecurrenceFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Calculate days between occurrences
     */
    public int getDaysInterval() {
        switch (this) {
            case DAILY: return 1;
            case WEEKLY: return 7;
            case BIWEEKLY: return 14;
            case MONTHLY: return 30;
            default: return 0;
        }
    }
}

