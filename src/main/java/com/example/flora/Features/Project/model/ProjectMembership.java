package com.example.flora.Features.Project.model;


public class ProjectMembership {

    private final Project project;
    private final ProjectRole role;

    public ProjectMembership(Project project, ProjectRole role) {
        this.project = project;
        this.role = role;
    }

    public Project getProject() {
        return project;
    }

    public ProjectRole getRole() {
        return role;
    }

    public boolean isLeader() {
        return role == ProjectRole.LEADER;
    }
}