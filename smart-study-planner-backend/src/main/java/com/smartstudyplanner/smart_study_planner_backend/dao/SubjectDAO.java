package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;

import java.util.List;

/**
 * DAO interface for Subject entity
 */
public interface SubjectDAO extends DAO<Subject, Long> {

    /**
     * Find subjects by user ID
     *
     * @param userId User ID
     * @return List of subjects belonging to user
     */
    List<Subject> findByUserId(Long userId);

    /**
     * Find subjects by user ID and priority
     *
     * @param userId User ID
     * @param priority Priority to filter by
     * @return List of filtered subjects
     */
    List<Subject> findByUserIdAndPriority(Long userId, Priority priority);

    /**
     * Find subjects by user ID containing name (case-insensitive)
     *
     * @param userId User ID
     * @param name Name to search for
     * @return List of matching subjects
     */
    List<Subject> findByUserIdAndNameContaining(Long userId, String name);
}

