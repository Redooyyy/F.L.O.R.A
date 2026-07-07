package com.example.flora.Features.Project.service;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Core.Notify.Notify;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.model.ProjectMembership;
import com.example.flora.Features.Project.model.ProjectRole;
import com.example.flora.Features.Project.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(String name, String description, String ownerID) {
        // TODO: replace "121" with a real ID generator (UUID.randomUUID().toString())
        Project project = new Project("0", name, description, ownerID, DateAndTime.now());
        projectRepository.save(project);
        projectRepository.addMember(project.getId(), ownerID, ProjectRole.LEADER);
        return project;
    }

    public Project createProject(String name, String description, String ownerID,
                                 List<String> devices, List<String> techs) {
        Project project = new Project("0", name, description, ownerID,
                DateAndTime.now(), devices, techs);
        projectRepository.save(project);
        projectRepository.addMember(project.getId(), ownerID, ProjectRole.LEADER);
        Notify.projectCreated(Integer.parseInt(ownerID),name);
        return project;
    }

    public void updateProject(Project project) {
        projectRepository.update(project);
    }

    public void deleteProject(String id) {
        projectRepository.delete(id);
    }

    public void addMember(String projectId, String userId) {
        projectRepository.addMember(projectId, userId, ProjectRole.MEMBER);
    }

    public void removeMember(String projectId, String userId) {
        projectRepository.removeMember(projectId, userId);
    }

    public Optional<ProjectRole> getUserRole(String projectId, String userId) {
        return projectRepository.findUserRole(projectId, userId);
    }


    public List<ProjectMembership> getProjectsForUser(String userId) {
        return projectRepository.findByUserId(userId);
    }


    public List<ProjectMembership> getProjectsWhereLeader(String userId) {
        return projectRepository.findByUserIdAndRole(userId, ProjectRole.LEADER);
    }


    public List<ProjectMembership> getProjectsWhereMember(String userId) {
        return projectRepository.findByUserIdAndRole(userId, ProjectRole.MEMBER);
    }


    @Deprecated
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Deprecated
    public List<Project> getProjectsByOwner(String ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }

    public List<String> getProjectMembers(String projectId) {
        return projectRepository.findMemberUsernamesByProjectId(projectId);
    }
}