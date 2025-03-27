package com.smartstudyplanner.smart_study_planner_backend.controller;


import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectDTO;
import com.smartstudyplanner.smart_study_planner_backend.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for subject operations
 */
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Get all subjects for current user
     */
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    /**
     * Get subject by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    /**
     * Create a new subject
     */
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectDTO subjectDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(subjectDto));
    }

    /**
     * Update an existing subject
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable Integer id,
            @Valid @RequestBody SubjectDTO subjectDto) {
        return ResponseEntity.ok(subjectService.updateSubject(id, subjectDto));
    }

    /**
     * Delete a subject
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}