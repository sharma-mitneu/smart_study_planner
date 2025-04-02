package com.smartstudyplanner.smart_study_planner_backend.dto;

import jakarta.validation.constraints.NotBlank;
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

    @Builder.Default
    private BigDecimal completionPercentage = BigDecimal.ZERO;

    @Builder.Default
    private Integer taskCount = 0;
}