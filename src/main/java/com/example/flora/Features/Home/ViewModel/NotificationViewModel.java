package com.example.flora.Features.Home.ViewModel;

import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.model.NotificationType;
import com.example.flora.Features.Home.services.NotificationService;
import com.example.flora.Features.Project.service.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotificationViewModel {

    private final NotificationService notificationService;
    private final ProjectService projectService;   // needed for invite accept
    private final int userID;
    private final String userStringID;     // ProjectService uses String IDs

    private final ObservableList<Notification> notifications =
            FXCollections.observableArrayList();


    public NotificationViewModel(NotificationService notificationService,
                                 ProjectService projectService,
                                 int userID,
                                 String userStringID) {
        this.notificationService = notificationService;
        this.projectService = projectService;
        this.userID = userID;
        this.userStringID = userStringID;
    }

    public ObservableList<Notification> getNotifications() {
        return notifications;
    }

    public void load() {
        notifications.setAll(notificationService.loadNotifications(userID));
    }


    public void markAsRead(Notification notification) {
        notificationService.markRead(notification.getId());
        notification.markAsRead();
    }


    public void delete(Notification notification) {
        notificationService.delete(notification.getId(), userID);
        notifications.remove(notification);
    }

    public void deleteAll() {
        notificationService.deleteAll(userID);
        notifications.clear();
    }

    public void create(int userId, String title, String desc) {
        notificationService.create(userId, title, desc);
        load();
    }


    public void acceptInvite(Notification notification) {
        if (notification.getType() != NotificationType.PROJECT_INVITE) return;

        try {
            //TODO:ROLE MUST
            projectService.addMember(notification.getProjectName(), userStringID);
        } catch (Exception e) {
            throw new RuntimeException("Failed to accept invitation: " + e.getMessage(), e);
        }

        markAsRead(notification);
        delete(notification);
    }


    public void declineInvite(Notification notification) {
        if (notification.getType() != NotificationType.PROJECT_INVITE) return;
        delete(notification);
    }

    public void createTestInvite(int userId, String project, String sender, String role) {
        notificationService.notifyProjectInvite(userId, project, sender, role);
    }

    public void createTestTaskAssigned(int userId, String task, String project, String sender) {
        notificationService.notifyTaskAssigned(userId, task, project, sender);
    }
}