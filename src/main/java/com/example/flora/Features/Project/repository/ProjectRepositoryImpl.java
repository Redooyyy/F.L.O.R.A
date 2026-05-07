package com.example.flora.Features.Project.repository;

import com.example.flora.Features.Project.model.Project;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class ProjectRepositoryImpl implements ProjectRepository{
    private final Connection connection;

    public ProjectRepositoryImpl(Connection connection){
        this.connection = connection;
    }

    @Override
    public void save(Project project) {
        //TODO: save in database via SQL query
    }

    @Override
    public void update(Project project) {
        //TODO: update in database via SQL query
    }

    @Override
    public void delete(String id) {
        //TODO: delete from database via sql query
    }

    @Override
    public Optional<Project> findById(String id) {
        //TODO: find specific project via SQL query
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        //TODO: find all project via SQL query
        return List.of();
    }

    @Override
    public List<Project> findByOwnerId(String ownerId) {
        //TODO: find all project via SQL query per user
        return List.of();
    }
}
