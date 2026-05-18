package com.example.flora.Features.Task.service;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import com.example.flora.Features.Task.repository.TaskRepository;

import java.util.List;

public class TaskServices {
    private final TaskRepository taskRepository;

    public TaskServices(TaskRepository taskRepository){
        this.taskRepository=taskRepository;
    }

    public Task createTask(String title, String description, String projectId, String dueDate){
        //TODO: task id fix
        Task task = new Task(null,title,description, TaskStatus.TODO,projectId,null,dueDate, DateAndTime.now());
        taskRepository.save(task);
        return task;
    }

    public void updateStatus(String taskID, TaskStatus status){
        Task task = taskRepository.findById(taskID).orElse(null);
        task.setStatus(status);
        taskRepository.update(task);
    }

    public void deleteTask(String taskID){
        taskRepository.delete(taskID);
    }

    public List<Task> getTasksByProject(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
