package com.example.flora.Features.Project.ViewModel;

import com.example.flora.Features.Auth.ViewModel.AuthViewModel;
import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Bug.viewmodel.BugViewModel;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProjectDetailViewModel {

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TaskViewModel taskViewModel;
    private final BugViewModel bugViewModel;   // ← injected, no longer self-owned
    private final AuthViewModel authViewModel;
    private final ProjectViewModel projectViewModel;

    private Project currentProject;
    private String currentUserId;
    private boolean isLeader;

    private final ObservableList<String> members = FXCollections.observableArrayList();

    private final IntegerProperty memberCount = new SimpleIntegerProperty(0);

    public ProjectDetailViewModel(ProjectViewModel projectViewModel,TaskViewModel taskViewModel, BugViewModel bugViewModel,AuthViewModel authViewModel,String currentUserId) {
        this.projectViewModel = projectViewModel;
        this.taskViewModel = taskViewModel;
        this.bugViewModel = bugViewModel;
        this.currentUserId = currentUserId;
        this.authViewModel = authViewModel;
    }

    public void init(Project project,boolean isLeader) {
        this.currentProject = project;
        this.isLeader = isLeader;

        taskViewModel.init(project.getId(), isLeader);

        bugViewModel.selectProject(project.getName());

        loadMembers();
    }


    public TaskViewModel getTaskViewModel() {
        return taskViewModel;
    }

    public List<String> searchUsers(String query) {
        if (query == null || query.isBlank()) return List.of();
        return authViewModel.searchUsers(query)
                .stream()
                .filter(u -> !members.contains(u))
                .toList();
    }

    public void assignTask(String title, String assigneeId, LocalDate deadline) {
        taskViewModel.assignTask(title, assigneeId, deadline);
    }

    public void saveDraftTask(String title, LocalDate deadline) {
        taskViewModel.saveDraftTask(title, deadline);
    }

    public void markTaskDone(Task task) {
        taskViewModel.markDone(task);
    }

    public void updateTaskStatus(Task task, TaskStatus newStatus) {
        taskViewModel.updateStatus(task, newStatus);
    }

    public void reassignTask(Task task, String newAssigneeId) {
        taskViewModel.reassign(task, newAssigneeId);
    }

    public void updateTaskDeadline(Task task, LocalDate newDeadline) {
        taskViewModel.updateDeadline(task, newDeadline);
    }

    public void deleteTask(Task task) {
        taskViewModel.deleteTask(task);
    }

    public List<Task> getFilteredTasks() {
        return taskViewModel.getFilteredTasks();
    }

    public ObservableList<Task> getTasks() {
        return taskViewModel.getTasks();
    }

    public StringProperty activeTaskFilterProperty() {
        return taskViewModel.activeFilterProperty();
    }

    public void setActiveTaskFilter(String filter) {
        taskViewModel.setActiveFilter(filter);
    }

    public void setPendingAssignDeadline(LocalDate date) {
        taskViewModel.setPendingAssignDeadline(date);
    }

    public void setPendingDraftDeadline(LocalDate date) {
        taskViewModel.setPendingDraftDeadline(date);
    }

    public LocalDate getPendingAssignDeadline() {
        return taskViewModel.getPendingAssignDeadline();
    }

    public LocalDate getPendingDraftDeadline() {
        return taskViewModel.getPendingDraftDeadline();
    }

    public void clearPendingAssignDeadline() {
        taskViewModel.clearPendingAssignDeadline();
    }

    public void clearPendingDraftDeadline() {
        taskViewModel.clearPendingDraftDeadline();
    }

    public boolean isDraft(Task task) {
        return task.getAssigneeId() == null || task.getAssigneeId().isBlank();
    }


    public BugViewModel getBugViewModel() {
        return bugViewModel;
    }

    public void claimBug(Bug bug) {
        bugViewModel.claimBug(bug);
    }

    public boolean assignBug(Bug bug, String userId) {
        return bugViewModel.assignBug(bug, userId);
    }

    public void markBugFixed(Bug bug) {
        bugViewModel.markFixed(bug);
    }

    public void reportBug(String title, String reportedByUserId, BugSeverity severity) {
        bugViewModel.reportBug(title, reportedByUserId, severity, currentProject.getName());
    }

    public void updateBugStatus(Bug bug, BugStatus newStatus) {
        bugViewModel.updateBugStatus(bug, newStatus);
    }

    public List<Bug> getFilteredBugs() {
        return bugViewModel.getFilteredBugs();
    }

    public ObservableList<Bug> getBugs() {
        return bugViewModel.filteredBugs();
    }

    public StringProperty activeBugFilterProperty() {
        return bugViewModel.activeBugFilterProperty();
    }

    public void setActiveBugFilter(String filter) {
        bugViewModel.setActiveBugFilter(filter);
    }

    public IntegerProperty bugCountProperty() {
        IntegerProperty prop = new SimpleIntegerProperty();
        prop.bind(Bindings.size(bugViewModel.filteredBugs()));
        return prop;
    }


    private void loadMembers() {
        members.clear();
        members.addAll(projectViewModel.getMembers(currentProject.getId()));
        memberCount.set(members.size());
    }

    public boolean sendInvite(String username) {
        if (username.isBlank()) return false;
        projectViewModel.addMembers(currentProject.getId(),authViewModel.findByUserName(username).getId().toString());
        return true;
    }

    public void removeMember(String username) {
        members.remove(username);
        memberCount.set(members.size());
    }


    public Project getCurrentProject() {
        return currentProject;
    }

    public void updateCurrentProjectStatus(String status) {
        if (currentProject != null) {
            currentProject.setStatus(status);
            projectViewModel.updateProject(currentProject);
        }
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public ObservableList<String> getMembers() {
        return members;
    }

    public IntegerProperty memberCountProperty() {
        return memberCount;
    }

    public IntegerProperty taskCountProperty() {
        IntegerProperty prop = new SimpleIntegerProperty();
        prop.bind(Bindings.size(taskViewModel.getTasks()));
        return prop;
    }

    public ObservableList<String>passInTaskView(){
        loadMembers();
        return getMembers();
    }

    public String getLeaderUsername() {
        return getUsernameById(currentProject.getOwnerId());
    }

    public String getUsernameById(String userId) {
        try {
            return authViewModel.findByUserID(Integer.parseInt(userId)).getUsername();
        } catch (Exception e) {
            return userId; // Fallback to ID if not found
        }
    }
}