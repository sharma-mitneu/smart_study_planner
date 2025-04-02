package com.smartstudyplanner.smart_study_planner_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subjects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Add a task to this subject
     * @param task The task to add
     * @return the subject for method chaining
     */
    public Subject addTask(Task task) {
        tasks.add(task);
        task.setSubject(this);
        return this;
    }

    /**
     * Remove a task from this subject
     * @param task The task to remove
     * @return the subject for method chaining
     */
    public Subject removeTask(Task task) {
        tasks.remove(task);
        task.setSubject(null);
        return this;
    }

    /**
     * Calculate completion percentage for this subject
     * @return percentage of completed tasks
     */
    public double getCompletionPercentage() {
        if (tasks.isEmpty()) {
            return 0.0;
        }

        long completedTasks = tasks.stream()
                .filter(Task::isCompleted)
                .count();

        return (double) completedTasks / tasks.size() * 100.0;
    }
}