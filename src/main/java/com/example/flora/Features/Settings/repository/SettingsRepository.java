package com.example.flora.Features.Settings.repository;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Settings.model.UserSettings;

import java.util.List;
import java.util.Optional;

public interface SettingsRepository {

    Optional<UserSettings> findByUserId(String userId);

    void save(UserSettings settings);

    List<Project> findProjectsByLeader(String leaderId);

    void renameProject(String projectId, String newName);

    void deleteProject(String projectId);
}