package com.smartstudyplanner.smart_study_planner_backend.util;

import com.smartstudyplanner.smart_study_planner_backend.dao.SubjectDAO;
import com.smartstudyplanner.smart_study_planner_backend.dao.SubjectDAOImpl;
import com.smartstudyplanner.smart_study_planner_backend.exception.ValidationException;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for Subject-related operations
 */
public class SubjectService {

    private final SubjectDAO subjectDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAOImpl();
    }

    /**
     * Get all subjects for a user
     *
     * @param userId User ID
     * @return List of subjects
     */
    public List<Subject> getSubjectsByUserId(Long userId) {
        return subjectDAO.findByUserId(userId);
    }

    /**
     * Get subjects for a user filtered by priority
     *
     * @param userId User ID
     * @param priority Priority to filter by
     * @return List of filtered subjects
     */
    public List<Subject> getSubjectsByUserIdAndPriority(Long userId, Priority priority) {
        return subjectDAO.findByUserIdAndPriority(userId, priority);
    }

    /**
     * Get a subject by ID, ensuring it belongs to the user
     *
     * @param id Subject ID
     * @param userId User ID
     * @return Subject if found
     * @throws ValidationException If subject not found or doesn't belong to user
     */
    public Subject getSubjectById(Long id, Long userId) throws ValidationException {
        Optional<Subject> subjectOpt = subjectDAO.findById(id);

        if (subjectOpt.isEmpty() || !subjectOpt.get().getUserId().equals(userId)) {
            throw new ValidationException("Subject not found");
        }

        return subjectOpt.get();
    }

    /**
     * Create a new subject
     *
     * @param subject Subject to create
     * @return Created subject
     * @throws ValidationException If validation fails
     */
    public Subject createSubject(Subject subject) throws ValidationException {
        validateSubject(subject);
        return subjectDAO.save(subject);
    }

    /**
     * Update an existing subject
     *
     * @param subject Subject to update
     * @return Updated subject
     * @throws ValidationException If validation fails or subject not found
     */
    public Subject updateSubject(Subject subject) throws ValidationException {
        // Check if subject exists and belongs to user
        getSubjectById(subject.getId(), subject.getUserId());

        validateSubject(subject);
        return subjectDAO.update(subject);
    }

    /**
     * Delete a subject by ID
     *
     * @param id Subject ID
     * @param userId User ID
     * @return true if deleted, false if not found
     * @throws ValidationException If subject not found or doesn't belong to user
     */
    public boolean deleteSubject(Long id, Long userId) throws ValidationException {
        // Check if subject exists and belongs to user
        getSubjectById(id, userId);

        return subjectDAO.delete(id);
    }

    /**
     * Search subjects by name (case-insensitive)
     *
     * @param userId User ID
     * @param searchTerm Search term
     * @return List of matching subjects
     */
    public List<Subject> searchSubjectsByName(Long userId, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return subjectDAO.findByUserIdAndNameContaining(userId, searchTerm);
    }

    /**
     * Import multiple subjects
     *
     * @param subjects List of subjects to import
     * @return List of saved subjects
     */
    public List<Subject> importSubjects(List<Subject> subjects) {
        List<Subject> savedSubjects = new ArrayList<>();

        for (Subject subject : subjects) {
            try {
                validateSubject(subject);
                savedSubjects.add(subjectDAO.save(subject));
            } catch (ValidationException e) {
                // Log error and continue with next subject
                System.err.println("Error importing subject: " + e.getMessage());
            }
        }

        return savedSubjects;
    }

    /**
     * Validate subject data
     *
     * @param subject Subject to validate
     * @throws ValidationException If validation fails
     */
    private void validateSubject(Subject subject) throws ValidationException {
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new ValidationException("Subject name is required");
        }

        if (subject.getUserId() == null) {
            throw new ValidationException("User ID is required");
        }

        // Ensure priority is set
        if (subject.getPriority() == null) {
            subject.setPriority(Priority.MEDIUM);
        }
    }
}

