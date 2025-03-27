package com.smartstudyplanner.smart_study_planner_backend.repository;

import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    /**
     * Find all tasks for a user
     */
    List<Task> findByUser(User user);

    /**
     * Find all tasks for a user ID
     */
    List<Task> findByUserId(Integer userId);

    /**
     * Find all tasks for a subject
     */
    List<Task> findBySubject(Subject subject);

    /**
     * Find tasks by completion status for a user
     */
    List<Task> findByUserAndCompleted(User user, boolean completed);

    /**
     * Find tasks due soon for a user
     */
    List<Task> findByUserAndCompletedAndDueDateBetween(
            User user, boolean completed, LocalDateTime start, LocalDateTime end);

    /**
     * Find tasks by priority for a user
     */
    List<Task> findByUserAndPriorityOrderByDueDateAsc(User user, Priority priority);

    /**
     * Find overdue tasks for a user
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
            "AND t.completed = false AND t.dueDate < CURRENT_TIMESTAMP")
    List<Task> findOverdueTasks(Integer userId);

    /**
     * Find recurring tasks for a user
     */
    List<Task> findByUserAndRecurringTrue(User user);

    /**
     * Find tasks by subject ID
     */
    List<Task> findBySubjectId(Integer subjectId);
}
