package com.smartstudyplanner.smart_study_planner_backend.service;


import com.smartstudyplanner.smart_study_planner_backend.dto.StudyTimeDto;
import com.smartstudyplanner.smart_study_planner_backend.dto.SubjectDTO;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.User;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final AuthService authService;

    /**
     * Get all subjects for current user
     */
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        User currentUser = authService.getCurrentUser();
        List<Subject> subjects = subjectRepository.findByUser(currentUser);

        // Get task counts for each subject
        Map<Integer, Long> taskCountMap = taskRepository.findByUserId(currentUser.getId()).stream()
                .collect(Collectors.groupingBy(
                        task -> task.getSubject().getId(),
                        Collectors.counting()
                ));

        // Get completion percentages
        Map<Integer, Double> completionRateMap = taskRepository.findByUserId(currentUser.getId()).stream()
                .collect(Collectors.groupingBy(
                        task -> task.getSubject().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                tasks -> {
                                    if (tasks.isEmpty()) {
                                        return 0.0;
                                    }
                                    long completedCount = tasks.stream().filter(task -> task.isCompleted()).count();
                                    return (double) completedCount / tasks.size() * 100.0;
                                }
                        )
                ));

        return subjects.stream().map(subject -> {
            Integer subjectId = subject.getId();
            return SubjectDTO.builder()
                    .id(subjectId)
                    .name(subject.getName())
                    .description(subject.getDescription())
                    .priority(subject.getPriority())
                    .taskCount(taskCountMap.getOrDefault(subjectId, 0L).intValue())
                    .completionPercentage(BigDecimal.valueOf(completionRateMap.getOrDefault(subjectId, 0.0)))
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Get subject by ID
     */
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Integer id) {
        User currentUser = authService.getCurrentUser();
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // Security check: ensure subject belongs to current user
        if (!subject.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }

        // Calculate task count
        long taskCount = taskRepository.findBySubjectId(id).size();

        // Calculate completion percentage
        Double completionPercentage = taskRepository.findBySubjectId(id).stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        tasks -> {
                            if (tasks.isEmpty()) {
                                return 0.0;
                            }
                            long completedCount = tasks.stream().filter(task -> task.isCompleted()).count();
                            return (double) completedCount / tasks.size() * 100.0;
                        }
                ));

        return SubjectDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .priority(subject.getPriority())
                .taskCount((int) taskCount)
                .completionPercentage(BigDecimal.valueOf(completionPercentage))
                .build();
    }

    /**
     * Create a new subject
     */
    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDto) {
        User currentUser = authService.getCurrentUser();

        Subject subject = Subject.builder()
                .name(subjectDto.getName())
                .description(subjectDto.getDescription())
                .priority(subjectDto.getPriority() != null ? subjectDto.getPriority() : Priority.MEDIUM)
                .user(currentUser)
                .build();

        Subject savedSubject = subjectRepository.save(subject);

        return SubjectDTO.builder()
                .id(savedSubject.getId())
                .name(savedSubject.getName())
                .description(savedSubject.getDescription())
                .priority(savedSubject.getPriority())
                .taskCount(0)
                .completionPercentage(BigDecimal.valueOf(0.0))
                .build();
    }

    /**
     * Update an existing subject
     */
    @Transactional
    public SubjectDTO updateSubject(Integer id, SubjectDTO subjectDto) {
        User currentUser = authService.getCurrentUser();

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // Security check: ensure subject belongs to current user
        if (!subject.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }

        subject.setName(subjectDto.getName());
        subject.setDescription(subjectDto.getDescription());
        subject.setPriority(subjectDto.getPriority());

        Subject updatedSubject = subjectRepository.save(subject);

        // Calculate task count and completion percentage
        List<com.smartstudyplanner.smart_study_planner_backend.model.Task> tasks = taskRepository.findBySubjectId(id);
        int taskCount = tasks.size();

        double completionPercentage = 0.0;
        if (taskCount > 0) {
            long completedCount = tasks.stream().filter(task -> task.isCompleted()).count();
            completionPercentage = (double) completedCount / taskCount * 100.0;
        }

        return SubjectDTO.builder()
                .id(updatedSubject.getId())
                .name(updatedSubject.getName())
                .description(updatedSubject.getDescription())
                .priority(updatedSubject.getPriority())
                .taskCount(taskCount)
                .completionPercentage(BigDecimal.valueOf(completionPercentage))
                .build();
    }

    /**
     * Delete a subject
     */
    @Transactional
    public void deleteSubject(Integer id) {
        User currentUser = authService.getCurrentUser();

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));

        // Security check: ensure subject belongs to current user
        if (!subject.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Subject not found with id: " + id);
        }

        subjectRepository.delete(subject);
    }
}
