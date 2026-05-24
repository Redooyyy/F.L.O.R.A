package com.example.flora.Features.Home.repository;

import com.example.flora.Features.Home.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final Connection connection;

    public NotificationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createNotification(int userId, String title, String description) {
        String sql = "INSERT INTO notifications (user_id, title, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
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

    private Notification mapRow(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_read")
        );
    }
}