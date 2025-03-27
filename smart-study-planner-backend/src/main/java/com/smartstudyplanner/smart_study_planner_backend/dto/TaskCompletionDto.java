package com.smartstudyplanner.smart_study_planner_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionDto {

    private Integer totalTasks;
    private Integer completedTasks;
    private Integer pendingTasks;
    private Integer overdueTasks;
    private Double completionRate;
    private List<SubjectCompletion> subjectBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectCompletion {
        private String subjectName;
        private Integer totalTasks;
        private Integer completedTasks;
        private Double completionRate;
    }
}
