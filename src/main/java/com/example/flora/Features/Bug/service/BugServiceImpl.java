package com.example.flora.Features.Bug.service;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Bug.repository.BugRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class BugServiceImpl implements BugService {

    private final BugRepository repository;

    public BugServiceImpl(BugRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<Bug> getAllBugs() {
        return repository.findAll();
    }

    @Override
    public List<Bug> getFilteredBugs(String projectName, BugSeverity severity, BugStatus status) {
        return repository.findFiltered(projectName, severity, status);
    }

    @Override
    public List<String> getDistinctProjectNames() {
        return repository.findDistinctProjectNames();
    }

    @Override
    public Map<String, ProjectSummary> getProjectSummaries() {
        List<Bug> all = repository.findAll();
        Map<String, ProjectSummary> map = new LinkedHashMap<>();

        // "All Projects" aggregate
        map.put(null, buildSummary(null, all));

        repository.findDistinctProjectNames().forEach(p ->
                map.put(p, buildSummary(p, repository.findByProject(p)))
        );
        return map;
    }

    @Override
    public String getProjectLeader(String projectName) {
        return repository.findProjectLeader(projectName);
    }


    @Override
    public Optional<Bug> claimBug(int bugId, String currentUserId) {
        return repository.findById(bugId)
                .filter(b -> !b.isClosed())
                .filter(Bug::isUnclaimed)
                .flatMap(b -> repository.assignFixer(bugId, currentUserId));
    }

    @Override
    public Optional<Bug> markFixed(int bugId, String currentUserId) {
        return repository.findById(bugId)
                .filter(b -> !b.isClosed())
                .filter(b -> currentUserId.equals(b.getFixingUserId()))
                .flatMap(b -> repository.updateStatus(bugId, BugStatus.CLOSED));
    }

    @Override
    public Optional<Bug> toggleProgress(int bugId, String currentUserId) {
        return repository.findById(bugId)
                .filter(b -> !b.isClosed())
                .filter(b -> currentUserId.equals(b.getFixingUserId()))
                .flatMap(b -> {
                    BugStatus next = b.getStatus() == BugStatus.IN_PROGRESS
                            ? BugStatus.OPEN : BugStatus.IN_PROGRESS;
                    return repository.updateStatus(bugId, next);
                });
    }

    @Override
    public Optional<Bug> assignBug(int bugId, String assigneeUserId, String leaderUserId) {
        return repository.findById(bugId)
                .filter(b -> !b.isClosed())
                .filter(b -> leaderUserId.equals(repository.findProjectLeader(b.getProjectName())))
                .flatMap(b -> repository.assignFixer(bugId, assigneeUserId));
    }

    @Override
    public void reportBug(String title, String reportedByUserId, BugSeverity severity, String projectName) {
        if (title.isBlank() || reportedByUserId.isBlank()) return;
        String today = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"));
        Bug bug = new Bug(
                0,
                projectName, title, "",
                severity, BugStatus.OPEN, null, reportedByUserId, today
        );
        repository.save(bug);
    }

    @Override
    public Optional<Bug> updateBugStatus(int bugId, BugStatus newStatus) {
        return repository.updateStatus(bugId, newStatus);
    }


    private ProjectSummary buildSummary(String projectName, List<Bug> bugs) {
        long open = bugs.stream().filter(b -> b.getStatus() != BugStatus.CLOSED).count();
        long crit = countOpenBySeverity(bugs, BugSeverity.CRITICAL);
        long high = countOpenBySeverity(bugs, BugSeverity.HIGH);
        long med  = countOpenBySeverity(bugs, BugSeverity.MEDIUM);
        long low  = countOpenBySeverity(bugs, BugSeverity.LOW);
        String leader = projectName == null ? "" : repository.findProjectLeader(projectName);
        return new ProjectSummary(projectName, leader, open, crit, high, med, low);
    }

    private long countOpenBySeverity(List<Bug> bugs, BugSeverity severity) {
        return bugs.stream()
                .filter(b -> b.getSeverity() == severity && b.getStatus() != BugStatus.CLOSED)
                .count();
    }
}