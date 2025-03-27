package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.dto.ProgressDto;
import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectDTO;
import com.smartstudyplanner.smart_study_planner_backend.dto.TaskDto;
import com.smartstudyplanner.smart_study_planner_backend.service.CSVExportService;
import com.smartstudyplanner.smart_study_planner_backend.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller for CSV export and import operations
 */
@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CSVExportService csvExportService;
    private final CsvImportService csvImportService;

    /**
     * Export subjects to CSV
     */
    @GetMapping("/export/subjects")
    public ResponseEntity<ByteArrayResource> exportSubjects() throws IOException {
        String csv = csvExportService.exportSubjects();
        return createCsvResponse(csv, "subjects.csv");
    }

    /**
     * Export tasks to CSV
     */
    @GetMapping("/export/tasks")
    public ResponseEntity<ByteArrayResource> exportTasks() throws IOException {
        String csv = csvExportService.exportTasks();
        return createCsvResponse(csv, "tasks.csv");
    }

    /**
     * Export progress entries to CSV
     */
    @GetMapping("/export/progress")
    public ResponseEntity<ByteArrayResource> exportProgress() throws IOException {
        String csv = csvExportService.exportProgress();
        return createCsvResponse(csv, "progress.csv");
    }

    /**
     * Export study summary to CSV
     */
    @GetMapping("/export/study-summary")
    public ResponseEntity<ByteArrayResource> exportStudySummary() throws IOException {
        String csv = csvExportService.exportStudySummary();
        return createCsvResponse(csv, "study-summary.csv");
    }

    /**
     * Import subjects from CSV
     */
    @PostMapping("/import/subjects")
    public ResponseEntity<List<SubjectDTO>> importSubjects(@RequestParam("file") MultipartFile file) throws IOException {
        List<SubjectDTO> importedSubjects = csvImportService.importSubjects(file);
        return ResponseEntity.ok(importedSubjects);
    }

    /**
     * Import tasks from CSV
     */
    @PostMapping("/import/tasks")
    public ResponseEntity<List<TaskDto>> importTasks(@RequestParam("file") MultipartFile file) throws IOException {
        List<TaskDto> importedTasks = csvImportService.importTasks(file);
        return ResponseEntity.ok(importedTasks);
    }

    /**
     * Import progress entries from CSV
     */
    @PostMapping("/import/progress")
    public ResponseEntity<List<ProgressDto>> importProgress(@RequestParam("file") MultipartFile file) throws IOException {
        List<ProgressDto> importedProgress = csvImportService.importProgress(file);
        return ResponseEntity.ok(importedProgress);
    }

    /**
     * Helper method to create CSV download response
     */
    private ResponseEntity<ByteArrayResource> createCsvResponse(String csv, String filename) {
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(bytes.length)
                .body(resource);
    }
}
