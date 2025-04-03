package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.dto.StudyTimeDto;
import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectDTO;
import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectEnrollmentDTO;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import com.smartstudyplanner.smart_study_planner_backend.exception.StudyPlannerException;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.SubjectEnrollment;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.UserRole;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectEnrollmentRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);
    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final SubjectEnrollmentRepository subjectEnrollmentRepository;
    private final AuthService authService;

    /**
     * Get all subjects for current user
     */
    public List<SubjectDTO> getAllSubjects() {
        User currentUser = authService.getCurrentUser();

        // If user is a student, return enrolled subjects
        if (currentUser.getRole() == UserRole.STUDENT) {
            return getEnrolledSubjects();
        }

        // For admin, return all subjects they created
        List<Subject> subjects = subjectRepository.findByUser(currentUser);

        // Get tasks for the current user
        List<Task> userTasks = taskRepository.findByUserId(currentUser.getId());

        // Group tasks by subject
        Map<Integer, List<Task>> tasksBySubject = userTasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getSubject().getId(),
                        Collectors.toList()
                ));

        // Get task counts for each subject
        Map<Integer, Long> taskCountMap = userTasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getSubject().getId(),
                        Collectors.counting()
                ));

        // Get completion percentages
        Map<Integer, BigDecimal> completionRateMap = tasksBySubject.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> calculateCompletionPercentage(entry.getValue())
                ));

        return subjects.stream()
                .map(subject -> mapToDTO(subject, taskCountMap, completionRateMap))
                .collect(Collectors.toList());
    }


    /**
     * Get subject by ID
     */
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Integer id) {
        User currentUser = authService.getCurrentUser();
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // If user is student, check enrollment
        if (currentUser.getRole() == UserRole.STUDENT) {
            if (!isStudentEnrolled(currentUser.getId(), id)) {
                throw new StudyPlannerException("Student is not enrolled in this subject");
            }
        } else {
            // For admin, check ownership
            if (!subject.getUser().getId().equals(currentUser.getId())) {
                throw new ResourceNotFoundException("Subject not found with id: " + id);
            }
        }

        return mapToDTO(subject);
    }

    /**
     * Create a new subject (Admin only)
     */
    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDto) {
        User currentUser = authService.getCurrentUser();

        // Validate admin access
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new StudyPlannerException("Only administrators can create subjects");
        }

        // Check if a subject with the same course ID already exists
        Optional<Subject> existingSubject = subjectRepository.findByCourseId(subjectDto.getCourseId());
        if (existingSubject.isPresent()) {
            throw new StudyPlannerException("A subject with this course ID already exists");
        }

        Subject subject = Subject.builder()
                .name(subjectDto.getName())
                .description(subjectDto.getDescription())
                .courseId(subjectDto.getCourseId())
                .professor(subjectDto.getProfessor())
                .user(currentUser)
                .build();

        Subject savedSubject = subjectRepository.save(subject);
        return mapToDTO(savedSubject);
    }

    /**
     * Update an existing subject (Admin only)
     */
    @Transactional
    public SubjectDTO updateSubject(Integer id, SubjectDTO subjectDto) {
        validateAdminAccess();

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // Security check: ensure subject belongs to admin
        if (!subject.getUser().getId().equals(authService.getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }

        subject.setName(subjectDto.getName());
        subject.setDescription(subjectDto.getDescription());
        //subject.setPriority(subjectDto.getPriority());

        Subject updatedSubject = subjectRepository.save(subject);
        return mapToDTO(updatedSubject);
    }

    /**
     * Delete a subject (Admin only)
     */
    @Transactional
    public void deleteSubject(Integer id) {
        validateAdminAccess();

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // Security check: ensure subject belongs to admin
        if (!subject.getUser().getId().equals(authService.getCurrentUser().getId())) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }

        // Delete all enrollments first
        subjectEnrollmentRepository.deleteBySubjectId(id);

        subjectRepository.delete(subject);
    }

    /**
     * Enroll student in a subject (Student only)
     */
    @Transactional
    public SubjectEnrollmentDTO enrollStudent(Integer subjectId) {
        User currentUser = authService.getCurrentUser();

        // Ensure only students can enroll
        if (currentUser.getRole() != UserRole.STUDENT) {
            throw new StudyPlannerException("Only students can enroll in subjects");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        // Check if already enrolled
        if (isStudentEnrolled(currentUser.getId(), subjectId)) {
            throw new StudyPlannerException("Already enrolled in this subject");
        }

        SubjectEnrollment enrollment = SubjectEnrollment.builder()
                .subject(subject)
                .student(currentUser)
                .enrollmentDate(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        subjectEnrollmentRepository.save(enrollment);

        return mapToEnrollmentDTO(enrollment);
    }

    /**
     * Unenroll student from a subject (Student only)
     */
    @Transactional
    public void unenrollStudent(Integer subjectId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != UserRole.STUDENT) {
            throw new StudyPlannerException("Only students can unenroll from subjects");
        }

        SubjectEnrollment enrollment = subjectEnrollmentRepository
                .findByStudentIdAndSubjectId(currentUser.getId(), subjectId)
                .orElseThrow(() -> new StudyPlannerException("Student is not enrolled in this subject"));

        // Check for incomplete tasks in the subject
        List<Task> incompleteTasks = taskRepository.findBySubjectIdAndUserIdAndCompleted(
                subjectId,
                currentUser.getId(),
                false
        );

        // If there are incomplete tasks, prevent un-enrollment
        if (!incompleteTasks.isEmpty()) {
            throw new StudyPlannerException(
                    "Cannot un-enroll. You have " + incompleteTasks.size() +
                            " incomplete task(s) in this subject. Please complete them first."
            );
        }

        subjectEnrollmentRepository.delete(enrollment);
    }

    /**
     * Get enrolled subjects (Student only)
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getEnrolledSubjects() {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != UserRole.STUDENT) {
            throw new StudyPlannerException("Only students can view enrolled subjects");
        }

        return subjectEnrollmentRepository.findByStudentId(currentUser.getId()).stream()
                .map(enrollment -> mapToDTO(enrollment.getSubject()))
                .collect(Collectors.toList());
    }

    // Helper methods
    private void validateAdminAccess() {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new StudyPlannerException("Only administrators can perform this action");
        }
    }

    private boolean isStudentEnrolled(Integer studentId, Integer subjectId) {
        return subjectEnrollmentRepository.existsByStudentIdAndSubjectId(studentId, subjectId);
    }

    /**
     * Calculate completion percentage for tasks
     */
    private BigDecimal calculateCompletionPercentage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        long completedTasks = tasks.stream()
                .filter(Task::isCompleted)
                .count();
        return BigDecimal.valueOf((completedTasks * 100.0) / tasks.size())
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Map Subject to SubjectDTO with task count and completion percentage
     */
    private SubjectDTO mapToDTO(Subject subject) {
        // Get task count and calculate completion percentage
        List<com.smartstudyplanner.smart_study_planner_backend.model.Task> tasks =
                taskRepository.findBySubjectId(subject.getId());

        return SubjectDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .taskCount(tasks.size())
                .completionPercentage(calculateCompletionPercentage(tasks))
                .build();
    }

    /**
     * Updated mapToDTO method to accept BigDecimal completion rate
     */
    private SubjectDTO mapToDTO(Subject subject,
                                Map<Integer, Long> taskCountMap,
                                Map<Integer, BigDecimal> completionRateMap) {
        return SubjectDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .taskCount(taskCountMap.getOrDefault(subject.getId(), 0L).intValue())
                .completionPercentage(completionRateMap.getOrDefault(
                        subject.getId(),
                        BigDecimal.ZERO
                ))
                .build();
    }

    private SubjectEnrollmentDTO mapToEnrollmentDTO(SubjectEnrollment enrollment) {
        return SubjectEnrollmentDTO.builder()
                .subjectId(enrollment.getSubject().getId())
                .subjectName(enrollment.getSubject().getName())
                .studentUsername(enrollment.getStudent().getUsername())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .build();
    }
}