package com.example.flora.Features.Bug.repository;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;

import java.util.List;
import java.util.Optional;


public interface BugRepository {

    List<Bug> findAll();

    void save(Bug bug);

    List<Bug> findByProject(String projectName);

    Optional<Bug> findById(String id);

    Optional<Bug> updateStatus(String bugId, BugStatus newStatus);

    Optional<Bug> assignFixer(String bugId, String fixerUserId);

    List<String> findDistinctProjectNames();

    List<Bug> findFiltered(String projectName, BugSeverity severity, BugStatus status);
}