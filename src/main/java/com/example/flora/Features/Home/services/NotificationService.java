package com.example.flora.Features.Home.services;

import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.model.NotificationType;
import com.example.flora.Features.Home.repository.NotificationRepository;
import com.example.flora.Features.Home.repository.NotificationRepositoryImpl;

import java.util.List;

public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<Notification> loadNotifications(int userId) {
        return repository.getNotifications(userId);
    }


    public void notifyTaskAssigned(int targetUserId, String taskName, String projectName, String assignedBy) {
        String title = "Task Assigned";
        String desc = "You have been assigned the task: " + taskName;
        createTyped(targetUserId, title, desc,
                NotificationType.TASK_ASSIGNED, assignedBy, projectName, "Member");
    }

    public void notifyBugAssigned(int targetUserId, String bugTitle, String projectName, String assignedBy) {
        String title = "Bug Assigned";
        String desc = "You have been assigned a bug: " + bugTitle;
        createTyped(targetUserId, title, desc,
                NotificationType.BUG_ASSIGNED, assignedBy, projectName, "Developer");
    }


    public void notifyProjectInvite(int targetUserId, int projectId, String projectName, String invitedBy, String roleOffered) {
        String title = "Project Invitation";
        String desc = invitedBy + " invited you to join: " + projectName;
        createTypedWithProject(targetUserId, title, desc,
                NotificationType.PROJECT_INVITE, invitedBy, projectName, roleOffered, projectId);
    }

    public void createTypedWithProject(int userId, String title, String desc, NotificationType type,
                                       String senderName, String projectName, String role, int projectId) {
        if (repository instanceof NotificationRepositoryImpl impl) {
            impl.createNotification(userId, title, desc, type, senderName, projectName, role, projectId);
        } else {
            repository.createNotification(userId, title, desc);
        }
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

    public void deleteAll(int userId) {
        repository.deleteAllNotifications(userId);
    }


    public void createTyped(int userId,String title, String desc, NotificationType type, String senderName, String projectName, String role) {
        if (repository instanceof NotificationRepositoryImpl impl) {
            impl.createNotification(userId, title, desc, type, senderName, projectName, role);
        } else {
            repository.createNotification(userId, title, desc);
        }
    }
}