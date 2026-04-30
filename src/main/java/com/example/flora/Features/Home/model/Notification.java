package com.example.flora.Features.Home.model;

import java.time.LocalDateTime;

public class Notification {

    private final int id;
    private final String title;
    private final String description;
    private final LocalDateTime time;
    private boolean isRead;

    public Notification(int id, String title, String description, LocalDateTime time, boolean isRead) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.isRead = isRead;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getTime() { return time; }
    public boolean isRead() { return isRead; }

    public void markAsRead() {
        this.isRead = true;
    }
}
