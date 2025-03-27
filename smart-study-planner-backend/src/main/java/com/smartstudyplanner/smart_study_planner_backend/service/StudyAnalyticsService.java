package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.StudyTimeDto;
import com.smartstudyplanner.smart_study_planner_backend.dto.TaskCompletionDto;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.repository.ProgressRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating study analytics
 * Demonstrates use of Streams, Lambdas and Collections processing
 */
@Service
@RequiredArgsConstructor
public class StudyAnalyticsService {

    private final ProgressRepository progressRepository;
    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;
    private final AuthService authService;

    /**
     * Get study time analytics for the current user
     */
    @Transactional(readOnly = true)
    public StudyTimeDto getStudyTimeAnalytics(LocalDate startDate, LocalDate endDate) {
        User currentUser = authService.getCurrentUser();
        Integer userId = currentUser.getId();

        // Get total study time in minutes
        Integer totalMinutes = progressRepository.getTotalStudyTimeByUserAndDateRange(
                userId, startDate, endDate);

        if (totalMinutes == null) {
            totalMinutes = 0;
        }

        // Get daily study time breakdown
        List<Object[]> dailyData = progressRepository.getDailyStudyTimeForLastDays(userId, startDate);
        List<StudyTimeDto.DailyStudyTime> dailyBreakdown = dailyData.stream()
                .map(row -> new StudyTimeDto.DailyStudyTime(
                        (LocalDate) row[0],
                        ((Long) row[1]).intValue()
                ))
                .collect(Collectors.toList());

        // Get subject study time breakdown
        List<Object[]> subjectData = progressRepository.getStudyTimeSummaryBySubject(userId);
        List<StudyTimeDto.SubjectStudyTime> subjectBreakdown = new ArrayList<>();

        for (Object[] row : subjectData) {
            String subjectName = (String) row[0];
            Integer minutes = ((Long) row[1]).intValue();
            Double percentage = totalMinutes > 0 ? (minutes * 100.0 / totalMinutes) : 0.0;

            subjectBreakdown.add(new StudyTimeDto.SubjectStudyTime(
                    subjectName,
                    minutes,
                    percentage
            ));
        }

        return StudyTimeDto.builder()
                .totalMinutes(totalMinutes)
                .dailyBreakdown(dailyBreakdown)
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    /**
     * Get task completion analytics for the current user
     */
    @Transactional(readOnly = true)
    public TaskCompletionDto getTaskCompletionAnalytics() {
        User currentUser = authService.getCurrentUser();

        // Get all tasks for current user
        var tasks = taskRepository.findByUser(currentUser);

        // Count total, completed, and overdue tasks
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(task -> task.isCompleted()).count();
        int pendingTasks = totalTasks - completedTasks;
        int overdueTasks = (int) tasks.stream()
                .filter(task -> !task.isCompleted() && task.isOverdue())
                .count();

        // Calculate completion rate
        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        // Get all subjects for current user
        List<Subject> subjects = subjectRepository.findByUser(currentUser);

        // Group tasks by subject
        Map<Integer, List<com.smartstudyplanner.smart_study_planner_backend.model.Task>> tasksBySubject = tasks.stream()
                .collect(Collectors.groupingBy(task -> task.getSubject().getId()));

        // Create subject breakdown
        List<TaskCompletionDto.SubjectCompletion> subjectBreakdown = new ArrayList<>();

        for (Subject subject : subjects) {
            List<com.smartstudyplanner.smart_study_planner_backend.model.Task> subjectTasks = tasksBySubject.getOrDefault(subject.getId(), List.of());
            int subjectTotalTasks = subjectTasks.size();

            if (subjectTotalTasks > 0) {
                int subjectCompletedTasks = (int) subjectTasks.stream()
                        .filter(task -> task.isCompleted())
                        .count();
                double subjectCompletionRate = (subjectCompletedTasks * 100.0 / subjectTotalTasks);

                subjectBreakdown.add(new TaskCompletionDto.SubjectCompletion(
                        subject.getName(),
                        subjectTotalTasks,
                        subjectCompletedTasks,
                        subjectCompletionRate
                ));
            }
        }

        // Sort by completion rate (ascending)
        subjectBreakdown.sort((a, b) -> Double.compare(a.getCompletionRate(), b.getCompletionRate()));

        return TaskCompletionDto.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .overdueTasks(overdueTasks)
                .completionRate(completionRate)
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    /**
     * Get study streak data (consecutive days with study sessions)
     */
    @Transactional(readOnly = true)
    public int getCurrentStudyStreak() {
        User currentUser = authService.getCurrentUser();
        LocalDate today = LocalDate.now();

        // Get all progress entries sorted by date (descending)
        List<LocalDate> studyDates = progressRepository.findByUser(currentUser).stream()
                .map(progress -> progress.getDate())
                .distinct()
                .sorted((a, b) -> b.compareTo(a)) // Sort descending
                .collect(Collectors.toList());

        if (studyDates.isEmpty()) {
            return 0;
        }

        // Check if studied today
        boolean studiedToday = studyDates.contains(today);

        // Initialize streak counter
        int streak = studiedToday ? 1 : 0;

        // Start from yesterday
        LocalDate currentDate = today.minusDays(1);

        // Count consecutive days
        for (int i = (studiedToday ? 1 : 0); i < studyDates.size(); i++) {
            if (studyDates.contains(currentDate)) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * Get productivity score based on task completion and study consistency
     */
    @Transactional(readOnly = true)
    public double getProductivityScore() {
        TaskCompletionDto taskCompletion = getTaskCompletionAnalytics();
        int streak = getCurrentStudyStreak();

        // Calculate productivity score (50% completion rate, 30% streak, 20% overdue penalty)
        double completionComponent = taskCompletion.getCompletionRate() * 0.5;
        double streakComponent = Math.min(streak * 5, 30); // Max 30 points for 6+ day streak

        // Overdue penalty calculation
        double overduePenalty = 0;
        if (taskCompletion.getTotalTasks() > 0) {
            double overduePercentage = (double) taskCompletion.getOverdueTasks() / taskCompletion.getTotalTasks();
            overduePenalty = overduePercentage * 20; // Max 20 points penalty
        }

        // Calculate final score
        double score = completionComponent + streakComponent - overduePenalty;

        // Ensure score is between 0 and 100
        return Math.max(0, Math.min(100, score));
    }
}