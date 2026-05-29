package com.example.flora.Features.Home.repository;

import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.model.NotificationType;

import java.util.List;

public interface NotificationRepository {

    void createNotification(int userId, String title, String description);

    public void createNotification(int userId, String title, String description, NotificationType type, String senderName, String projectName, String role, Integer projectId);

    List<Notification> getNotifications(int userId);

    void markAsRead(int notificationId);

    public void deleteAllNotifications(int userId);

    void deleteNotification(int notificationId, int userId);
}
