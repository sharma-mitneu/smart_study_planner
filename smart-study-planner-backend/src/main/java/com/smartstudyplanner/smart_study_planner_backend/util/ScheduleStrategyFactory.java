package com.smartstudyplanner.smart_study_planner_backend.util;


import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory for different scheduling strategies
 * Demonstrates Strategy Pattern
 */
@Component
public class ScheduleStrategyFactory {

    private final Map<String, ScheduleStrategy> strategies = new HashMap<>();

    public ScheduleStrategyFactory() {
        initializeStrategies();
    }

    /**
     * Initialize all available scheduling strategies
     */
    private void initializeStrategies() {
        strategies.put("deadline", new DeadlineFirstStrategy());
        strategies.put("priority", new PriorityFirstStrategy());
        strategies.put("balanced", new BalancedStrategy());
        strategies.put("overdue", new OverdueFirstStrategy());
    }

    /**
     * Get a scheduling strategy by name
     * @param name strategy name (default to balanced if not found)
     * @return scheduling strategy
     */
    public ScheduleStrategy getStrategy(String name) {
        if (name == null || name.isEmpty() || !strategies.containsKey(name.toLowerCase())) {
            return strategies.get("balanced"); // Default strategy
        }
        return strategies.get(name.toLowerCase());
    }

    /**
     * Interface for scheduling strategies
     */
    public interface ScheduleStrategy {
        /**
         * Schedule tasks for a specific day
         * @param tasks list of available tasks
         * @param date target date
         * @param availableMinutes available time in minutes
         * @return list of scheduled tasks
         */
        List<Task> schedule(List<Task> tasks, LocalDate date, int availableMinutes);
    }

    /**
     * Strategy: Prioritize tasks with closest deadlines
     */
    private static class DeadlineFirstStrategy implements ScheduleStrategy {
        @Override
        public List<Task> schedule(List<Task> tasks, LocalDate date, int availableMinutes) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusSeconds(1);

            // Filter tasks due on or before the target date + 3 days
            List<Task> eligibleTasks = tasks.stream()
                    .filter(task -> !task.isCompleted())
                    .filter(task -> task.getDueDate().isBefore(endOfDay.plusDays(3)))
                    .sorted(Comparator.comparing(Task::getDueDate)) // Sort by due date ascending
                    .collect(Collectors.toList());

            return selectTasksForSchedule(eligibleTasks, availableMinutes);
        }
    }

    /**
     * Strategy: Prioritize tasks with highest priority
     */
    private static class PriorityFirstStrategy implements ScheduleStrategy {
        @Override
        public List<Task> schedule(List<Task> tasks, LocalDate date, int availableMinutes) {
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusSeconds(1);

            // Sort by priority (high to low) then by due date (ascending)
            List<Task> eligibleTasks = tasks.stream()
                    .filter(task -> !task.isCompleted())
                    .sorted(
                            Comparator.comparing((Task t) -> t.getPriority().getWeight()).reversed()
                                    .thenComparing(Task::getDueDate)
                    )
                    .collect(Collectors.toList());

            return selectTasksForSchedule(eligibleTasks, availableMinutes);
        }
    }

    /**
     * Strategy: Balance between priority and deadline
     */
    private static class BalancedStrategy implements ScheduleStrategy {
        @Override
        public List<Task> schedule(List<Task> tasks, LocalDate date, int availableMinutes) {
            LocalDateTime now = LocalDateTime.now();

            // Create a composite score for each task
            List<ScoredTask> scoredTasks = tasks.stream()
                    .filter(task -> !task.isCompleted())
                    .map(task -> {
                        // Calculate priority score (0-100)
                        int priorityScore = calculatePriorityScore(task.getPriority());

                        // Calculate deadline score (0-100)
                        int deadlineScore = calculateDeadlineScore(task.getDueDate(), now);

                        // Calculate combined score (higher is more urgent)
                        int combinedScore = (priorityScore + deadlineScore) / 2;

                        return new ScoredTask(task, combinedScore);
                    })
                    .sorted(Comparator.comparing(ScoredTask::getScore).reversed())
                    .collect(Collectors.toList());

            // Extract tasks in score order
            List<Task> eligibleTasks = scoredTasks.stream()
                    .map(ScoredTask::getTask)
                    .collect(Collectors.toList());

            return selectTasksForSchedule(eligibleTasks, availableMinutes);
        }

        private int calculatePriorityScore(Priority priority) {
            switch (priority) {
                case HIGH: return 100;
                case MEDIUM: return 60;
                case LOW: return 30;
                default: return 0;
            }
        }

        private int calculateDeadlineScore(LocalDateTime dueDate, LocalDateTime now) {
            // Calculate days until due
            long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(now, dueDate);

            // Score based on days until due
            if (daysUntilDue < 0) {
                return 100; // Overdue
            } else if (daysUntilDue == 0) {
                return 90; // Due today
            } else if (daysUntilDue <= 1) {
                return 80; // Due tomorrow
            } else if (daysUntilDue <= 3) {
                return 70; // Due in 2-3 days
            } else if (daysUntilDue <= 7) {
                return 50; // Due this week
            } else if (daysUntilDue <= 14) {
                return 30; // Due next week
            } else {
                return 10; // Due later
            }
        }

        // Helper class to store task with score
        private static class ScoredTask {
            private final Task task;
            private final int score;

            public ScoredTask(Task task, int score) {
                this.task = task;
                this.score = score;
            }

            public Task getTask() {
                return task;
            }

            public int getScore() {
                return score;
            }
        }
    }

    /**
     * Strategy: Prioritize overdue tasks
     */
    private static class OverdueFirstStrategy implements ScheduleStrategy {
        @Override
        public List<Task> schedule(List<Task> tasks, LocalDate date, int availableMinutes) {
            LocalDateTime now = LocalDateTime.now();

            // Separate overdue and non-overdue tasks
            List<Task> overdueTasks = tasks.stream()
                    .filter(task -> !task.isCompleted())
                    .filter(task -> task.getDueDate().isBefore(now))
                    .sorted(Comparator.comparing(Task::getDueDate))
                    .collect(Collectors.toList());

            List<Task> upcomingTasks = tasks.stream()
                    .filter(task -> !task.isCompleted())
                    .filter(task -> !task.getDueDate().isBefore(now))
                    .sorted(Comparator.comparing(Task::getDueDate))
                    .collect(Collectors.toList());

            // Prioritize overdue tasks, then upcoming tasks
            List<Task> eligibleTasks = new ArrayList<>(overdueTasks);
            eligibleTasks.addAll(upcomingTasks);

            return selectTasksForSchedule(eligibleTasks, availableMinutes);
        }
    }

    /**
     * Helper method to select tasks for schedule based on available time
     */
    private static List<Task> selectTasksForSchedule(List<Task> eligibleTasks, int availableMinutes) {
        List<Task> scheduledTasks = new ArrayList<>();
        int remainingMinutes = availableMinutes;

        // Estimate 60 minutes per task as a default if not specified
        int defaultTaskMinutes = 60;

        for (Task task : eligibleTasks) {
            // Stop if we've run out of time
            if (remainingMinutes <= 0) {
                break;
            }

            // Estimate task time (could enhance this in future)
            int taskMinutes = defaultTaskMinutes;

            // Add task if we have time
            if (taskMinutes <= remainingMinutes) {
                scheduledTasks.add(task);
                remainingMinutes -= taskMinutes;
            }
        }

        return scheduledTasks;
    }
}
