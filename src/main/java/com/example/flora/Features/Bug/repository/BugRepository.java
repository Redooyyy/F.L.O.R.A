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

    Optional<Bug> findById(int id);

    Optional<Bug> updateStatus(int bugId, BugStatus newStatus);

    Optional<Bug> assignFixer(int bugId, String fixerUserId);

    List<String> findDistinctProjectNames();

    List<Bug> findFiltered(String projectName, BugSeverity severity, BugStatus status);

    String findProjectLeader(String projectName);
}