package com.example.flora.Features.Project.repository;

import com.example.flora.Features.Project.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);
    void update(Project project);
    void delete(String id);
    Optional<Project> findById(String id);
    List<Project> findAll();
    List<Project> findByOwnerId(String ownerId);
}
