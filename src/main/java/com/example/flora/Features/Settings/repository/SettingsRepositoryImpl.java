package com.example.flora.Features.Settings.repository;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Settings.model.UserSettings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingsRepositoryImpl implements SettingsRepository {

    private final Connection connection;

    public SettingsRepositoryImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public Optional<UserSettings> findByUserId(String userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load settings: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public void save(UserSettings s) {
        // Upsert — works on both SQLite and PostgreSQL (with minor dialect change)
        String sql = """
                INSERT INTO user_settings
                    (user_id, display_name, email, bio, avatar_color,
                     notify_task_assign, notify_bug_report, notify_mention, theme)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(user_id) DO UPDATE SET
                    display_name       = excluded.display_name,
                    email              = excluded.email,
                    bio                = excluded.bio,
                    avatar_color       = excluded.avatar_color,
                    notify_task_assign = excluded.notify_task_assign,
                    notify_bug_report  = excluded.notify_bug_report,
                    notify_mention     = excluded.notify_mention,
                    theme              = excluded.theme
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getUserId());
            ps.setString(2, s.getDisplayName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getBio());
            ps.setString(5, s.getAvatarColor());
            ps.setInt(6, s.isNotifyOnTaskAssign() ? 1 : 0);
            ps.setInt(7, s.isNotifyOnBugReport() ? 1 : 0);
            ps.setInt(8, s.isNotifyOnMention() ? 1 : 0);
            ps.setString(9, s.getTheme());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save settings: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Project> findProjectsByLeader(String leaderId) {
        String sql = "SELECT * FROM projects WHERE owner_id = ? ORDER BY created_at DESC";
        List<Project> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, leaderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProject(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load leader projects: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void renameProject(String projectId, String newName) {
        String sql = "UPDATE projects SET name = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rename project: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteProject(String projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete project: " + e.getMessage(), e);
        }
    }



    private UserSettings mapRow(ResultSet rs) throws SQLException {
        return new UserSettings(
                rs.getString("user_id"),
                rs.getString("display_name"),
                rs.getString("email"),
                rs.getString("bio"),
                rs.getString("avatar_color"),
                rs.getInt("notify_task_assign") == 1,
                rs.getInt("notify_bug_report") == 1,
                rs.getInt("notify_mention") == 1,
                rs.getString("theme")
        );
    }

    private Project mapProject(ResultSet rs) throws SQLException {
        return new Project(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("owner_id"),
                rs.getString("created_at")
        );
    }
}