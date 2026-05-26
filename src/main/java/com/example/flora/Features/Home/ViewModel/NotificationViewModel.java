package com.example.flora.Features.Home.ViewModel;

import com.example.flora.Features.Home.model.Notification;
import com.example.flora.Features.Home.services.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotificationViewModel {

    private final NotificationService service;
    private final int userID;

    private final ObservableList<Notification> notifications =
            FXCollections.observableArrayList();

    public NotificationViewModel(NotificationService service, int userID) {
        this.service = service;
        this.userID = userID;
    }

    public ObservableList<Notification> getNotifications() {
        return notifications;
    }

    public void load() {
        notifications.setAll(service.loadNotifications(this.userID));
    }

    public void markAsRead(Notification notification) {
        service.markRead(notification.getId());
        notification.markAsRead();
    }

    public void delete(Notification notification, int userId) {
        service.delete(notification.getId(), userId);
        notifications.remove(notification);
    }

    public void create(int userId, String title, String desc) {
        service.create(userId, title, desc);
        load();
    }
}
