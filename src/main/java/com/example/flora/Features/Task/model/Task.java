package com.example.flora.Features.Task.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {

    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private String projectId;
    private String assigneeId;
    private String dueDate;
    private String createdAt;

    public Task() {
    }

    public Task(String id, String title, String description, TaskStatus status,
                String projectId, String assigneeId, String dueDate, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }


    public static Task create(String id, String title, String description,
                              TaskStatus status, String projectId, String assigneeId,
                              LocalDate dueDate, LocalDate createdAt) {
        return new Task(
                id, title, description, status, projectId, assigneeId,
                dueDate != null ? dueDate.format(DATE_FMT) : null,
                createdAt != null ? createdAt.format(DATE_FMT) : null
        );
    }

    public boolean isDraft() {
        return assigneeId == null || assigneeId.isBlank();
    }


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}