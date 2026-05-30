package com.example.flora.Features.Project.ViewModel;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.model.ProjectMembership;
import com.example.flora.Features.Project.model.ProjectRole;
import com.example.flora.Features.Project.service.ProjectService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ProjectViewModel {

    private final ProjectService projectService;
    private String currUserID;

    private final ObservableList<ProjectMembership> memberships =
            FXCollections.observableArrayList();


    private final ObservableList<Project> projects = FXCollections.observableArrayList();


    private final ObservableList<Project> leaderProjects = FXCollections.observableArrayList();
    private final ObservableList<Project> memberProjects = FXCollections.observableArrayList();


    private final StringProperty projectName = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    private final ObservableList<String> devices = FXCollections.observableArrayList();
    private final ObservableList<String> techs = FXCollections.observableArrayList();

    public ProjectViewModel(ProjectService projectService, String currUserID) {
        this.projectService = projectService;
        this.currUserID = currUserID;
    }

    public String getCurrUserID() {
        return currUserID;
    }

    public void loadProject() {
        List<ProjectMembership> result = projectService.getProjectsForUser(currUserID);

        memberships.setAll(result);

        projects.clear();
        leaderProjects.clear();
        memberProjects.clear();

        for (ProjectMembership m : result) {
            projects.add(m.getProject());
            if (m.getRole() == ProjectRole.LEADER) {
                leaderProjects.add(m.getProject());
            } else {
                memberProjects.add(m.getProject());
            }
        }
    }

    public void addMembers(String projectID, String invitedUserID){
        projectService.addMember(projectID,invitedUserID);
    }

    public void createProject() {
        try {
            projectService.createProject(projectName.get(), description.get(), currUserID);
            resetForm();
            loadProject();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void createProject(List<String> deviceList, List<String> techList) {
        try {
            projectService.createProject(
                    projectName.get(), description.get(), currUserID,
                    deviceList, techList);
            resetForm();
            loadProject();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProject(Project project) {
        projectService.deleteProject(project.getId());
        loadProject();
    }


    public boolean isLeaderOf(Project project) {
        return memberships.stream()
                .filter(m -> m.getProject().getId().equals(project.getId()))
                .findFirst()
                .map(ProjectMembership::isLeader)
                .orElse(false);
    }


    public ProjectRole getRoleFor(Project project) {
        return memberships.stream()
                .filter(m -> m.getProject().getId().equals(project.getId()))
                .findFirst()
                .map(ProjectMembership::getRole)
                .orElse(null);
    }


    public ObservableList<Project> getProjects() {
        return projects;
    }

    public ObservableList<Project> getLeaderProjects() {
        return leaderProjects;
    }

    public ObservableList<Project> getMemberProjects() {
        return memberProjects;
    }

    public ObservableList<ProjectMembership> getMemberships() {
        return memberships;
    }

    public StringProperty projectNameProperty() {
        return projectName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<String> getDevices() {
        return devices;
    }

    public ObservableList<String> getTechs() {
        return techs;
    }

    public List<String> getMembers(String projectId) {
        return projectService.getProjectMembers(projectId);
    }


    private void resetForm() {
        projectName.setValue("");
        description.setValue("");
        devices.clear();
        techs.clear();
    }
}