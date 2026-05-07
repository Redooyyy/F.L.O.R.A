package com.example.flora.Features.Task.model;

public class Task {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private String projectId;
    private String assigneeId;
    private String dueDate;
    private String createdAt;

    public Task() {}

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

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
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

    public void setDescription(String description) {
        this.description = description;
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
