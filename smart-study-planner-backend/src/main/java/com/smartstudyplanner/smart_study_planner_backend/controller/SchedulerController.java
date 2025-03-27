package com.smartstudyplanner.smart_study_planner_backend.controller;


import com.smartstudyplanner.smart_study_planner_backend.dto.TaskDto;
import com.smartstudyplanner.smart_study_planner_backend.service.StudySchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for study scheduling operations
 */
@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final StudySchedulerService schedulerService;

    /**
     * Generate daily study schedule
     */
    @GetMapping("/daily")
    public ResponseEntity<List<TaskDto>> generateDailySchedule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "balanced") String strategy,
            @RequestParam(required = false, defaultValue = "8") Integer maxHours) {

        return ResponseEntity.ok(schedulerService.generateDailySchedule(date, strategy, maxHours));
    }

    /**
     * Generate weekly study schedule
     */
    @GetMapping("/weekly")
    public ResponseEntity<List<List<TaskDto>>> generateWeeklySchedule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false, defaultValue = "balanced") String strategy,
            @RequestParam(required = false, defaultValue = "8") Integer maxHoursPerDay) {

        return ResponseEntity.ok(schedulerService.generateWeeklySchedule(startDate, strategy, maxHoursPerDay));
    }

    /**
     * Create recurring task instances
     */
    @PostMapping("/recurring-tasks/{taskId}")
    public ResponseEntity<List<TaskDto>> createRecurringTaskInstances(@PathVariable Integer taskId) {
        return ResponseEntity.ok(schedulerService.createRecurringTaskInstances(taskId));
    }

    /**
     * Suggest priority tasks
     */
    @GetMapping("/suggest-priority")
    public ResponseEntity<List<TaskDto>> suggestPriorityTasks(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(schedulerService.suggestPriorityTasks(limit));
    }

    /**
     * Balance study load across subjects
     */
    @GetMapping("/balance-load")
    public ResponseEntity<List<TaskDto>> balanceStudyLoad(
            @RequestParam(required = false, defaultValue = "2") Integer tasksPerSubject) {
        return ResponseEntity.ok(schedulerService.balanceStudyLoad(tasksPerSubject));
    }
}
