package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.config.DatabaseConfig;
import com.smartstudyplanner.smart_study_planner_backend.model.Progress;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ProgressDAO
 */
public class ProgressDAOImpl implements ProgressDAO {

    @Override
    public Optional<Progress> findById(Long id) {
        String sql = "SELECT * FROM progress WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProgress(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding progress by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Progress> findAll() {
        List<Progress> progressList = new ArrayList<>();
        String sql = "SELECT * FROM progress ORDER BY date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                progressList.add(mapResultSetToProgress(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all progress: " + e.getMessage());
            e.printStackTrace();
        }

        return progressList;
    }

    @Override
    public Progress save(Progress progress) {
        String sql = "INSERT INTO progress (date, minutes_spent, notes, task_id, user_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(progress.getDate()));
            stmt.setInt(2, progress.getMinutesSpent());
            stmt.setString(3, progress.getNotes());
            stmt.setLong(4, progress.getTaskId());
            stmt.setLong(5, progress.getUserId());
            stmt.setTimestamp(6, Timestamp.valueOf(progress.getCreatedAt()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    progress.setId(rs.getLong("id"));
                    return progress;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving progress: " + e.getMessage());
            e.printStackTrace();
        }

        return progress;
    }

    @Override
    public Progress update(Progress progress) {
        String sql = "UPDATE progress SET date = ?, minutes_spent = ?, notes = ? " +
                "WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(progress.getDate()));
            stmt.setInt(2, progress.getMinutesSpent());
            stmt.setString(3, progress.getNotes());
            stmt.setLong(4, progress.getId());
            stmt.setLong(5, progress.getUserId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return progress;
            }

        } catch (SQLException e) {
            System.err.println("Error updating progress: " + e.getMessage());
            e.printStackTrace();
        }

        return progress;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM progress WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting progress: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Progress> findByUserId(Long userId) {
        List<Progress> progressList = new ArrayList<>();
        String sql = "SELECT * FROM progress WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding progress by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return progressList;
    }

    @Override
    public List<Progress> findByTaskId(Long taskId) {
        List<Progress> progressList = new ArrayList<>();
        String sql = "SELECT * FROM progress WHERE task_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, taskId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding progress by task ID: " + e.getMessage());
            e.printStackTrace();
        }

        return progressList;
    }

    @Override
    public List<Progress> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Progress> progressList = new ArrayList<>();
        String sql = "SELECT * FROM progress WHERE user_id = ? AND date BETWEEN ? AND ? ORDER BY date";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding progress by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return progressList;
    }

    @Override
    public int getTotalMinutesByUserId(Long userId) {
        String sql = "SELECT SUM(minutes_spent) AS total_minutes FROM progress WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_minutes");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting total minutes: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Map database ResultSet to Progress entity
     *
     * @param rs ResultSet from database query
     * @return Mapped Progress entity
     * @throws SQLException If ResultSet access fails
     */
    private Progress mapResultSetToProgress(ResultSet rs) throws SQLException {
        Progress progress = new Progress();
        progress.setId(rs.getLong("id"));
        progress.setDate(rs.getDate("date").toLocalDate());
        progress.setMinutesSpent(rs.getInt("minutes_spent"));
        progress.setNotes(rs.getString("notes"));
        progress.setTaskId(rs.getLong("task_id"));
        progress.setUserId(rs.getLong("user_id"));
        progress.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return progress;
    }
}

