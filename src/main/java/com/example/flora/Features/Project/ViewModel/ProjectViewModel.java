package com.example.flora.Features.Project.ViewModel;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Project.service.ProjectService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ProjectViewModel {
    private final ProjectService projectService;
    private String currUserID;

    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final StringProperty projectName = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");

    private final ObservableList<String> devices = FXCollections.observableArrayList();
    private final ObservableList<String> techs = FXCollections.observableArrayList();

    public ProjectViewModel(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setCurrUserID(String userID) {
        this.currUserID = userID;
    }

    public void loadProject() {
        projects.setAll(projectService.getAllProjects());
    }

    public void createProject() {
        try {
            projectService.createProject(projectName.get(), description.get(), currUserID);
            projectName.setValue("");
            description.setValue("");
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
            projectName.setValue("");
            description.setValue("");
            devices.clear();
            techs.clear();
            loadProject();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProject(Project project) {
        projectService.deleteProject(project.getId());
        loadProject();
    }

    public ObservableList<Project> getProjects() {
        return projects;
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
}