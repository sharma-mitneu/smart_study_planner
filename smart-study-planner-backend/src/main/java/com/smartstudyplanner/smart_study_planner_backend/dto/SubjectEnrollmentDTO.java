package com.smartstudyplanner.smart_study_planner_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectEnrollmentDTO {
    private Integer subjectId;
    private String subjectName;
    private String studentUsername;
    private LocalDateTime enrollmentDate;
    private String status;
}