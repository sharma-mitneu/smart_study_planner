package com.smartstudyplanner.smart_study_planner_backend.controller;


import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<Task>> getUserPendingTasks(@PathVariable Integer userId) {
        return ResponseEntity.ok(taskService.getUserPendingTasks(userId));
    }

    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks(@PathVariable Integer userId) {
        return ResponseEntity.ok(taskService.getOverdueTasks(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Integer id,
            @RequestBody @Valid Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskCompleted(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.markTaskCompleted(id));
    }
}