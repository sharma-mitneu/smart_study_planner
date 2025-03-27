package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.TaskDto;
import com.smartstudyplanner.smart_study_planner_backend.exception.StudyPlannerException;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import com.smartstudyplanner.smart_study_planner_backend.util.ScheduleStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for scheduling and optimizing study tasks
 * Demonstrates Strategy Pattern for different scheduling algorithms
 */
@Service
@RequiredArgsConstructor
public class StudySchedulerService {

    private final TaskRepository taskRepository;
    private final AuthService authService;
    private final TaskService taskService;
    private final ScheduleStrategyFactory strategyFactory;

    /**
     * Generate an optimized schedule for a specific day
     */
    @Transactional(readOnly = true)
    public List<TaskDto> generateDailySchedule(LocalDate date, String strategy, Integer maxHours) {
        User currentUser = authService.getCurrentUser();

        // Validate inputs
        if (date == null) {
            date = LocalDate.now();
        }

        if (maxHours == null || maxHours <= 0) {
            maxHours = 8; // Default to 8 hours
        } else if (maxHours > 16) {
            maxHours = 16; // Cap at 16 hours per day
        }

        // Convert hours to minutes
        int availableMinutes = maxHours * 60;

        // Get all pending tasks for the user
        List<Task> allTasks = taskRepository.findByUserAndCompleted(currentUser, false);

        // Get the scheduling strategy
        ScheduleStrategyFactory.ScheduleStrategy scheduleStrategy =
                strategyFactory.getStrategy(strategy);

        // Apply the strategy to generate a schedule
        List<Task> scheduledTasks = scheduleStrategy.schedule(allTasks, date, availableMinutes);

        // Convert to DTOs
        return scheduledTasks.stream()
                .map(task -> taskService.getTaskById(task.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Generate a weekly study schedule
     */
    @Transactional(readOnly = true)
    public List<List<TaskDto>> generateWeeklySchedule(LocalDate startDate, String strategy, Integer maxHoursPerDay) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }

        List<List<TaskDto>> weeklySchedule = new ArrayList<>();

        // Generate schedule for 7 days
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            List<TaskDto> dailySchedule = generateDailySchedule(currentDate, strategy, maxHoursPerDay);
            weeklySchedule.add(dailySchedule);
        }

        return weeklySchedule;
    }

    /**
     * Create recurring tasks based on a task's recurrence pattern
     */
    @Transactional
    public List<TaskDto> createRecurringTaskInstances(Integer taskId) {
        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new StudyPlannerException("Task not found with id: " + taskId));

        // Security check
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new StudyPlannerException("Access denied");
        }

        // Verify task is recurring
        if (!task.isRecurring()) {
            throw new StudyPlannerException("Task is not recurring");
        }

        // Verify recurrence end date is in the future
        if (task.getRecurrenceEndDate() == null ||
                task.getRecurrenceEndDate().isBefore(LocalDateTime.now())) {
            throw new StudyPlannerException("Invalid recurrence end date");
        }

        List<TaskDto> createdTasks = new ArrayList<>();
        LocalDateTime nextDueDate = task.getDueDate();
        int daysInterval = task.getRecurrenceFrequency().getDaysInterval();

        while (true) {
            // Calculate next due date
            nextDueDate = nextDueDate.plusDays(daysInterval);

            // Stop if we've reached the end date
            if (nextDueDate.isAfter(task.getRecurrenceEndDate())) {
                break;
            }

            // Create a new task instance
            TaskDto newTaskDto = TaskDto.builder()
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .dueDate(nextDueDate)
                    .completed(false)
                    .priority(task.getPriority())
                    .subjectId(task.getSubject().getId())
                    .recurring(false) // Child tasks are not recurring
                    .build();

            TaskDto createdTask = taskService.createTask(newTaskDto);
            createdTasks.add(createdTask);
        }

        return createdTasks;
    }

    /**
     * Suggest optimal time allocation based on task priority and due dates
     */
    @Transactional(readOnly = true)
    public List<TaskDto> suggestPriorityTasks(int limit) {
        User currentUser = authService.getCurrentUser();

        if (limit <= 0) {
            limit = 5; // Default to top 5 tasks
        }

        // Get all pending tasks
        List<Task> pendingTasks = taskRepository.findByUserAndCompleted(currentUser, false);

        // Create a composite comparator for ranking
        Comparator<Task> rankingComparator = createPriorityRankingComparator();

        // Sort tasks by priority ranking
        List<Task> rankedTasks = pendingTasks.stream()
                .sorted(rankingComparator)
                .limit(limit)
                .collect(Collectors.toList());

        // Convert to DTOs
        return rankedTasks.stream()
                .map(task -> taskService.getTaskById(task.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Create a composite comparator for task priority ranking
     * Demonstrates Comparator usage with composition
     */
    private Comparator<Task> createPriorityRankingComparator() {
        // Components of the ranking:
        // 1. Overdue tasks first
        Comparator<Task> overdueComparator = (t1, t2) -> {
            boolean o1 = t1.isOverdue();
            boolean o2 = t2.isOverdue();
            return Boolean.compare(o2, o1); // Reverse to prioritize overdue (true)
        };

        // 2. Priority level (HIGH, MEDIUM, LOW)
        Comparator<Task> priorityComparator = (t1, t2) -> {
            Priority p1 = t1.getPriority();
            Priority p2 = t2.getPriority();
            return Integer.compare(p2.getWeight(), p1.getWeight()); // Reverse for high priority first
        };

        // 3. Due date (sooner deadlines first)
        Comparator<Task> dueDateComparator = Comparator.comparing(Task::getDueDate);

        // Combine comparators with thenComparing
        return overdueComparator
                .thenComparing(priorityComparator)
                .thenComparing(dueDateComparator);
    }

    /**
     * Balance study load across subjects
     */
    @Transactional(readOnly = true)
    public List<TaskDto> balanceStudyLoad(int tasksPerSubject) {
        User currentUser = authService.getCurrentUser();

        if (tasksPerSubject <= 0) {
            tasksPerSubject = 2; // Default to 2 tasks per subject
        }

        // Get all pending tasks grouped by subject
        List<Task> pendingTasks = taskRepository.findByUserAndCompleted(currentUser, false);

        // Group tasks by subject
        Map<Integer, List<Task>> tasksBySubject = pendingTasks.stream()
                .collect(Collectors.groupingBy(task -> task.getSubject().getId()));

        // Create due date comparator
        Comparator<Task> dueDateComparator = Comparator.comparing(Task::getDueDate);

        // Select top N tasks per subject
        int finalTasksPerSubject = tasksPerSubject;
        List<Task> balancedTasks = tasksBySubject.values().stream()
                .flatMap(tasks -> tasks.stream()
                        .sorted(dueDateComparator)
                        .limit(finalTasksPerSubject))
                .sorted(dueDateComparator)
                .toList();

        // Convert to DTOs
        return balancedTasks.stream()
                .map(task -> taskService.getTaskById(task.getId()))
                .collect(Collectors.toList());
    }
}
