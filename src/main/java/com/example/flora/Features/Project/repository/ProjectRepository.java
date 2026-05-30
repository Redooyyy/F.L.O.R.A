package com.example.flora.Features.Project.repository;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.model.ProjectMembership;
import com.example.flora.Features.Project.model.ProjectRole;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);

    void update(Project project);

    void delete(String id);

    Optional<Project> findById(String id);

    List<Project> findAll();

    List<Project> findByOwnerId(String ownerId);

    List<ProjectMembership> findByUserId(String userId);

    List<ProjectMembership> findByUserIdAndRole(String userId, ProjectRole role);

    void addMember(String projectId, String userId, ProjectRole role);

    void removeMember(String projectId, String userId);

    Optional<ProjectRole> findUserRole(String projectId, String userId);

    List<String> findMemberUsernamesByProjectId(String projectId);
}