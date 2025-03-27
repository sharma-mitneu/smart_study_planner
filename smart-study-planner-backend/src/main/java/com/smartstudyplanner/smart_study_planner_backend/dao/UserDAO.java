package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.model.User;

import java.util.Optional;

/**
 * DAO interface for User entity
 */
public interface UserDAO extends DAO<User, Long> {

    /**
     * Find user by username
     *
     * @param username Username to search for
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     *
     * @param email Email to search for
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username already exists
     *
     * @param username Username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email already exists
     *
     * @param email Email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);
}


