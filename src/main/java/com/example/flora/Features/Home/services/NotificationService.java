package com.example.flora.Features.Home.services;

import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.repository.NotificationRepository;

import java.util.List;

public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<Notification> loadNotifications(int userId) {
        return repository.getNotifications(userId);
    }

    public void create(int userId, String title, String desc) {
        repository.createNotification(userId, title, desc);
    }

    public void markRead(int id) {
        repository.markAsRead(id);
    }

    public void delete(int id, int userId) {
        repository.deleteNotification(id, userId);
    }
}
