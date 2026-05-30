package com.example.flora.Features.Bug.viewmodel;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Bug.service.BugService;
import com.example.flora.Features.Bug.service.BugService.ProjectSummary;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class BugViewModel {

    private final BugService service;
    private final String currentUserId;


    private final ObservableList<Bug> filteredBugs = FXCollections.observableArrayList();

    private final ObjectProperty<String> activeProject =
            new SimpleObjectProperty<>(null);


    private final ObjectProperty<BugSeverity> activeSeverity =
            new SimpleObjectProperty<>(null);

    private final ObjectProperty<BugStatus> activeStatus =
            new SimpleObjectProperty<>(null);


    private final StringProperty activeBugFilterString =
            new SimpleStringProperty("ALL");


    private final ObjectProperty<Bug> selectedBug =
            new SimpleObjectProperty<>(null);

    private final StringProperty activeSectionLabel =
            new SimpleStringProperty("All Bugs");

    private final IntegerProperty bugCount =
            new SimpleIntegerProperty(0);

    private final ObservableList<ProjectSummary> projectSummaries =
            FXCollections.observableArrayList();

    public BugViewModel(BugService service,String currentUserId) {
        this.service = service;
        this.currentUserId = currentUserId;
    }


    public void selectProject(String projectName) {
        activeProject.set(projectName);
        activeSectionLabel.set(projectName == null ? "All Bugs" : projectName + "  Bugs");
        applyFilters();
    }

    public void setSeverityFilter(BugSeverity severity) {
        activeSeverity.set(severity);
        applyFilters();
    }

    public void setStatusFilter(BugStatus status) {
        activeStatus.set(status);
        applyFilters();
    }

    public void selectBug(Bug bug) {
        selectedBug.set(bug);
    }

    public void closeDetail() {
        selectedBug.set(null);
    }

    public void claimBug(Bug bug) {
        service.claimBug(bug.getId(), currentUserId).ifPresent(updated -> {
            selectedBug.set(updated);
            refresh();
        });
    }

    public void markFixed(Bug bug) {
        service.markFixed(bug.getId(), currentUserId).ifPresent(updated -> {
            selectedBug.set(updated);
            refresh();
        });
    }

    public void toggleProgress(Bug bug) {
        service.toggleProgress(bug.getId(), currentUserId).ifPresent(updated -> {
            selectedBug.set(updated);
            refresh();
        });
    }

    public boolean assignBug(Bug bug, String assigneeUserId) {
        Optional<Bug> result = service.assignBug(bug.getId(), assigneeUserId, currentUserId);
        result.ifPresent(updated -> {
            selectedBug.set(updated);
            refresh();
        });
        return result.isPresent();
    }


    public void reportBug(String title, String reportedByUserId, BugSeverity severity, String projectName) {
        service.reportBug(title, reportedByUserId, severity, projectName);
        refresh();
    }


    public void updateBugStatus(Bug bug, BugStatus newStatus) {
        service.updateBugStatus(bug.getId(), newStatus);
        refresh();
    }


    public boolean isProjectLeader(String projectName) {
        return currentUserId.equals(service.getProjectLeader(projectName));
    }

    public boolean isCurrentUserFixer(Bug bug) {
        return !bug.isUnclaimed() && currentUserId.equals(bug.getFixingUserId());
    }


    public String getProjectLeader(String projectName) {
        String leader = service.getProjectLeader(projectName);
        return leader.isBlank() ? "—" : leader;
    }


    public ObservableList<Bug> filteredBugs() {
        return filteredBugs;
    }

    public ObservableList<ProjectSummary> projectSummaries() {
        return projectSummaries;
    }

    public ObjectProperty<Bug> selectedBugProperty() {
        return selectedBug;
    }

    public StringProperty activeSectionLabel() {
        return activeSectionLabel;
    }

    public IntegerProperty bugCountProperty() {
        return bugCount;
    }

    public ObjectProperty<String> activeProjectProperty() {
        return activeProject;
    }

    public ObjectProperty<BugSeverity> activeSeverityProperty() {
        return activeSeverity;
    }

    public ObjectProperty<BugStatus> activeStatusProperty() {
        return activeStatus;
    }


    public StringProperty activeBugFilterProperty() {
        return activeBugFilterString;
    }

    public void setActiveBugFilter(String filter) {
        activeBugFilterString.set(filter);
        activeStatus.set(switch (filter) {
            case "OPEN" -> BugStatus.OPEN;
            case "IN_PROGRESS" -> BugStatus.IN_PROGRESS;
            case "CLOSED" -> BugStatus.CLOSED;
            default -> null;  // "ALL" → no filter
        });
        applyFilters();
    }

    public List<Bug> getFilteredBugs() {
        return List.copyOf(filteredBugs);
    }

    public Bug getSelectedBug() {
        return selectedBug.get();
    }

    public String getActiveProject() {
        return activeProject.get();
    }

    public BugSeverity getActiveSeverity() {
        return activeSeverity.get();
    }

    public BugStatus getActiveStatus() {
        return activeStatus.get();
    }


    private void refresh() {
        Map<String, ProjectSummary> summaryMap = service.getProjectSummaries();
        projectSummaries.setAll(summaryMap.values());

        applyFilters();
    }

    private void applyFilters() {
        List<Bug> result = service.getFilteredBugs(
                activeProject.get(),
                activeSeverity.get(),
                activeStatus.get()
        );
        filteredBugs.setAll(result);
        bugCount.set(result.size());
    }
}