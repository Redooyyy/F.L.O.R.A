package com.example.flora.Features.Home.repository;

import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.model.NotificationType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Expected DB schema:
 *
 * CREATE TABLE notifications (
 *     id           INT          PRIMARY KEY AUTO_INCREMENT,
 *     user_id      INT          NOT NULL,
 *     title        VARCHAR(255) NOT NULL,
 *     description  TEXT,
 *     type         VARCHAR(50)  NOT NULL DEFAULT 'GENERAL',
 *     sender_name  VARCHAR(255),
 *     project_name VARCHAR(255),
 *     role         VARCHAR(100),
 *     is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
 *     created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
 *     project_id   INT         DEFAULT NULL
 * );
 */
public class NotificationRepositoryImpl implements NotificationRepository {

    private final Connection connection;

    public NotificationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void createNotification(int userId, String title, String description) {
        createNotification(userId, title, description, NotificationType.GENERAL, null, null, null);
    }

    public void createNotification(int userId,
                                   String title,
                                   String description,
                                   NotificationType type,
                                   String senderName,
                                   String projectName,
                                   String role) {
        String sql = """
                INSERT INTO notifications
                    (user_id, title, description, type, sender_name, project_name, role)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, type.name());
            ps.setString(5, senderName);
            ps.setString(6, projectName);
            ps.setString(7, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create notification: " + e.getMessage(), e);
        }
    }

    @Override
    public void createNotification(int userId,
                                   String title,
                                   String description,
                                   NotificationType type,
                                   String senderName,
                                   String projectName,
                                   String role,
                                   Integer projectId) {  // added
        String sql = """
            INSERT INTO notifications
                (user_id, title, description, type, sender_name, project_name, role, project_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, type.name());
            ps.setString(5, senderName);
            ps.setString(6, projectName);
            ps.setString(7, role);
            if (projectId != null) ps.setInt(8, projectId);
            else ps.setNull(8, Types.INTEGER);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create notification: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Notification> getNotifications(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) notifications.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get notifications: " + e.getMessage(), e);
        }
        return notifications;
    }

    @Override
    public void markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark notification as read: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteNotification(int notificationId, int userId) {
        String sql = "DELETE FROM notifications WHERE id = ? AND user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete notification: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllNotifications(int userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete all notifications: " + e.getMessage(), e);
        }
    }


    private Notification mapRow(ResultSet rs) throws SQLException {
        NotificationType type;
        try {
            type = NotificationType.valueOf(rs.getString("type"));
        } catch (IllegalArgumentException | NullPointerException ex) {
            type = NotificationType.GENERAL;
        }

        return new Notification(
                rs.getInt   ("id"),
                rs.getInt("project_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_read"),
                type,
                rs.getString("sender_name"),
                rs.getString("project_name"),
                rs.getString("role")
        );
    }
}