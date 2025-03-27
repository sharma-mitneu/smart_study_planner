package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.dto.TaskDto;
import com.smartstudyplanner.smart_study_planner_backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for task operations
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Get all tasks for current user
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(taskService.getAllTasks(sortBy));
    }

    /**
     * Get tasks by subject
     */
    @GetMapping("/by-subject/{subjectId}")
    public ResponseEntity<List<TaskDto>> getTasksBySubject(
            @PathVariable Integer subjectId,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(taskService.getTasksBySubject(subjectId, sortBy));
    }

    /**
     * Get upcoming tasks
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskDto>> getUpcomingTasks() {
        return ResponseEntity.ok(taskService.getUpcomingTasks());
    }

    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    /**
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskDto));
    }

    /**
     * Update an existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Integer id,
            @Valid @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDto));
    }

    /**
     * Delete a task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mark a task as completed
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskDto> markTaskAsCompleted(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.markTaskAsCompleted(id));
    }
}