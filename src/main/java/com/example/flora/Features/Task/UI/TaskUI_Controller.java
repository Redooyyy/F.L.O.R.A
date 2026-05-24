package com.example.flora.Features.Task.UI;

import com.example.flora.Features.Home.UI.Cards.TaskNotifyController;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import com.example.flora.Features.Task.UI.Card.TaskCardUI_Controller;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TaskUI_Controller implements Initializable {


    private final TaskViewModel taskViewModel;
    private final HomeUI_Controller homeController;


    @FXML
    private ScrollPane proScroll;
    @FXML
    private VBox projectCardScroll;


    @FXML
    private Label projectNameInViewBox;
    @FXML
    private ScrollPane tasScroll;
    @FXML
    private VBox TaskCardScroll;


    @FXML
    private Button filterAll;
    @FXML
    private Button filterMy;
    @FXML
    private Button filterDue;
    @FXML
    private Button filterCompleted;
    @FXML
    private Button filterDrafts;


    @FXML
    private AnchorPane detailOverlay;   // the full StackPane layer
    @FXML
    private VBox detailPanel;     // the right-side panel VBox


    @FXML
    private Label detailTitle;
    @FXML
    private Label detailDescription;
    @FXML
    private Label detailStatus;
    @FXML
    private Label detailDueDate;
    @FXML
    private Label detailAssignee;
    @FXML
    private Label detailCreatedAt;
    @FXML
    private Pane detailAvatarDot;
    @FXML
    private Pane stepTodo;
    @FXML
    private Pane stepInProgress;
    @FXML
    private Pane stepInReview;
    @FXML
    private Pane stepDone;
    @FXML
    private Button cycleStatusBtn;
    @FXML
    private Button markDoneBtn;
    @FXML
    private Button deleteBtn;


    private Task selectedTask;


    public TaskUI_Controller(TaskViewModel taskViewModel, HomeUI_Controller homeController) {
        this.taskViewModel = taskViewModel;
        this.homeController = homeController;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBar(tasScroll);
        removeScrollBar(proScroll);

        if (taskViewModel.getCurrentProjectId() != null) {
            projectNameInViewBox.setText(taskViewModel.getCurrentProjectId());
        }

        taskViewModel.getTasks().addListener(
                (javafx.collections.ListChangeListener<Task>) c -> renderTaskCards());
        taskViewModel.activeFilterProperty().addListener(
                (obs, o, n) -> {
                    updateFilterButtons(n);
                    renderTaskCards();
                });

        loadProjectNotifyCards();
        updateFilterButtons(taskViewModel.activeFilterProperty().get());


        if (taskViewModel.getTasks().isEmpty()) {
            taskViewModel.getTasks().addAll(
                    new Task("1", "Design login screen", "Create wireframes and mockups", TaskStatus.TODO, "proj-1", "bushra", "27 May 2026", "20 May 2026"),
                    new Task("2", "Patient Registration", "Implement new patient intake form", TaskStatus.IN_PROGRESS, "proj-1", "karim", "30 May 2026", "18 May 2026"),
                    new Task("3", "Doctor Appointment API", "REST endpoints for scheduling", TaskStatus.IN_REVIEW, "proj-1", "amina", "02 Jun 2026", "15 May 2026"),
                    new Task("4", "Auth & Role Management", "Doctor / nurse / admin permission levels", TaskStatus.DONE, "proj-1", "reza", "15 May 2026", "01 May 2026"),
                    new Task("5", "Billing & Invoice Screen", "PDF generation for patient billing", TaskStatus.TODO, "proj-1", null, "10 Jun 2026", "21 May 2026")
            );
        }


        renderTaskCards();


        detailPanel.setTranslateX(380);
        detailOverlay.setVisible(false);
    }

    // ── Filter handlers ────────────────────────────────────────────────────────
    @FXML
    private void onFilterAll() {
        taskViewModel.setActiveFilter("ALL");
    }

    @FXML
    private void onFilterMy() {
        taskViewModel.setActiveFilter("MY");
    }

    @FXML
    private void onFilterDue() {
        taskViewModel.setActiveFilter("DUE");
    }

    @FXML
    private void onFilterCompleted() {
        taskViewModel.setActiveFilter("COMPLETED");
    }

    @FXML
    private void onFilterDrafts() {
        taskViewModel.setActiveFilter("DRAFTS");
    }

    @FXML
    private void onAddTaskClicked() {
        // TODO: open your Add Task dialog here
    }


    @FXML
    private void onScrimClicked() {
        closeDetailPanel();
    }


    public void onTaskSelected(Task task) {
        this.selectedTask = task;
        populateDetail(task);
        openDetailPanel();
    }


    public void onStatusToggled(Task task) {
        TaskStatus next = nextStatus(task.getStatus());
        taskViewModel.updateStatus(task, next);
        // If this task is currently open in the detail panel, refresh it
        if (selectedTask != null && selectedTask.getId().equals(task.getId())) {
            populateDetail(task);
        }
    }


    @FXML
    public void onMarkDone() {
        if (selectedTask == null) return;
        taskViewModel.markDone(selectedTask);
        selectedTask.setStatus(TaskStatus.DONE);
        populateDetail(selectedTask);
    }


    @FXML
    public void onDelete() {
        if (selectedTask == null) return;
        taskViewModel.deleteTask(selectedTask);
        selectedTask = null;
        closeDetailPanel();
    }


    @FXML
    public void onDetailClose() {
        closeDetailPanel();
    }


    @FXML
    public void onCycleStatus() {
        if (selectedTask == null) return;
        TaskStatus next = nextStatus(selectedTask.getStatus());
        taskViewModel.updateStatus(selectedTask, next);
        selectedTask.setStatus(next);
        populateDetail(selectedTask);
    }


    public void onDeleteTask(Task task) {
        taskViewModel.deleteTask(task);
    }

    public void closeDetailPanel() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(220), detailPanel);
        slide.setToX(380);
        slide.setOnFinished(e -> detailOverlay.setVisible(false));
        slide.play();
    }


    private void populateDetail(Task task) {
        detailTitle.setText(nvl(task.getTitle(), "Untitled"));
        detailDescription.setText(nvl(task.getDescription(), "No description provided."));
        detailDueDate.setText(nvl(task.getDueDate(), "No due date"));
        detailCreatedAt.setText(nvl(task.getCreatedAt(), "—"));

        boolean draft = task.isDraft();
        detailAssignee.setText(draft ? "Unassigned (Draft)" : task.getAssigneeId());
        detailAvatarDot.setStyle(draft
                ? "-fx-background-color:#3D3B55;-fx-background-radius:50%;"
                : "-fx-background-color:#7C6AF7;-fx-background-radius:50%;");

        applyDetailStatus(task.getStatus());
        markDoneBtn.setDisable(task.getStatus() == TaskStatus.DONE);
    }

    private void applyDetailStatus(TaskStatus status) {
        detailStatus.getStyleClass().removeAll(
                "status-todo", "status-progress", "status-late", "status-done");

        String inactive = "-fx-background-color:#2C2A40;-fx-background-radius:3px;";
        String active = "-fx-background-color:#7C6AF7;-fx-background-radius:3px;";
        String done = "-fx-background-color:#34D399;-fx-background-radius:3px;";

        stepTodo.setStyle(inactive);
        stepInProgress.setStyle(inactive);
        stepInReview.setStyle(inactive);
        stepDone.setStyle(inactive);

        if (status == null) status = TaskStatus.TODO;

        switch (status) {
            case TODO -> {
                detailStatus.setText("To Do");
                detailStatus.getStyleClass().add("status-todo");
                stepTodo.setStyle(active);
                cycleStatusBtn.setText("Start →");
            }
            case IN_PROGRESS -> {
                detailStatus.setText("In Progress");
                detailStatus.getStyleClass().add("status-progress");
                stepTodo.setStyle(done);
                stepInProgress.setStyle(active);
                cycleStatusBtn.setText("Send to Review →");
            }
            case IN_REVIEW -> {
                detailStatus.setText("In Review");
                detailStatus.getStyleClass().add("status-late");
                stepTodo.setStyle(done);
                stepInProgress.setStyle(done);
                stepInReview.setStyle(active);
                cycleStatusBtn.setText("Mark Done →");
            }
            case DONE -> {
                detailStatus.setText("Done");
                detailStatus.getStyleClass().add("status-done");
                stepTodo.setStyle(done);
                stepInProgress.setStyle(done);
                stepInReview.setStyle(done);
                stepDone.setStyle(done);
                cycleStatusBtn.setText("Reopen →");
            }
        }
    }


    private void renderTaskCards() {
        TaskCardScroll.getChildren().clear();
        List<Task> filtered = taskViewModel.getFilteredTasks();

        if (filtered.isEmpty()) {
            Label empty = new Label("No tasks here yet.");
            empty.getStyleClass().add("empty-state");
            TaskCardScroll.getChildren().add(empty);
            return;
        }

        for (Task task : filtered) {
            try {
                TaskCardScroll.getChildren().add(buildTaskCard(task));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load TaskCardUI.fxml", e);
            }
        }
    }

    private AnchorPane buildTaskCard(Task task) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Task/UI/Card/TaskCardUI.fxml"));
        loader.setControllerFactory(c -> new TaskCardUI_Controller(this));
        AnchorPane card = loader.load();
        loader.<TaskCardUI_Controller>getController().bind(task);
        return card;
    }

    private void loadProjectNotifyCards() {
        projectCardScroll.getChildren().clear();
        String[][] demo = {
                {"Hospital Management System", "3"},
                {"E-Commerce Platform", "5"},
                {"Flora Project Manager", "2"},
        };
        for (String[] p : demo) {
            try {
                loadProjectNotifyCard(p[0], p[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadProjectNotifyCard(String name, String count) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/Home/UI/Cards/taskNotify.fxml"));
        AnchorPane card = loader.load();
        loader.<TaskNotifyController>getController().setValue(name, count);
        projectCardScroll.getChildren().add(card);
    }


    private void openDetailPanel() {
        detailOverlay.setVisible(true);
        TranslateTransition slide = new TranslateTransition(Duration.millis(250), detailPanel);
        slide.setToX(0);
        slide.play();
    }

    private void updateFilterButtons(String active) {
        List<Button> btns = List.of(filterAll, filterMy, filterDue, filterCompleted, filterDrafts);
        List<String> keys = List.of("ALL", "MY", "DUE", "COMPLETED", "DRAFTS");
        for (int i = 0; i < btns.size(); i++) {
            Button b = btns.get(i);
            b.getStyleClass().removeAll("filter-btn-active", "filter-btn");
            b.getStyleClass().add(keys.get(i).equals(active) ? "filter-btn-active" : "filter-btn");
        }
    }

    private TaskStatus nextStatus(TaskStatus current) {
        return switch (current) {
            case TODO -> TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> TaskStatus.IN_REVIEW;
            case IN_REVIEW -> TaskStatus.DONE;
            case DONE -> TaskStatus.TODO;
        };
    }

    private void removeScrollBar(ScrollPane sp) {
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private String nvl(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }
}