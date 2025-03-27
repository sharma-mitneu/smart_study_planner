package com.smartstudyplanner.smart_study_planner_backend.model;

import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.RecurrenceFrequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private boolean completed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean recurring;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency")
    private RecurrenceFrequency recurrenceFrequency;

    @Column(name = "recurrence_end_date")
    private LocalDateTime recurrenceEndDate;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progress> progressEntries = new ArrayList<>();

    /**
     * Add a progress entry to this task
     * @param progress The progress entry to add
     * @return the task for method chaining
     */
    public Task addProgressEntry(Progress progress) {
        progressEntries.add(progress);
        progress.setTask(this);
        return this;
    }

    /**
     * Remove a progress entry from this task
     * @param progress The progress entry to remove
     * @return the task for method chaining
     */
    public Task removeProgressEntry(Progress progress) {
        progressEntries.remove(progress);
        progress.setTask(null);
        return this;
    }

    /**
     * Calculate total minutes spent on this task
     * @return total minutes spent
     */
    public int getTotalMinutesSpent() {
        return progressEntries.stream()
                .mapToInt(Progress::getMinutesSpent)
                .sum();
    }

    /**
     * Check if task is overdue
     * @return true if task is overdue and not completed
     */
    public boolean isOverdue() {
        return !completed && LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Inner class for custom comparator factory
     */
    public static class Comparators {
        /**
         * Compare tasks by due date (ascending)
         */
        public static class DueDateComparator implements java.util.Comparator<Task> {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
        }

        /**
         * Compare tasks by priority (descending)
         */
        public static class PriorityComparator implements java.util.Comparator<Task> {
            @Override
            public int compare(Task t1, Task t2) {
                return Integer.compare(t2.getPriority().getWeight(),
                        t1.getPriority().getWeight());
            }
        }

        /**
         * Compare tasks by completion status
         */
        public static class CompletionStatusComparator implements java.util.Comparator<Task> {
            @Override
            public int compare(Task t1, Task t2) {
                return Boolean.compare(t1.isCompleted(), t2.isCompleted());
            }
        }
    }
}
