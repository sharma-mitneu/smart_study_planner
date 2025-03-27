package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.dto.StudyTimeDto;
import com.smartstudyplanner.smart_study_planner_backend.dto.TaskCompletionDto;
import com.smartstudyplanner.smart_study_planner_backend.service.StudyAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller for analytics operations
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final StudyAnalyticsService analyticsService;

    /**
     * Get study time analytics
     */
    @GetMapping("/study-time")
    public ResponseEntity<StudyTimeDto> getStudyTimeAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 30 days if not specified
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return ResponseEntity.ok(analyticsService.getStudyTimeAnalytics(startDate, endDate));
    }

    /**
     * Get task completion analytics
     */
    @GetMapping("/task-completion")
    public ResponseEntity<TaskCompletionDto> getTaskCompletionAnalytics() {
        return ResponseEntity.ok(analyticsService.getTaskCompletionAnalytics());
    }

    /**
     * Get current study streak
     */
    @GetMapping("/study-streak")
    public ResponseEntity<Integer> getCurrentStudyStreak() {
        return ResponseEntity.ok(analyticsService.getCurrentStudyStreak());
    }

    /**
     * Get productivity score
     */
    @GetMapping("/productivity-score")
    public ResponseEntity<Double> getProductivityScore() {
        return ResponseEntity.ok(analyticsService.getProductivityScore());
    }
}
