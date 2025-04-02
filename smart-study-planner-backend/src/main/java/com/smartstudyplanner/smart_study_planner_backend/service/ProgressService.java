package com.smartstudyplanner.smart_study_planner_backend.service;


import com.smartstudyplanner.smart_study_planner_backend.dto.ProgressDto;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import com.smartstudyplanner.smart_study_planner_backend.exception.StudyPlannerException;
import com.smartstudyplanner.smart_study_planner_backend.model.Progress;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.repository.ProgressRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final TaskRepository taskRepository;
    private final AuthService authService;

    /**
     * Get all progress entries for current user
     */
    @Transactional(readOnly = true)
    public List<ProgressDto> getAllProgressEntries() {
        User currentUser = authService.getCurrentUser();
        List<Progress> progressEntries = progressRepository.findByUser(currentUser);

        return mapProgressToDto(progressEntries);
    }

    /**
     * Get progress entries by date range
     */
    @Transactional(readOnly = true)
    public List<ProgressDto> getProgressByDateRange(LocalDate startDate, LocalDate endDate) {
        User currentUser = authService.getCurrentUser();
        List<Progress> progressEntries = progressRepository.findByUserAndDateBetween(
                currentUser, startDate, endDate);

        return mapProgressToDto(progressEntries);
    }

    /**
     * Get progress entries for a specific task
     */
    @Transactional(readOnly = true)
    public List<ProgressDto> getProgressByTask(Integer taskId) {
        User currentUser = authService.getCurrentUser();

        // Verify task exists and belongs to user
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }

        List<Progress> progressEntries = progressRepository.findByTask(task);

        return mapProgressToDto(progressEntries);
    }

    /**
     * Get a specific progress entry
     */
    @Transactional(readOnly = true)
    public ProgressDto getProgressById(Integer id) {
        User currentUser = authService.getCurrentUser();

        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress entry not found with id: " + id));

        // Security check: ensure progress belongs to current user
        if (!progress.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Progress entry not found with id: " + id);
        }

        return mapProgressToDto(progress);
    }

    /**
     * Create a new progress entry
     */
    @Transactional
    public ProgressDto createProgress(ProgressDto progressDto) {
        User currentUser = authService.getCurrentUser();

        // Verify task exists and belongs to user
        Task task = taskRepository.findById(progressDto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + progressDto.getTaskId()));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Task not found with id: " + progressDto.getTaskId());
        }

        Progress progress = Progress.builder()
                .date(progressDto.getDate() != null ? progressDto.getDate() : LocalDate.now())
                .minutesSpent(progressDto.getMinutesSpent())
                .notes(progressDto.getNotes())
                .task(task)
                .user(currentUser)
                .build();

        Progress savedProgress = progressRepository.save(progress);

        return mapProgressToDto(savedProgress);
    }

    /**
     * Update an existing progress entry
     */
    @Transactional
    public ProgressDto updateProgress(Integer id, ProgressDto progressDto) {
        User currentUser = authService.getCurrentUser();

        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress entry not found with id: " + id));

        // Security check: ensure progress belongs to current user
        if (!progress.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Progress entry not found with id: " + id);
        }

        // Check if task is changed
        if (!progress.getTask().getId().equals(progressDto.getTaskId())) {
            Task newTask = taskRepository.findById(progressDto.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + progressDto.getTaskId()));

            // Ensure new task belongs to user
            if (!newTask.getUser().getId().equals(currentUser.getId())) {
                throw new ResourceNotFoundException("Task not found with id: " + progressDto.getTaskId());
            }

            progress.setTask(newTask);
        }

        progress.setDate(progressDto.getDate());
        progress.setMinutesSpent(progressDto.getMinutesSpent());
        progress.setNotes(progressDto.getNotes());

        Progress updatedProgress = progressRepository.save(progress);

        return mapProgressToDto(updatedProgress);
    }

    /**
     * Delete a progress entry
     */
    @Transactional
    public void deleteProgress(Integer id) {
        User currentUser = authService.getCurrentUser();

        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress entry not found with id: " + id));

        // Security check: ensure progress belongs to current user
        if (!progress.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Progress entry not found with id: " + id);
        }

        progressRepository.delete(progress);
    }

    /**
     * Map Progress entities to ProgressDto
     */
    private List<ProgressDto> mapProgressToDto(List<Progress> progressEntries) {
        return progressEntries.stream()
                .map(this::mapProgressToDto)
                .collect(Collectors.toList());
    }

    /**
     * Log progress for a task
     */
    @Transactional
    public ProgressDto logProgress(ProgressDto progressDto) {
        User currentUser = authService.getCurrentUser();

        // Verify task exists and belongs to user
        Task task = taskRepository.findById(progressDto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Ensure user can only log progress for their own tasks
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new StudyPlannerException("You can only log progress for your own tasks");
        }

        // Validate progress details
        validateProgressDetails(progressDto);

        Progress progress = Progress.builder()
                .task(task)
                .user(currentUser)
                .date(progressDto.getDate() != null ? progressDto.getDate() : LocalDate.now())
                .minutesSpent(progressDto.getMinutesSpent())
                .notes(progressDto.getNotes())
                .build();

        Progress savedProgress = progressRepository.save(progress);

        // Optional: Update task completion status based on progress
        updateTaskCompletionStatus(task);

        return mapProgressToDto(savedProgress);
    }

    /**
     * Validate progress logging details
     */
    private void validateProgressDetails(ProgressDto progressDto) {
        if (progressDto.getMinutesSpent() <= 0) {
            throw new StudyPlannerException("Minutes spent must be positive");
        }

        if (progressDto.getDate() != null && progressDto.getDate().isAfter(LocalDate.now())) {
            throw new StudyPlannerException("Progress date cannot be in the future");
        }
    }

    /**
     * Update task completion status based on overall progress
     */
    private void updateTaskCompletionStatus(Task task) {
        // Calculate total progress time
        int totalProgressTime = task.getProgressEntries().stream()
                .mapToInt(Progress::getMinutesSpent)
                .sum();

        // Simple completion logic - adjust as needed
        if (totalProgressTime >= estimateRequiredTime(task)) {
            task.setCompleted(true);
            taskRepository.save(task);
        }
    }

    /**
     * Estimate required time for task completion
     */
    private int estimateRequiredTime(Task task) {
        switch (task.getPriority()) {
            case HIGH: return 180; // 3 hours
            case MEDIUM: return 120; // 2 hours
            case LOW: return 60; // 1 hour
            default: return 90;
        }
    }

    /**
     * Map Progress entity to ProgressDto
     */
    private ProgressDto mapProgressToDto(Progress progress) {
        Task task = progress.getTask();

        return ProgressDto.builder()
                .id(progress.getId())
                .date(progress.getDate())
                .minutesSpent(progress.getMinutesSpent())
                .notes(progress.getNotes())
                .taskId(task.getId())
                .taskTitle(task.getTitle())
                .subjectId(task.getSubject().getId())
                .subjectName(task.getSubject().getName())
                .build();
    }
}

