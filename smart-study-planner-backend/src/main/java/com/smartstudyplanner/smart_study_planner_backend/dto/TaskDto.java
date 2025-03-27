package com.smartstudyplanner.smart_study_planner_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.RecurrenceFrequency;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Integer id;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;

    private boolean completed;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Subject ID is required")
    private Integer subjectId;

    private String subjectName; // For display purposes

    private boolean recurring;

    private RecurrenceFrequency recurrenceFrequency;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recurrenceEndDate;

    // Calculated fields
    private boolean overdue;
    private Integer totalMinutesSpent;
}
