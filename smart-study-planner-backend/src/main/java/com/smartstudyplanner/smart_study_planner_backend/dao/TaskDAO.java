package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO interface for Task entity
 */
public interface TaskDAO extends DAO<Task, Long> {

    /**
     * Find tasks by user ID
     *
     * @param userId User ID
     * @return List of tasks
     */
    List<Task> findByUserId(Long userId);

    /**
     * Find tasks by subject ID
     *
     * @param subjectId Subject ID
     * @return List of tasks
     */
    List<Task> findBySubjectId(Long subjectId);

    /**
     * Find tasks by subject ID and user ID
     *
     * @param subjectId Subject ID
     * @param userId User ID
     * @return List of tasks
     */
    List<Task> findBySubjectIdAndUserId(Long subjectId, Long userId);

    /**
     * Find tasks by user ID and priority
     *
     * @param userId User ID
     * @param priority Priority to filter by
     * @return List of filtered tasks
     */
    List<Task> findByUserIdAndPriority(Long userId, Priority priority);

    /**
     * Find tasks by user ID and completion status
     *
     * @param userId User ID
     * @param completed Completion status
     * @return List of tasks
     */
    List<Task> findByUserIdAndCompleted(Long userId, boolean completed);

    /**
     * Find tasks by due date range
     *
     * @param userId User ID
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of tasks
     */
    List<Task> findByUserIdAndDueDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find tasks that are overdue (due date in past and not completed)
     *
     * @param userId User ID
     * @param currentDate Reference date
     * @return List of overdue tasks
     */
    List<Task> findOverdueTasks(Long userId, LocalDateTime currentDate);
}

