package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.TaskDto;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import com.smartstudyplanner.smart_study_planner_backend.exception.StudyPlannerException;
import com.smartstudyplanner.smart_study_planner_backend.model.Progress;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectEnrollmentRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import com.smartstudyplanner.smart_study_planner_backend.util.TaskComparatorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;
    private final AuthService authService;
    private final SubjectEnrollmentRepository subjectEnrollmentRepository;
    private final TaskComparatorFactory taskComparatorFactory;

    /**
     * Get all tasks for current user
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks(String sortBy) {
        User currentUser = authService.getCurrentUser();
        List<Task> tasks = taskRepository.findByUser(currentUser);

        // Apply sorting if specified
        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Task> comparator = taskComparatorFactory.getComparator(sortBy);
            if (comparator != null) {
                tasks.sort(comparator);
            }
        }

        return mapTasksToDto(tasks);
    }

    /**
     * Get tasks by subject ID
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksBySubject(Integer subjectId, String sortBy) {
        User currentUser = authService.getCurrentUser();

        // Verify subject exists and belongs to user
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        if (!subject.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subject not found with id: " + subjectId);
        }

        List<Task> tasks = taskRepository.findBySubjectId(subjectId);

        // Apply sorting if specified
        if (sortBy != null && !sortBy.isEmpty()) {
            Comparator<Task> comparator = taskComparatorFactory.getComparator(sortBy);
            if (comparator != null) {
                tasks.sort(comparator);
            }
        }

        return mapTasksToDto(tasks);
    }

    /**
     * Get upcoming tasks (due in the next 7 days)
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getUpcomingTasks() {
        User currentUser = authService.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        List<Task> tasks = taskRepository.findByUserAndCompletedAndDueDateBetween(
                currentUser, false, now, sevenDaysLater);

        // Sort by due date
        tasks.sort(Comparator.comparing(Task::getDueDate));

        return mapTasksToDto(tasks);
    }

    /**
     * Get overdue tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getOverdueTasks() {
        User currentUser = authService.getCurrentUser();
        List<Task> tasks = taskRepository.findOverdueTasks(currentUser.getId());

        // Sort by due date
        tasks.sort(Comparator.comparing(Task::getDueDate));

        return mapTasksToDto(tasks);
    }

    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Integer id) {
        User currentUser = authService.getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Security check: ensure task belongs to current user
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return mapTaskToDto(task);
    }

    /**
     * Check if student is enrolled in a subject
     */
    private boolean isStudentEnrolledInSubject(Integer studentId, Integer subjectId) {
        return subjectEnrollmentRepository
                .existsByStudentIdAndSubjectId(studentId, subjectId);
    }

    /**
     * Create a new task
     */
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        User currentUser = authService.getCurrentUser();

        // Verify subject exists
        Subject subject = subjectRepository.findById(taskDto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        // Check student enrollment
        if (!isStudentEnrolledInSubject(currentUser.getId(), subject.getId())) {
            throw new StudyPlannerException("You must be enrolled in the subject to create tasks");
        }

        // Validate task priority (mandatory for students)
        if (taskDto.getPriority() == null) {
            throw new StudyPlannerException("Task priority is required");
        }

        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .priority(taskDto.getPriority())
                .subject(subject)
                .user(currentUser)
                .completed(false)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapTaskToDto(savedTask);
    }

    /**
     * Update an existing task
     */
    @Transactional
    public TaskDto updateTask(Integer id, TaskDto taskDto) {
        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Security check: ensure task belongs to current user
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        // Check if subject is changed
        if (!task.getSubject().getId().equals(taskDto.getSubjectId())) {
            Subject newSubject = subjectRepository.findById(taskDto.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + taskDto.getSubjectId()));

            // Ensure new subject belongs to user
            if (!newSubject.getUser().getId().equals(currentUser.getId())) {
                throw new ResourceNotFoundException("Subject not found with id: " + taskDto.getSubjectId());
            }

            task.setSubject(newSubject);
        }

        // Validate recurrence data
        if (taskDto.isRecurring()) {
            if (taskDto.getRecurrenceFrequency() == null) {
                throw new StudyPlannerException("Recurrence frequency is required for recurring tasks");
            }
            if (taskDto.getRecurrenceEndDate() == null) {
                throw new StudyPlannerException("Recurrence end date is required for recurring tasks");
            }
            if (taskDto.getRecurrenceEndDate().isBefore(taskDto.getDueDate())) {
                throw new StudyPlannerException("Recurrence end date must be after due date");
            }
        }

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setCompleted(taskDto.isCompleted());
        task.setPriority(taskDto.getPriority());
        task.setRecurring(taskDto.isRecurring());
        task.setRecurrenceFrequency(taskDto.getRecurrenceFrequency());
        task.setRecurrenceEndDate(taskDto.getRecurrenceEndDate());

        Task updatedTask = taskRepository.save(task);

        return mapTaskToDto(updatedTask);
    }

    /**
     * Delete a task
     */
    @Transactional
    public void deleteTask(Integer id) {
        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Security check: ensure task belongs to current user
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        taskRepository.delete(task);
    }

    /**
     * Mark a task as completed
     */
    @Transactional
    public TaskDto markTaskAsCompleted(Integer id) {
        User currentUser = authService.getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Security check: ensure task belongs to current user
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);

        return mapTaskToDto(updatedTask);
    }

    /**
     * Map Task entities to TaskDto
     */
    private List<TaskDto> mapTasksToDto(List<Task> tasks) {
        return tasks.stream()
                .map(this::mapTaskToDto)
                .collect(Collectors.toList());
    }

    /**
     * Map Task entity to TaskDto
     */
    private TaskDto mapTaskToDto(Task task) {
        // Calculate if task is overdue
        boolean isOverdue = !task.isCompleted() && LocalDateTime.now().isAfter(task.getDueDate());

        // Calculate total minutes spent

        // Calculate total minutes spent (handle null case)
        Integer totalMinutesSpent = task.getProgressEntries() != null
                ? task.getProgressEntries().stream().mapToInt(Progress::getMinutesSpent).sum()
                : 0;


//        Integer totalMinutesSpent = task.getProgressEntries().stream()
//                .mapToInt(p -> p.getMinutesSpent())
//                .sum();

        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .completed(task.isCompleted())
                .priority(task.getPriority())
                .subjectId(task.getSubject().getId())
                .subjectName(task.getSubject().getName())
                .recurring(task.isRecurring())
                .recurrenceFrequency(task.getRecurrenceFrequency())
                .recurrenceEndDate(task.getRecurrenceEndDate())
                .overdue(isOverdue)
                .totalMinutesSpent(totalMinutesSpent)
                .build();
    }
}

