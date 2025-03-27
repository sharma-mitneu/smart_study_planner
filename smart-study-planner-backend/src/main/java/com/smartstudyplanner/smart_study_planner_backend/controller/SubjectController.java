package com.smartstudyplanner.smart_study_planner_backend.controller;

import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Subject> createSubject(
            @RequestBody @Valid Subject subject,
            @RequestParam Integer userId) {
        return ResponseEntity.ok(subjectService.createSubject(subject, userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subject>> getUserSubjects(@PathVariable Integer userId) {
        return ResponseEntity.ok(subjectService.getUserSubjects(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(
            @PathVariable Integer id,
            @RequestBody @Valid Subject subject) {
        return ResponseEntity.ok(subjectService.updateSubject(id, subject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}