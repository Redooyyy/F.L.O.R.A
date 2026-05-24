package com.example.flora.Features.Project.service;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.repository.ProjectRepository;

import java.util.List;

public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String name, String description, String ownerID) {
        //TODO: must define project ID logic
        Project project = new Project("121", name, description, ownerID, DateAndTime.now());
        projectRepository.save(project);
        return project;
    }

    public Project createProject(String name, String description, String ownerID,
                                 List<String> devices, List<String> techs) {
        //TODO: must define project ID logic
        Project project = new Project("121", name, description, ownerID,
                DateAndTime.now(), devices, techs);
        projectRepository.save(project);
        return project;
    }

    public void updateProject(Project project) {
        projectRepository.update(project);
    }

    public void deleteProject(String id) {
        projectRepository.delete(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByOwner(String ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }
}