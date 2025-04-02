package com.smartstudyplanner.smart_study_planner_backend.repository;

import com.smartstudyplanner.smart_study_planner_backend.model.SubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectEnrollmentRepository extends JpaRepository<SubjectEnrollment, Integer> {
    List<SubjectEnrollment> findByStudentId(Integer studentId);
    Optional<SubjectEnrollment> findByStudentIdAndSubjectId(Integer studentId, Integer subjectId);
    boolean existsByStudentIdAndSubjectId(Integer studentId, Integer subjectId);
    void deleteBySubjectId(Integer subjectId);
}