package com.example.flora.Features.Task.repository;

import com.example.flora.Features.Task.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    void save(Task task);
    void update(Task task);
    void delete(String id);
    Optional<Task> findById(String id);
    List<Task> findByProjectId(String projectId);
    List<Task> findAll();

}
