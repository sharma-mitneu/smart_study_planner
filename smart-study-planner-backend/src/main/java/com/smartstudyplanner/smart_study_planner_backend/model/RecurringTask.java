package com.smartstudyplanner.smart_study_planner_backend.model;

//import com.smartstudyplanner.smart_study_planner_backend.model.enums.Frequency;

import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Extended Task class for recurring tasks
 */
public class RecurringTask extends Task {
    private Frequency frequency;
    private LocalDateTime endDate;

    public RecurringTask() {
        super();
        this.frequency = Frequency.WEEKLY;
    }

    public RecurringTask(String title, String description, LocalDateTime dueDate,
                         Priority priority, Long subjectId, Long userId,
                         Frequency frequency, LocalDateTime endDate) {
        super(title, description, dueDate, priority, subjectId, userId);
        this.frequency = frequency;
        this.endDate = endDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     * Generate the next occurrence of this recurring task
     *
     * @return A new Task instance with updated due date
     */
    public Task generateNextOccurrence() {
        LocalDateTime nextDueDate = calculateNextDueDate();

        // Check if we've reached the end date
        if (endDate != null && nextDueDate.isAfter(endDate)) {
            return null;
        }

        Task nextTask = new Task(
                getTitle(),
                getDescription(),
                nextDueDate,
                getPriority(),
                getSubjectId(),
                getUserId()
        );

        return nextTask;
    }

    /**
     * Calculate the next due date based on frequency
     *
     * @return The next due date
     */
    private LocalDateTime calculateNextDueDate() {
        LocalDateTime currentDueDate = getDueDate();

        switch (frequency) {
            case DAILY:
                return currentDueDate.plus(1, ChronoUnit.DAYS);
            case WEEKLY:
                return currentDueDate.plus(1, ChronoUnit.WEEKS);
            case MONTHLY:
                return currentDueDate.plus(1, ChronoUnit.MONTHS);
            default:
                return currentDueDate.plus(1, ChronoUnit.WEEKS);
        }
    }

    @Override
    public String toString() {
        return "RecurringTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", dueDate=" + getDueDate() +
                ", frequency=" + frequency +
                ", endDate=" + endDate +
                ", priority=" + getPriority() +
                '}';
    }
}
