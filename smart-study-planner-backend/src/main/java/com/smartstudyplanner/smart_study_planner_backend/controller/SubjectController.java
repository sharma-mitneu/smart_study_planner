package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectDTO;
import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectEnrollmentDTO;
import com.smartstudyplanner.smart_study_planner_backend.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @PostMapping("admin/create-subject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody SubjectDTO subjectDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(subjectDto));
    }

    @GetMapping("student/available")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubjectDTO>> getAvailableSubjects() {
        return ResponseEntity.ok(subjectService.getUnenrolledSubjects());
    }


    @PutMapping("admin/update-subject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable Integer id,
            @Valid @RequestBody SubjectDTO subjectDto) {
        return ResponseEntity.ok(subjectService.updateSubject(id, subjectDto));
    }

    @DeleteMapping("admin/delete-subject/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("student/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubjectEnrollmentDTO> enrollInSubject(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.enrollStudent(id));
    }

    @DeleteMapping("student/{id}/unenroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> unenrollFromSubject(@PathVariable Integer id) {
        subjectService.unenrollStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("student/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SubjectDTO>> getEnrolledSubjects() {
        return ResponseEntity.ok(subjectService.getEnrolledSubjects());
    }
}