package com.smartstudyplanner.smart_study_planner_backend.repository;

import com.smartstudyplanner.smart_study_planner_backend.model.Progress;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    /**
     * Find all progress entries for a user
     */
    List<Progress> findByUser(User user);

    /**
     * Find all progress entries for a task
     */
    List<Progress> findByTask(Task task);

    /**
     * Find progress entries between dates for a user
     */
    List<Progress> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    /**
     * Find progress entries for a specific date and user
     */
    List<Progress> findByUserAndDate(User user, LocalDate date);

    /**
     * Get total study time by user for date range
     */
    @Query("SELECT SUM(p.minutesSpent) FROM Progress p " +
            "WHERE p.user.id = :userId AND p.date BETWEEN :startDate AND :endDate")
    Integer getTotalStudyTimeByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    /**
     * Get study time summary by subject for a user
     */
    @Query("SELECT s.name, SUM(p.minutesSpent) " +
            "FROM Progress p " +
            "JOIN p.task t " +
            "JOIN t.subject s " +
            "WHERE p.user.id = :userId " +
            "GROUP BY s.name")
    List<Object[]> getStudyTimeSummaryBySubject(Integer userId);

    /**
     * Get daily study time for the last n days
     */
    @Query("SELECT p.date, SUM(p.minutesSpent) " +
            "FROM Progress p " +
            "WHERE p.user.id = :userId AND p.date >= :startDate " +
            "GROUP BY p.date " +
            "ORDER BY p.date")
    List<Object[]> getDailyStudyTimeForLastDays(Integer userId, LocalDate startDate);
}
