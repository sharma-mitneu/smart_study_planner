package com.smartstudyplanner.smart_study_planner_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDto {

    private Integer id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Minutes spent is required")
    @Min(value = 1, message = "Minutes spent must be at least 1")
    private Integer minutesSpent;

    private String notes;

    @NotNull(message = "Task ID is required")
    private Integer taskId;

    private String taskTitle; // For display purposes

    private Integer subjectId; // For display purposes

    private String subjectName; // For display purposes
}
