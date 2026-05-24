package com.example.flora.Features.Settings.viewmodel;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Settings.model.UserSettings;
import com.example.flora.Features.Settings.service.SettingsService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;


public class SettingsViewModel {

    private final SettingsService service;


    private final StringProperty displayName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty bio = new SimpleStringProperty("");
    private final StringProperty avatarColor = new SimpleStringProperty("#7C6AF7");


    private final BooleanProperty notifyTaskAssign = new SimpleBooleanProperty(true);
    private final BooleanProperty notifyBugReport = new SimpleBooleanProperty(true);
    private final BooleanProperty notifyMention = new SimpleBooleanProperty(true);


    private final ObservableList<Project> leaderProjects = FXCollections.observableArrayList();


    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty saveSuccess = new SimpleBooleanProperty(false);


    private UserSettings current;
    private String currentUserId;
    private List<Project> leaderProjectsSnapshot;   // for auth checks in service

    public SettingsViewModel(SettingsService service) {
        this.service = service;
    }


    public void init(String userId, String email) {
        this.currentUserId = userId;
        current = service.getOrCreate(userId, email);
        loadFromModel();
        loadLeaderProjects();
    }


    public void savePersonalInfo() {
        if (current == null) return;
        current.setDisplayName(displayName.get());
        current.setEmail(email.get());
        current.setBio(bio.get());
        current.setAvatarColor(avatarColor.get());
        current.setNotifyOnTaskAssign(notifyTaskAssign.get());
        current.setNotifyOnBugReport(notifyBugReport.get());
        current.setNotifyOnMention(notifyMention.get());
        try {
            service.saveSettings(current);
            setStatus("Changes saved successfully!", true);
        } catch (Exception e) {
            setStatus(e.getMessage(), false);
        }
    }

    public void renameProject(Project project, String newName) {
        try {
            service.renameProject(project.getId(), newName, currentUserId, leaderProjectsSnapshot);
            loadLeaderProjects();
            setStatus("Project renamed to \"" + newName + "\"", true);
        } catch (Exception e) {
            setStatus(e.getMessage(), false);
        }
    }

    public void deleteProject(Project project) {
        try {
            service.deleteProject(project.getId(), currentUserId, leaderProjectsSnapshot);
            loadLeaderProjects();
            setStatus("Project \"" + project.getName() + "\" deleted.", true);
        } catch (Exception e) {
            setStatus(e.getMessage(), false);
        }
    }


    private void loadFromModel() {
        displayName.set(nvl(current.getDisplayName()));
        email.set(nvl(current.getEmail()));
        bio.set(nvl(current.getBio()));
        avatarColor.set(nvl(current.getAvatarColor(), "#7C6AF7"));
        notifyTaskAssign.set(current.isNotifyOnTaskAssign());
        notifyBugReport.set(current.isNotifyOnBugReport());
        notifyMention.set(current.isNotifyOnMention());
    }

    private void loadLeaderProjects() {
        leaderProjectsSnapshot = service.getLeaderProjects(currentUserId);
        leaderProjects.setAll(leaderProjectsSnapshot);
    }

    private void setStatus(String msg, boolean success) {
        statusMessage.set(msg);
        saveSuccess.set(success);
    }

    private String nvl(String v) {
        return v != null ? v : "";
    }

    private String nvl(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }


    public StringProperty displayNameProperty() {
        return displayName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty bioProperty() {
        return bio;
    }

    public StringProperty avatarColorProperty() {
        return avatarColor;
    }

    public BooleanProperty notifyTaskAssignProperty() {
        return notifyTaskAssign;
    }

    public BooleanProperty notifyBugReportProperty() {
        return notifyBugReport;
    }

    public BooleanProperty notifyMentionProperty() {
        return notifyMention;
    }

    public ObservableList<Project> getLeaderProjects() {
        return leaderProjects;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public BooleanProperty saveSuccessProperty() {
        return saveSuccess;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }
}