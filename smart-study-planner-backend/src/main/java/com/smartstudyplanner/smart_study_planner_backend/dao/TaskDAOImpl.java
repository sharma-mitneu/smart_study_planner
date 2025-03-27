package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.config.DatabaseConfig;
import com.smartstudyplanner.smart_study_planner_backend.model.RecurringTask;
import com.smartstudyplanner.smart_study_planner_backend.model.Task;
import com.smartstudyplanner.smart_study_planner_backend.model.Frequency;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TaskDAO
 */
public class TaskDAOImpl implements TaskDAO {

    @Override
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding task by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all tasks: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public Task save(Task task) {
        String sql;
        boolean isRecurring = task instanceof RecurringTask;

        if (isRecurring) {
            sql = "INSERT INTO tasks (title, description, due_date, completed, priority, " +
                    "subject_id, user_id, recurring, recurrence_frequency, recurrence_end_date, " +
                    "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        } else {
            sql = "INSERT INTO tasks (title, description, due_date, completed, priority, " +
                    "subject_id, user_id, recurring, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, false, ?, ?) RETURNING id";
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getDueDate()));
            stmt.setBoolean(4, task.isCompleted());
            stmt.setString(5, task.getPriority().name());
            stmt.setLong(6, task.getSubjectId());
            stmt.setLong(7, task.getUserId());

            if (isRecurring) {
                RecurringTask recurringTask = (RecurringTask) task;
                stmt.setBoolean(8, true);
                stmt.setString(9, recurringTask.getFrequency().name());

                if (recurringTask.getEndDate() != null) {
                    stmt.setTimestamp(10, Timestamp.valueOf(recurringTask.getEndDate()));
                } else {
                    stmt.setNull(10, Types.TIMESTAMP);
                }

                stmt.setTimestamp(11, Timestamp.valueOf(task.getCreatedAt()));
                stmt.setTimestamp(12, Timestamp.valueOf(task.getUpdatedAt()));
            } else {
                stmt.setTimestamp(8, Timestamp.valueOf(task.getCreatedAt()));
                stmt.setTimestamp(9, Timestamp.valueOf(task.getUpdatedAt()));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    task.setId(rs.getLong("id"));
                    return task;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving task: " + e.getMessage());
            e.printStackTrace();
        }

        return task;
    }

    @Override
    public Task update(Task task) {
        String sql;
        boolean isRecurring = task instanceof RecurringTask;

        if (isRecurring) {
            sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, completed = ?, " +
                    "priority = ?, subject_id = ?, recurring = true, recurrence_frequency = ?, " +
                    "recurrence_end_date = ?, updated_at = ? WHERE id = ? AND user_id = ?";
        } else {
            sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, completed = ?, " +
                    "priority = ?, subject_id = ?, recurring = false, updated_at = ? " +
                    "WHERE id = ? AND user_id = ?";
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            task.updateTimestamp();

            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(task.getDueDate()));
            stmt.setBoolean(4, task.isCompleted());
            stmt.setString(5, task.getPriority().name());
            stmt.setLong(6, task.getSubjectId());

            if (isRecurring) {
                RecurringTask recurringTask = (RecurringTask) task;
                stmt.setString(7, recurringTask.getFrequency().name());

                if (recurringTask.getEndDate() != null) {
                    stmt.setTimestamp(8, Timestamp.valueOf(recurringTask.getEndDate()));
                } else {
                    stmt.setNull(8, Types.TIMESTAMP);
                }

                stmt.setTimestamp(9, Timestamp.valueOf(task.getUpdatedAt()));
                stmt.setLong(10, task.getId());
                stmt.setLong(11, task.getUserId());
            } else {
                stmt.setTimestamp(7, Timestamp.valueOf(task.getUpdatedAt()));
                stmt.setLong(8, task.getId());
                stmt.setLong(9, task.getUserId());
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return task;
            }

        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
            e.printStackTrace();
        }

        return task;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding tasks by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public List<Task> findBySubjectId(Long subjectId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE subject_id = ? ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding tasks by subject ID: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public List<Task> findBySubjectIdAndUserId(Long subjectId, Long userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE subject_id = ? AND user_id = ? ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, subjectId);
            stmt.setLong(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding tasks by subject ID and user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public List<Task> findByUserIdAndPriority(Long userId, Priority priority) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND priority = ? ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, priority.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding tasks by user ID and priority: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public List<Task> findByUserIdAndCompleted(Long userId, boolean completed) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND completed = ? ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setBoolean(2, completed);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding tasks by user ID and completed: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public List<Task> findByUserIdAndDueDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND due_date BETWEEN ? AND ? ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(startDate));
            stmt.setTimestamp(3, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding tasks by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    @Override
    public List<Task> findOverdueTasks(Long userId, LocalDateTime currentDate) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND due_date < ? AND completed = false ORDER BY due_date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(currentDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding overdue tasks: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    /**
     * Map database ResultSet to Task entity
     *
     * @param rs ResultSet from database query
     * @return Mapped Task entity
     * @throws SQLException If ResultSet access fails
     */
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        boolean recurring = rs.getBoolean("recurring");
        Task task;

        if (recurring) {
            // Create recurring task
            RecurringTask recurringTask = new RecurringTask();

            // Set recurring-specific properties
            recurringTask.setFrequency(Frequency.fromString(rs.getString("recurrence_frequency")));

            Timestamp endDate = rs.getTimestamp("recurrence_end_date");
            if (endDate != null) {
                recurringTask.setEndDate(endDate.toLocalDateTime());
            }

            task = recurringTask;
        } else {
            // Create regular task
            task = new Task();
        }

        // Set common properties
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
        task.setCompleted(rs.getBoolean("completed"));
        task.setPriority(Priority.fromString(rs.getString("priority")));
        task.setSubjectId(rs.getLong("subject_id"));
        task.setUserId(rs.getLong("user_id"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return task;
    }
}

