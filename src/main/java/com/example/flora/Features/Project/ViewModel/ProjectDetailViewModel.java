package com.example.flora.Features.Project.ViewModel;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectDetailViewModel {


    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private Project currentProject;
    private String  currentUserId;
    private boolean isLeader;


    private final ObservableList<Task> tasks = FXCollections.observableArrayList();


    private final ObservableList<Bug> bugs = FXCollections.observableArrayList();


    private final ObservableList<String> members = FXCollections.observableArrayList();


    private final StringProperty  activeTaskFilter = new SimpleStringProperty("ALL");
    private final StringProperty  activeBugFilter = new SimpleStringProperty("ALL");
    private final IntegerProperty taskCount = new SimpleIntegerProperty(0);
    private final IntegerProperty bugCount = new SimpleIntegerProperty(0);
    private final IntegerProperty memberCount = new SimpleIntegerProperty(0);


    private LocalDate pendingAssignDeadline = null;
    private LocalDate pendingDraftDeadline  = null;

    public void init(Project project, String currentUserId, boolean isLeader) {
        this.currentProject = project;
        this.currentUserId  = currentUserId;
        this.isLeader = isLeader;

        loadTasks();
        loadBugs();
        loadMembers();

        activeTaskFilter.set(isLeader ? "ALL" : "MY");
    }

    private void loadTasks() {
        tasks.clear();

        tasks.addAll(
                makeTask("1", "Design login screen","bushra", TaskStatus.DONE, LocalDate.now().minusDays(5)),
                makeTask("2", "Implement API endpoint","farhan", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(2)),
                makeTask("3", "Write unit tests", "reo", TaskStatus.TODO, LocalDate.now().plusDays(7)),
                makeTask("4", "Set up CI/CD pipeline", "farhan", TaskStatus.TODO, LocalDate.now().plusDays(14)),
                makeTask("5", "Code review – auth module","bushra", TaskStatus.IN_REVIEW, LocalDate.now().plusDays(1)),
                // Drafts — assigneeId null, status TODO
                makeTask("6", "Write API documentation",null, TaskStatus.TODO, LocalDate.now().plusDays(10)),
                makeTask("7", "Design onboarding flow", null, TaskStatus.TODO, null)
        );

        taskCount.set(tasks.size());
    }

    public void assignTask(String title, String assigneeId, LocalDate deadline) {
        if (title.isBlank() || assigneeId.isBlank()) return;

        Task t = new Task(
                generateId(), title, null,
                TaskStatus.TODO,
                currentProject.getId(),
                assigneeId,
                deadline != null ? deadline.format(DATE_FMT) : null,
                LocalDate.now().format(DATE_FMT)
        );
        // TODO: taskService.save(t); then set t.setId(returned id)
        tasks.add(t);
        taskCount.set(tasks.size());
        pendingAssignDeadline = null;
    }

    public void saveDraftTask(String title, LocalDate deadline) {
        if (title.isBlank()) return;

        Task t = new Task(
                generateId(), title, null,
                TaskStatus.TODO,
                currentProject.getId(),
                null,   // no assignee → draft
                deadline != null ? deadline.format(DATE_FMT) : null,
                LocalDate.now().format(DATE_FMT)
        );
        // TODO: taskService.saveDraft(t);
        tasks.add(t);
        taskCount.set(tasks.size());
        pendingDraftDeadline = null;
    }

    public void markTaskDone(Task task) {
        task.setStatus(TaskStatus.DONE);
        // TODO: taskService.updateStatus(task.getId(), TaskStatus.DONE);
        refreshTaskList(); // notify observers
    }

    public void updateTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
        // TODO: taskService.updateStatus(task.getId(), newStatus);
        refreshTaskList();
    }

    public void reassignTask(Task task, String newAssigneeId) {
        if (newAssigneeId.isBlank()) return;
        task.setAssigneeId(newAssigneeId);
        // TODO: taskService.reassign(task.getId(), newAssigneeId);
        refreshTaskList();
    }

    public void updateTaskDeadline(Task task, LocalDate newDeadline) {
        task.setDueDate(newDeadline != null ? newDeadline.format(DATE_FMT) : null);
        // TODO: taskService.updateDeadline(task.getId(), newDeadline);
        refreshTaskList();
    }


    public List<Task> getFilteredTasks() {
        return switch (activeTaskFilter.get()) {
            case "MY" -> tasks.stream()
                    .filter(t -> !isDraft(t) && currentUserId.equalsIgnoreCase(t.getAssigneeId()))
                    .collect(Collectors.toList());
            case "COMPLETED" -> tasks.stream()
                    .filter(t -> !isDraft(t) && t.getStatus() == TaskStatus.DONE)
                    .collect(Collectors.toList());
            case "DUE" -> tasks.stream()
                    .filter(t -> !isDraft(t) && t.getStatus() != TaskStatus.DONE)
                    .collect(Collectors.toList());
            case "DRAFTS" -> tasks.stream()
                    .filter(this::isDraft)
                    .collect(Collectors.toList());
            default -> tasks.stream()   // ALL
                    .filter(t -> !isDraft(t))
                    .collect(Collectors.toList());
        };
    }

    public boolean isDraft(Task task) {
        return task.getAssigneeId() == null || task.getAssigneeId().isBlank();
    }


    private void loadBugs() {
        bugs.clear();

        bugs.addAll(
                new Bug("b1", currentProject.getId(), "Login page crashes on empty password", BugStatus.OPEN,null),
                new Bug("b2", currentProject.getId(), "Dashboard flickers on resize", BugStatus.IN_PROGRESS,"farhan"),
                new Bug("b3", currentProject.getId(), "API returns 500 on invalid token", BugStatus.OPEN,null)
        );

        bugCount.set(bugs.size());
    }

    public void reportBug(String title) {
        if (title.isBlank()) return;
        Bug bug = Bug.open(currentProject.getId(), title);
        // TODO: bugService.report(bug); then bug.setId(returnedId)
        bug.setId(generateId());
        bugs.add(bug);
        bugCount.set(bugs.size());
    }

    public void claimBug(Bug bug) {
        bug.setFixingUserId(currentUserId);
        bug.setStatus(BugStatus.IN_PROGRESS);
        // TODO: bugService.claim(bug.getId(), currentUserId);
        refreshBugList();
    }

    public void assignBug(Bug bug, String userId) {
        if (userId.isBlank()) return;
        bug.setFixingUserId(userId);
        bug.setStatus(BugStatus.IN_PROGRESS);
        // TODO: bugService.assign(bug.getId(), userId);
        refreshBugList();
    }

    public void markBugFixed(Bug bug) {
        bug.setStatus(BugStatus.CLOSED);
        // TODO: bugService.markFixed(bug.getId());
        refreshBugList();
        bugCount.set((int) bugs.stream().filter(b -> !b.isClosed()).count());
    }

    public List<Bug> getFilteredBugs() {
        return switch (activeBugFilter.get()) {
            case "OPEN"   -> bugs.stream().filter(b -> !b.isClosed()).collect(Collectors.toList());
            case "CLOSED" -> bugs.stream().filter(Bug::isClosed).collect(Collectors.toList());
            default       -> bugs.stream().collect(Collectors.toList()); // ALL
        };
    }


    private void loadMembers() {
        members.clear();
        // TODO: members.setAll(projectService.getMembers(currentProject.getId()));
        members.addAll("bushra", "farhan", "reo");  // DEMO
        memberCount.set(members.size());
    }

    public boolean sendInvite(String username) {
        if (username.isBlank()) return false;
        // TODO: invitationService.sendInvite(currentProject.getId(), username);
        return true; // returns false if service reports failure
    }

    public void removeMember(String username) {
        members.remove(username);
        // TODO: projectService.removeMember(currentProject.getId(), username);
        memberCount.set(members.size());
    }


    public void setPendingAssignDeadline(LocalDate date) {
        pendingAssignDeadline = date;
    }
    public void setPendingDraftDeadline(LocalDate date)  {
        pendingDraftDeadline  = date;
    }
    public LocalDate getPendingAssignDeadline() {
        return pendingAssignDeadline;
    }
    public LocalDate getPendingDraftDeadline() {
        return pendingDraftDeadline;
    }
    public void clearPendingAssignDeadline() {
        pendingAssignDeadline = null;
    }
    public void clearPendingDraftDeadline() {
        pendingDraftDeadline  = null;
    }


    public Project getCurrentProject(){
        return currentProject;
    }
    public String getCurrentUserId(){
        return currentUserId;
    }
    public boolean isLeader(){
        return isLeader;
    }
    public ObservableList<Task> getTasks(){
        return tasks;
    }
    public ObservableList<Bug> getBugs(){
        return bugs;
    }
    public ObservableList<String> getMembers(){
        return members;
    }

    public StringProperty  activeTaskFilterProperty(){
        return activeTaskFilter;
    }
    public StringProperty  activeBugFilterProperty() {
        return activeBugFilter;
    }
    public IntegerProperty taskCountProperty(){
        return taskCount;
    }
    public IntegerProperty bugCountProperty(){
        return bugCount;
    }
    public IntegerProperty memberCountProperty(){
        return memberCount;
    }

    public void setActiveTaskFilter(String filter){
        activeTaskFilter.set(filter);
    }
    public void setActiveBugFilter(String filter){
        activeBugFilter.set(filter);
    }



    private void refreshTaskList() {
        List<Task> snapshot = List.copyOf(tasks);
        tasks.setAll(snapshot);
    }

    private void refreshBugList() {
        List<Bug> snapshot = List.copyOf(bugs);
        bugs.setAll(snapshot);
    }

    private Task makeTask(String id, String title, String assigneeId,TaskStatus status, LocalDate deadline) {
        return new Task(
                id, title, null, status,
                currentProject.getId(),
                assigneeId,
                deadline != null ? deadline.format(DATE_FMT) : null,
                LocalDate.now().format(DATE_FMT)
        );
    }

    private String generateId() {
        return "local-" + System.currentTimeMillis();
    }
}