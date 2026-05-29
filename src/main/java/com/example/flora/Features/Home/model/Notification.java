package com.example.flora.Features.Home.model;

import java.time.LocalDateTime;

public class Notification {

    private final int id;
    private final int projectID;
    private final String title;
    private final String description;
    private final LocalDateTime time;
    private boolean isRead;

    private final NotificationType type;
    private final String senderName;
    private final String projectName;
    private final String role;

    public Notification(int id,int projectID, String title, String description, LocalDateTime time, boolean isRead, NotificationType type, String senderName, String projectName, String role) {
        this.id = id;
        this.projectID = projectID;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
        this.type = type;
        this.senderName = senderName;
        this.projectName = projectName;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public int getProjectID() {
        return projectID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public boolean isRead() {
        return isRead;
    }

    public NotificationType getType() {
        return type;
    }

    public String getSenderName() {
        return senderName != null ? senderName : "";
    }

    public String getProjectName() {
        return projectName != null ? projectName : "";
    }

    public String getRole() {
        return role != null ? role : "";
    }

    public void markAsRead() {
        this.isRead = true;
    }
}