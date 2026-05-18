package com.example.flora.Features.Task.repository;

import com.example.flora.Features.Task.model.Task;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryImpl implements TaskRepository {
    private final Connection connection;

    public TaskRepositoryImpl(Connection connection){
        this.connection = connection;
    }


    @Override
    public void save(Task task) {
        //TODO: save task via SQL query

    }

    @Override
    public void update(Task task) {
        //TODO: update task via SQL query

    }

    @Override
    public void delete(String id) {
        //TODO: delete task via SQL query;

    }

    @Override
    public Optional<Task> findById(String id) {
        //TODO: find task via SQL query with task ID
        return Optional.empty();
    }

    @Override
    public List<Task> findByProjectId(String projectId) {
        //TODO: find task via SQL query with project ID
        return List.of();
    }

    @Override
    public List<Task> findAll() {
        //TODO: find all task via SQL query with task ID
        return List.of();
    }
}
