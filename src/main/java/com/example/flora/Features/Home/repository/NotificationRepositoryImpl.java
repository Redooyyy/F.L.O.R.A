package com.example.flora.Features.Home.repository;

import com.example.flora.Features.Home.model.Notification;


import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public void createNotification(int userId, String title, String description) {
        //TODO: save to sql
    }

    @Override
    public List<Notification> getNotifications(int userId) {
        //TODO: Get all notification from sql
        return null;
    }

    @Override
    public void markAsRead(int notificationId) {
        //TODO: mark as read in sql
    }

    @Override
    public void deleteNotification(int notificationId, int userId) {
        //TODO: delete notification in sql
    }

}
