package com.smartstudyplanner.smart_study_planner_backend.service;

import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.repository.TaskRepository;
import com.smartstudyplanner.smart_study_planner_backend.repository.SubjectRepository;
import com.smartstudyplanner.smart_study_planner_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public Task createTask(Task task) {
        if (!subjectRepository.existsById(task.getSubject().getId())) {
            throw new ResourceNotFoundException("Subject not found");
        }
        return taskRepository.save(task);
    }

    public List<Task> getUserPendingTasks(Integer userId) {
        return taskRepository.findByUserIdAndCompletedFalseOrderByDueDateAsc(userId);
    }

    public List<Task> getOverdueTasks(Integer userId) {
        return taskRepository.findOverdueTasks(userId, LocalDateTime.now());
    }

    @Transactional
    public Task updateTask(Integer id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDue_date(taskDetails.getDue_date());
        task.setPriority(taskDetails.getPriority());
        task.setCompleted(taskDetails.getCompleted());

        return taskRepository.save(task);
    }

    @Transactional
    public Task markTaskCompleted(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setCompleted(true);
        return taskRepository.save(task);
    }
}

