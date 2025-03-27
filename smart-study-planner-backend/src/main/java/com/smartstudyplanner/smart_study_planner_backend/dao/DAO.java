package com.smartstudyplanner.smart_study_planner_backend.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object interface
 *
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface DAO<T, ID> {

    /**
     * Find entity by ID
     *
     * @param id Entity ID
     * @return Optional containing entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities
     *
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Save new entity
     *
     * @param entity Entity to save
     * @return Saved entity with ID
     */
    T save(T entity);

    /**
     * Update existing entity
     *
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);

    /**
     * Delete entity by ID
     *
     * @param id Entity ID to delete
     * @return true if deleted, false if not found
     */
    boolean delete(ID id);
}
