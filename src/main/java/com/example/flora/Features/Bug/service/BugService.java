package com.example.flora.Features.Bug.service;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BugService {

    List<Bug> getAllBugs();

    List<Bug> getFilteredBugs(String projectName, BugSeverity severity, BugStatus status);

    List<String> getDistinctProjectNames();

    Map<String, ProjectSummary> getProjectSummaries();

    String getProjectLeader(String projectName);

    Optional<Bug> claimBug(int bugId, String currentUserId);

    Optional<Bug> markFixed(int bugId, String currentUserId);

    Optional<Bug> toggleProgress(int bugId, String currentUserId);

    Optional<Bug> assignBug(int bugId, String assigneeUserId, String leaderUserId);

    void reportBug(String title, String reportedByUserId, BugSeverity severity, String projectName);

    Optional<Bug> updateBugStatus(int bugId, BugStatus newStatus);

    record ProjectSummary(
            String projectName,
            String leaderUserId,
            long openCount,
            long criticalOpen,
            long highOpen,
            long mediumOpen,
            long lowOpen
    ) {
    }
}