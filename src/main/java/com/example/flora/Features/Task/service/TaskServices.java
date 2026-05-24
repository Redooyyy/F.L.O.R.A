package com.example.flora.Features.Task.service;

import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import com.example.flora.Features.Task.repository.TaskRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class TaskServices {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TaskRepository taskRepository;

    public TaskServices(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public Task assignTask(String title, String description,
                           String projectId, String assigneeId, LocalDate dueDate) {
        Task task = new Task(
                UUID.randomUUID().toString(),
                title, description,
                TaskStatus.TODO,
                projectId, assigneeId,
                dueDate != null ? dueDate.format(DATE_FMT) : null,
                LocalDate.now().format(DATE_FMT)
        );
        taskRepository.save(task);
        return task;
    }


    public Task saveDraft(String title, String description,
                          String projectId, LocalDate dueDate) {
        Task task = new Task(
                UUID.randomUUID().toString(),
                title, description,
                TaskStatus.TODO,
                projectId, null,            // no assignee → draft
                dueDate != null ? dueDate.format(DATE_FMT) : null,
                LocalDate.now().format(DATE_FMT)
        );
        taskRepository.save(task);
        return task;
    }


    public void updateStatus(String taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setStatus(newStatus);
        taskRepository.update(task);
    }

    public void markDone(String taskId) {
        updateStatus(taskId, TaskStatus.DONE);
    }

    public void reassign(String taskId, String newAssigneeId) {
        if (newAssigneeId == null || newAssigneeId.isBlank())
            throw new IllegalArgumentException("Assignee ID must not be blank");
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setAssigneeId(newAssigneeId);
        taskRepository.update(task);
    }

    public void updateDeadline(String taskId, LocalDate newDueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setDueDate(newDueDate != null ? newDueDate.format(DATE_FMT) : null);
        taskRepository.update(task);
    }


    public void deleteTask(String taskId) {
        taskRepository.delete(taskId);
    }


    public List<Task> getTasksByProject(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
