package com.example.flora.Features.Task.ViewModel;

import com.example.flora.Core.session.UserSession;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import com.example.flora.Features.Task.service.TaskServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class TaskViewModel {


    private final TaskServices taskServices;


    private String currentProjectId;
    private String currentUserId;
    private boolean isLeader;


    private final ObservableList<Task> tasks = FXCollections.observableArrayList();


    private final StringProperty activeFilter = new SimpleStringProperty("ALL");


    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");


    private LocalDate pendingAssignDeadline;
    private LocalDate pendingDraftDeadline;


    public TaskViewModel(TaskServices taskServices, String currentUserId) {
        this.taskServices = taskServices;
        this.currentUserId = currentUserId;
    }


    public void init(String projectId, boolean isLeader) {
        this.currentProjectId = projectId;
        this.isLeader = isLeader;
        activeFilter.set(isLeader ? "ALL" : "MY");
        loadTasks();
    }


    public void loadTasks() {
        tasks.setAll(taskServices.getTasksByProject(currentProjectId));
    }


    public void assignTask(String taskTitle, String assigneeId, LocalDate deadline) {
        if (taskTitle.isBlank() || assigneeId.isBlank()) return;
        Task t = taskServices.assignTask(
                taskTitle, description.get(),
                currentProjectId, assigneeId, deadline
        );
        tasks.add(t);
        pendingAssignDeadline = null;
        clearForm();
    }


    public void saveDraftTask(String taskTitle, LocalDate deadline) {
        if (taskTitle.isBlank()) return;
        Task t = taskServices.saveDraft(
                taskTitle, description.get(),
                currentProjectId, deadline
        );
        tasks.add(t);
        pendingDraftDeadline = null;
        clearForm();
    }

    public void updateStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
        taskServices.updateStatus(task.getId(), newStatus);
        refreshList();
    }

    public void markDone(Task task) {
        updateStatus(task, TaskStatus.DONE);
    }

    public void reassign(Task task, String newAssigneeId) {
        if (newAssigneeId.isBlank()) return;
        task.setAssigneeId(newAssigneeId);
        taskServices.reassign(task.getId(), newAssigneeId);
        refreshList();
    }

    public void updateDeadline(Task task, LocalDate newDueDate) {
        task.setDueDate(newDueDate != null ? newDueDate.format(
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")) : null);
        taskServices.updateDeadline(task.getId(), newDueDate);
        refreshList();
    }

    public void deleteTask(Task task) {
        taskServices.deleteTask(task.getId());
        tasks.remove(task);
    }


    public void setActiveFilter(String filter) {
        activeFilter.set(filter);
    }


    public List<Task> getFilteredTasks() {
        return switch (activeFilter.get()) {
            case "MY" -> tasks.stream()
                    .filter(t -> !t.isDraft() && currentUserId.equalsIgnoreCase(t.getAssigneeId()))
                    .collect(Collectors.toList());
            case "COMPLETED" -> tasks.stream()
                    .filter(t -> !t.isDraft() && t.getStatus() == TaskStatus.DONE)
                    .collect(Collectors.toList());
            case "DUE" -> tasks.stream()
                    .filter(t -> !t.isDraft() && t.getStatus() != TaskStatus.DONE)
                    .collect(Collectors.toList());
            case "DRAFTS" -> tasks.stream()
                    .filter(Task::isDraft)
                    .collect(Collectors.toList());
            default ->  // ALL — excludes drafts
                    tasks.stream()
                            .filter(t -> !t.isDraft())
                            .collect(Collectors.toList());
        };
    }


    public void setPendingAssignDeadline(LocalDate date) {
        pendingAssignDeadline = date;
    }

    public void setPendingDraftDeadline(LocalDate date) {
        pendingDraftDeadline = date;
    }

    public LocalDate getPendingAssignDeadline() {
        return pendingAssignDeadline;
    }

    public LocalDate getPendingDraftDeadline() {
        return pendingDraftDeadline;
    }

    public void clearPendingAssignDeadline() {
        pendingAssignDeadline = null;
    }

    public void clearPendingDraftDeadline() {
        pendingDraftDeadline = null;
    }


    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public StringProperty activeFilterProperty() {
        return activeFilter;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getCurrentProjectId() {
        return currentProjectId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public boolean isLeader() {
        return isLeader;
    }


    private void refreshList() {
        List<Task> snapshot = List.copyOf(tasks);
        tasks.setAll(snapshot);
    }

    private void clearForm() {
        title.set("");
        description.set("");
    }
}