package com.smartstudyplanner.smart_study_planner_backend.util;

import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating task comparators
 * Demonstrates Factory Pattern and Comparator usage
 */
@Component
public class TaskComparatorFactory {

    private final Map<String, Comparator<Task>> comparators = new HashMap<>();

    public TaskComparatorFactory() {
        initializeComparators();
    }

    /**
     * Initialize all available comparators
     */
    private void initializeComparators() {
        // Due date (ascending)
        comparators.put("due_date_asc", Comparator.comparing(Task::getDueDate));

        // Due date (descending)
        comparators.put("due_date_desc", Comparator.comparing(Task::getDueDate).reversed());

        // Priority (high to low)
        comparators.put("priority_desc", (t1, t2) -> {
            Priority p1 = t1.getPriority();
            Priority p2 = t2.getPriority();
            return Integer.compare(p2.getWeight(), p1.getWeight());
        });

        // Priority (low to high)
        comparators.put("priority_asc", (t1, t2) -> {
            Priority p1 = t1.getPriority();
            Priority p2 = t2.getPriority();
            return Integer.compare(p1.getWeight(), p2.getWeight());
        });

        // Completion status (completed first)
        comparators.put("completed_first", Comparator.comparing(Task::isCompleted).reversed());

        // Completion status (incomplete first)
        comparators.put("incomplete_first", Comparator.comparing(Task::isCompleted));

        // Created date (newest first)
        comparators.put("created_desc", new CreatedDateComparator(true));

        // Created date (oldest first)
        comparators.put("created_asc", new CreatedDateComparator(false));

        // Smart sorting (overdue first, then by priority and due date)
        comparators.put("smart", new SmartComparator());
    }

    /**
     * Get a comparator by name
     * @param name comparator name
     * @return comparator or null if not found
     */
    public Comparator<Task> getComparator(String name) {
        return comparators.get(name.toLowerCase());
    }

    /**
     * Inner class: Created date comparator
     */
    private static class CreatedDateComparator implements Comparator<Task> {
        private final boolean descending;

        public CreatedDateComparator(boolean descending) {
            this.descending = descending;
        }

        @Override
        public int compare(Task t1, Task t2) {
            int result = 0;
            return result;
            // resolve this issue
//            int result = t1.getCreatedAt().compareTo(t2.getCreatedAt());
//            return descending ? -result : result;
        }
    }

    /**
     * Inner class: Smart comparator
     * Sorts by overdue status, priority, and due date
     */
    private static class SmartComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            // First, check completion status (incomplete first)
            int completionCompare = Boolean.compare(t1.isCompleted(), t2.isCompleted());
            if (completionCompare != 0) {
                return completionCompare;
            }

            // For incomplete tasks, check overdue status
            if (!t1.isCompleted() && !t2.isCompleted()) {
                LocalDateTime now = LocalDateTime.now();
                boolean o1 = t1.getDueDate().isBefore(now);
                boolean o2 = t2.getDueDate().isBefore(now);

                if (o1 != o2) {
                    return o1 ? -1 : 1; // Overdue tasks first
                }
            }

            // Then by priority (high to low)
            int priorityCompare = Integer.compare(
                    t2.getPriority().getWeight(),
                    t1.getPriority().getWeight());

            if (priorityCompare != 0) {
                return priorityCompare;
            }

            // Finally by due date (sooner first)
            return t1.getDueDate().compareTo(t2.getDueDate());
        }
    }
}
