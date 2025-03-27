package com.smartstudyplanner.smart_study_planner_backend.dao;

import com.smartstudyplanner.smart_study_planner_backend.config.DatabaseConfig;
import com.smartstudyplanner.smart_study_planner_backend.model.Subject;
import com.smartstudyplanner.smart_study_planner_backend.model.enums.Priority;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of SubjectDAO
 */
public class SubjectDAOImpl implements SubjectDAO {

    @Override
    public Optional<Subject> findById(Long id) {
        String sql = "SELECT * FROM subjects WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSubject(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding subject by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Subject> findAll() {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all subjects: " + e.getMessage());
            e.printStackTrace();
        }

        return subjects;
    }

    @Override
    public Subject save(Subject subject) {
        String sql = "INSERT INTO subjects (name, description, priority, user_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.setString(3, subject.getPriority().name());
            stmt.setLong(4, subject.getUserId());
            stmt.setTimestamp(5, Timestamp.valueOf(subject.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(subject.getUpdatedAt()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    subject.setId(rs.getLong("id"));
                    return subject;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving subject: " + e.getMessage());
            e.printStackTrace();
        }

        return subject;
    }

    @Override
    public Subject update(Subject subject) {
        String sql = "UPDATE subjects SET name = ?, description = ?, priority = ?, " +
                "updated_at = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            subject.updateTimestamp();

            stmt.setString(1, subject.getName());
            stmt.setString(2, subject.getDescription());
            stmt.setString(3, subject.getPriority().name());
            stmt.setTimestamp(4, Timestamp.valueOf(subject.getUpdatedAt()));
            stmt.setLong(5, subject.getId());
            stmt.setLong(6, subject.getUserId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return subject;
            }

        } catch (SQLException e) {
            System.err.println("Error updating subject: " + e.getMessage());
            e.printStackTrace();
        }

        return subject;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM subjects WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting subject: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Subject> findByUserId(Long userId) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE user_id = ? ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(mapResultSetToSubject(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding subjects by user ID: " + e.getMessage());
            e.printStackTrace();
        }

        return subjects;
    }

    @Override
    public List<Subject> findByUserIdAndPriority(Long userId, Priority priority) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE user_id = ? AND priority = ? ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, priority.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(mapResultSetToSubject(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding subjects by user ID and priority: " + e.getMessage());
            e.printStackTrace();
        }

        return subjects;
    }

    @Override
    public List<Subject> findByUserIdAndNameContaining(Long userId, String name) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE user_id = ? AND LOWER(name) LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, "%" + name.toLowerCase() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(mapResultSetToSubject(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding subjects by user ID and name: " + e.getMessage());
            e.printStackTrace();
        }

        return subjects;
    }

    /**
     * Map database ResultSet to Subject entity
     *
     * @param rs ResultSet from database query
     * @return Mapped Subject entity
     * @throws SQLException If ResultSet access fails
     */
    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setId(rs.getLong("id"));
        subject.setName(rs.getString("name"));
        subject.setDescription(rs.getString("description"));
        subject.setPriority(Priority.fromString(rs.getString("priority")));
        subject.setUserId(rs.getLong("user_id"));
        subject.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        subject.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return subject;
    }
}

