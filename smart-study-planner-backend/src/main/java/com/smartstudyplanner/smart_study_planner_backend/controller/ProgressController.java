package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.model.Progress;
import com.smartstudyplanner.smart_study_planner_backend.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;

    @PostMapping
    public ResponseEntity<Progress> recordProgress(@RequestBody @Valid Progress progress) {
        return ResponseEntity.ok(progressService.recordProgress(progress));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Progress>> getUserProgress(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(progressService.getUserProgress(userId, startDate, endDate));
    }

    @GetMapping("/user/{userId}/daily")
    public ResponseEntity<Integer> getDailyStudyMinutes(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(progressService.getDailyStudyMinutes(userId, date));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Progress>> getTaskProgress(@PathVariable Integer taskId) {
        return ResponseEntity.ok(progressService.getTaskProgress(taskId));
    }
}