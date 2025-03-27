package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.dto.ProgressDto;
import com.smartstudyplanner.smart_study_planner_backend.service.ProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for progress tracking operations
 */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    /**
     * Get all progress entries for current user
     */
    @GetMapping
    public ResponseEntity<List<ProgressDto>> getAllProgressEntries() {
        return ResponseEntity.ok(progressService.getAllProgressEntries());
    }

    /**
     * Get progress entries by date range
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<ProgressDto>> getProgressByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(progressService.getProgressByDateRange(startDate, endDate));
    }

    /**
     * Get progress entries for a specific task
     */
    @GetMapping("/by-task/{taskId}")
    public ResponseEntity<List<ProgressDto>> getProgressByTask(@PathVariable Integer taskId) {
        return ResponseEntity.ok(progressService.getProgressByTask(taskId));
    }

    /**
     * Get a specific progress entry
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgressDto> getProgressById(@PathVariable Integer id) {
        return ResponseEntity.ok(progressService.getProgressById(id));
    }

    /**
     * Create a new progress entry
     */
    @PostMapping
    public ResponseEntity<ProgressDto> createProgress(@Valid @RequestBody ProgressDto progressDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(progressService.createProgress(progressDto));
    }

    /**
     * Update an existing progress entry
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProgressDto> updateProgress(
            @PathVariable Integer id,
            @Valid @RequestBody ProgressDto progressDto) {
        return ResponseEntity.ok(progressService.updateProgress(id, progressDto));
    }

    /**
     * Delete a progress entry
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable Integer id) {
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}