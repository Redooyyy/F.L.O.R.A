package com.example.flora.Features.Settings.service;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Settings.model.UserSettings;
import com.example.flora.Features.Settings.repository.SettingsRepository;

import java.util.List;

public class SettingsService {

    private final SettingsRepository repo;

    public SettingsService(SettingsRepository repo) {
        this.repo = repo;
    }


    public UserSettings getOrCreate(String userId, String email) {
        return repo.findByUserId(userId).orElseGet(() -> {
            UserSettings defaults = new UserSettings(
                    userId,
                    email.substring(0, email.indexOf('@')),  // username from email
                    email,
                    "",
                    "#7C6AF7",
                    true, true, true,
                    "DARK"
            );
            repo.save(defaults);
            return defaults;
        });
    }

    public void saveSettings(UserSettings settings) {
        validate(settings);
        repo.save(settings);
    }

    public List<Project> getLeaderProjects(String leaderId) {
        return repo.findProjectsByLeader(leaderId);
    }

    public void renameProject(String projectId, String newName, String requesterId, List<Project> leaderProjects) {
        boolean owns = leaderProjects.stream().anyMatch(p -> p.getId().equals(projectId));
        if (!owns) throw new SecurityException("Only the project leader can rename this project.");
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Project name cannot be empty.");
        repo.renameProject(projectId, newName.trim());
    }

    public void deleteProject(String projectId, String requesterId, List<Project> leaderProjects) {
        boolean owns = leaderProjects.stream().anyMatch(p -> p.getId().equals(projectId));
        if (!owns) throw new SecurityException("Only the project leader can delete this project.");
        repo.deleteProject(projectId);
    }

    private void validate(UserSettings s) {
        if (s.getDisplayName() == null || s.getDisplayName().isBlank())
            throw new IllegalArgumentException("Display name cannot be empty.");
        if (s.getEmail() == null || !s.getEmail().contains("@"))
            throw new IllegalArgumentException("Please enter a valid email address.");
    }
}