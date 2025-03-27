package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.model.Progress;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO interface for Progress entity
 */
public interface ProgressDAO extends DAO<Progress, Long> {

    /**
     * Find progress records by user ID
     *
     * @param userId User ID
     * @return List of progress records
     */
    List<Progress> findByUserId(Long userId);

    /**
     * Find progress records by task ID
     *
     * @param taskId Task ID
     * @return List of progress records
     */
    List<Progress> findByTaskId(Long taskId);

    /**
     * Find progress records by date range
     *
     * @param userId User ID
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of progress records
     */
    List<Progress> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Get total minutes spent by user
     *
     * @param userId User ID
     * @return Total minutes spent
     */
    int getTotalMinutesByUserId(Long userId);
}

