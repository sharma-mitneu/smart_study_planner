package com.smartstudyplanner.smart_study_planner_backend.repository;

import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    /**
     * Find all subjects for a user
     */
    List<Subject> findByUser(User user);

    /**
     * Find all subjects for a user ID
     */
    List<Subject> findByUserId(Integer userId);

    /**
     * Find subjects by user and priority
     */
    List<Subject> findByUserAndPriority(User user, Priority priority);

    /**
     * Find subjects with completion status
     */
    @Query("SELECT s, " +
            "(SELECT COUNT(t) FROM Task t WHERE t.subject = s AND t.completed = true) / " +
            "CASE WHEN COUNT(t) = 0 THEN 1.0 ELSE COUNT(t) END * 100 as completionPercentage " +
            "FROM Subject s LEFT JOIN s.tasks t " +
            "WHERE s.user.id = :userId " +
            "GROUP BY s")
    List<Object[]> findSubjectsWithCompletionStatusByUserId(Integer userId);
}
