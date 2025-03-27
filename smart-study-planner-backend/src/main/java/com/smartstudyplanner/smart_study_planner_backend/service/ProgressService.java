package com.smartstudyplanner.smart_study_planner_backend.service;


import com.smartstudyplanner.smart_study_planner_backend.model.Progress;
import com.smartstudyplanner.smart_study_planner_backend.repository.ProgressRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressService {
    private final ProgressRepository progressRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Progress recordProgress(Progress progress) {
        if (!taskRepository.existsById(progress.getTask().getId())) {
            throw new ResourceNotFoundException("Task not found");
        }
        return progressRepository.save(progress);
    }

    public List<Progress> getUserProgress(Integer userId, LocalDate startDate, LocalDate endDate) {
        return progressRepository.findProgressInDateRange(userId, startDate, endDate);
    }

    public Integer getDailyStudyMinutes(Integer userId, LocalDate date) {
        return progressRepository.getTotalMinutesForDay(userId, date);
    }

    public List<Progress> getTaskProgress(Integer taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }
        return progressRepository.findByTaskId(taskId);
    }
}

