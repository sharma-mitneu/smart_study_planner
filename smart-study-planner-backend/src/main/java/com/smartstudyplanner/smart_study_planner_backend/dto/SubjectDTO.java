package com.smartstudyplanner.smart_study_planner_backend.dto;

import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

    private Integer id;

    @NotBlank(message = "Subject name is required")
    private String name;

    @Builder.Default
    private String description = "";

    @NotNull(message = "Priority is required")
    private Priority priority;

    // Ensuring positive or zero value
    @PositiveOrZero(message = "Completion percentage must be zero or positive")
    @Builder.Default
    private BigDecimal completionPercentage = BigDecimal.ZERO;

    @PositiveOrZero(message = "Task count must be zero or positive")
    @Builder.Default
    private Integer taskCount = 0;
}
