package com.example.flora.Features.Home.repository;

import com.example.flora.Features.Home.model.Notification;

import java.util.List;

public interface NotificationRepository {

    void createNotification(int userId, String title, String description);

    List<Notification> getNotifications(int userId);

    void markAsRead(int notificationId);

    void deleteNotification(int notificationId, int userId);
}
