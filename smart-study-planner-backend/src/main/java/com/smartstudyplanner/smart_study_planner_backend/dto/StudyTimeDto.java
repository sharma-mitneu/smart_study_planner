package com.smartstudyplanner.smart_study_planner_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTimeDto {

    private Integer totalMinutes;
    private List<DailyStudyTime> dailyBreakdown;
    private List<SubjectStudyTime> subjectBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStudyTime {
        private LocalDate date;
        private Integer minutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectStudyTime {
        private String subjectName;
        private Integer minutes;
        private Double percentage;
    }
}
