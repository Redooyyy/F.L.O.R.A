package com.example.flora.Features.Task.UI;

import com.example.flora.Features.Home.UI.HomeUI_Controller;
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Task.UI.Card.TaskCardUI_Controller;
import com.example.flora.Features.Task.UI.Detail.TaskDetailUI_Controller;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import com.example.flora.Core.Helper.UI_Helper.Builder;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TaskUI_Controller implements Initializable {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final TaskViewModel taskViewModel;
    private final ProjectViewModel projectViewModel;
    private final HomeUI_Controller homeController;

    @FXML
    private ScrollPane proScroll;
    @FXML
    private VBox projectCardScroll;

    @FXML
    private Label projectNameInViewBox;
    @FXML
    private Label roleBadge;
    @FXML
    private Button addTaskBtn;

    @FXML
    private VBox addTaskPanel;
    @FXML
    private Button modeAssignBtn;
    @FXML
    private Button modeDraftBtn;
    @FXML
    private HBox assignFields;
    @FXML
    private HBox draftFields;
    @FXML
    private TextField taskTitleInput;
    @FXML
    private TextField taskAssigneeInput;
    @FXML
    private Label assignDeadlineChip;
    @FXML
    private TextField draftTitleInput;
    @FXML
    private Label draftDeadlineChip;

    @FXML
    private HBox filterBar;
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
    private Label taskCountBadge;

    @FXML
    private ScrollPane tasScroll;
    @FXML
    private VBox TaskCardScroll;

    @FXML
    private AnchorPane detailOverlay;
    @FXML
    private AnchorPane detailContainer;

    private boolean addPanelOpen = false;
    private Project activeProject;
    private TaskDetailUI_Controller detailController;

    public TaskUI_Controller(TaskViewModel taskViewModel,
                             ProjectViewModel projectViewModel,
                             HomeUI_Controller homeController) {
        this.taskViewModel = taskViewModel;
        this.projectViewModel = projectViewModel;
        this.homeController = homeController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        removeScrollBar(tasScroll);
        removeScrollBar(proScroll);

        loadProjectSidebar();

        loadDetailPanel();
        detailOverlay.setVisible(false);

        addTaskPanel.setVisible(false);
        addTaskPanel.setManaged(false);

        taskViewModel.getTasks().addListener(
                (javafx.collections.ListChangeListener<Task>) c -> renderTaskRows());
        taskViewModel.activeFilterProperty().addListener(
                (obs, o, n) -> {
                    updateFilterButtons(n);
                    renderTaskRows();
                });

        taskAssigneeInput.sceneProperty().addListener((obs, o, n) -> {
            if (n != null) new Builder(taskAssigneeInput, (AnchorPane) taskAssigneeInput.getParent().getParent().getParent())
                    .suggestions(q -> taskViewModel.getMembers().stream()
                            .filter(m -> q.isBlank() || m.toLowerCase().contains(q.toLowerCase()))
                            .toList())
                    .showOnFocus(true)
                    .onSelect((val, f) -> { f.setText(val); f.positionCaret(val.length()); })
                    .build()
                    .attach();
        });

        List<Project> all = projectViewModel.getProjects();
        if (!all.isEmpty()) {
            selectProject(all.get(0));
        } else {
            projectNameInViewBox.setText("No Projects");
            applyRoleUI(false);
        }
    }

    private void loadDetailPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Task/UI/Detail/TaskDetailUI.fxml"));
            loader.setControllerFactory(c -> new TaskDetailUI_Controller(this, taskViewModel));
            Node panel = loader.load();
            detailController = loader.getController();
            detailContainer.getChildren().setAll(panel);
            AnchorPane.setTopAnchor(panel, 0.0);
            AnchorPane.setBottomAnchor(panel, 0.0);
            AnchorPane.setLeftAnchor(panel, 0.0);
            AnchorPane.setRightAnchor(panel, 0.0);
            panel.setTranslateX(370);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TaskDetailUI.fxml", e);
        }
    }

    private void loadProjectSidebar() {
        projectCardScroll.getChildren().clear();
        List<Project> projects = projectViewModel.getProjects();

        if (projects.isEmpty()) {
            Label empty = new Label("No projects yet.");
            empty.setStyle("-fx-text-fill:#4A475E; -fx-font-size:12px;");
            empty.setPadding(new Insets(14, 0, 0, 4));
            projectCardScroll.getChildren().add(empty);
            return;
        }

        for (Project p : projects) {
            projectCardScroll.getChildren().add(buildSidebarCard(p));
        }
    }

    private VBox buildSidebarCard(Project project) {
        boolean leader = projectViewModel.isLeaderOf(project);

        VBox card = new VBox(4);
        card.getStyleClass().add("notify-card");
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setCursor(javafx.scene.Cursor.HAND);

        HBox inner = new HBox(8);
        inner.setAlignment(Pos.CENTER_LEFT);

        Pane bar = new Pane();
        bar.setPrefSize(3, 32);
        bar.setStyle((leader
                ? "-fx-background-color:#7C6AF7;"
                : "-fx-background-color:#3D3B55;")
                + "-fx-background-radius:2px;");

        VBox text = new VBox(2);
        Label nameLbl = new Label(project.getName());
        nameLbl.getStyleClass().add("notify-card-title");
        nameLbl.setWrapText(true);

        Label roleLbl = new Label(leader ? "LEADER" : "MEMBER");
        roleLbl.setStyle(leader
                ? "-fx-font-size:9px; -fx-font-family:'System Bold'; -fx-text-fill:#7C6AF7;"
                : "-fx-font-size:9px; -fx-font-family:'System Bold'; -fx-text-fill:#3D3B55;");

        text.getChildren().addAll(nameLbl, roleLbl);
        inner.getChildren().addAll(bar, text);
        card.getChildren().add(inner);
        card.setOnMouseClicked(e -> selectProject(project));
        return card;
    }

    private void selectProject(Project project) {
        activeProject = project;
        boolean leader = projectViewModel.isLeaderOf(project);
        projectNameInViewBox.setText(project.getName());
        taskViewModel.init(project.getId(), leader);
        applyRoleUI(leader);
        closeDetailPanel();
        highlightSidebarCard(project);
    }

    private void highlightSidebarCard(Project active) {
        for (Node node : projectCardScroll.getChildren()) {
            if (!(node instanceof VBox card)) continue;
            boolean isActive = false;
            try {
                HBox inner = (HBox) card.getChildren().get(0);
                VBox text = (VBox) inner.getChildren().get(1);
                Label lbl = (Label) text.getChildren().get(0);
                isActive = lbl.getText().equals(active.getName());
            } catch (Exception ignored) {
            }
            card.setStyle(isActive
                    ? "-fx-background-color:#252340; -fx-background-radius:12; -fx-border-color:#7C6AF744; -fx-border-radius:12; -fx-border-width:1;"
                    : "");
        }
    }

    private void applyRoleUI(boolean leader) {
        addTaskBtn.setVisible(leader);
        addTaskBtn.setManaged(leader);

        roleBadge.setVisible(true);
        roleBadge.setManaged(true);
        roleBadge.setText(leader ? "LEADER" : "MEMBER");
        roleBadge.setStyle(leader
                ? "-fx-background-color:#241E3A; -fx-text-fill:#7C6AF7; -fx-font-size:9px; -fx-font-family:'System Bold'; -fx-background-radius:20; -fx-padding:4 10 4 10; -fx-border-color:#7C6AF755; -fx-border-radius:20; -fx-border-width:1;"
                : "-fx-background-color:#1E1C2E; -fx-text-fill:#4A475E; -fx-font-size:9px; -fx-font-family:'System Bold'; -fx-background-radius:20; -fx-padding:4 10 4 10;");

        filterDrafts.setVisible(leader);
        filterDrafts.setManaged(leader);

        if (!leader) {
            if (addPanelOpen) collapseAddPanel();
            if ("DRAFTS".equals(taskViewModel.activeFilterProperty().get()))
                taskViewModel.setActiveFilter("MY");
        }

        if (detailController != null)
            detailController.setLeader(leader);

        updateFilterButtons(taskViewModel.activeFilterProperty().get());
    }

    @FXML
    private void onAddTaskClicked() {
        if (!taskViewModel.isLeader()) return;
        addPanelOpen = !addPanelOpen;
        addTaskPanel.setVisible(addPanelOpen);
        addTaskPanel.setManaged(addPanelOpen);
        addTaskBtn.setText(addPanelOpen ? "✕  Cancel" : "＋ New Task");

        double filterTop = addPanelOpen ? 200.0 : 61.0;
        double scrollTop = addPanelOpen ? 240.0 : 108.0;
        AnchorPane.setTopAnchor(filterBar, filterTop);
        AnchorPane.setTopAnchor(tasScroll, scrollTop);

        if (addPanelOpen) {
            FadeTransition ft = new FadeTransition(Duration.millis(180), addTaskPanel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            switchToAssignMode(null);
        }
    }

    private void collapseAddPanel() {
        addPanelOpen = false;
        addTaskPanel.setVisible(false);
        addTaskPanel.setManaged(false);
        addTaskBtn.setText("＋  New Task");
        AnchorPane.setTopAnchor(filterBar, 61.0);
        AnchorPane.setTopAnchor(tasScroll, 108.0);
    }

    @FXML
    private void switchToAssignMode(ActionEvent e) {
        assignFields.setVisible(true);
        assignFields.setManaged(true);
        draftFields.setVisible(false);
        draftFields.setManaged(false);
        setModeActive(modeAssignBtn, modeDraftBtn);
    }

    @FXML
    private void switchToDraftMode(ActionEvent e) {
        assignFields.setVisible(false);
        assignFields.setManaged(false);
        draftFields.setVisible(true);
        draftFields.setManaged(true);
        setModeActive(modeDraftBtn, modeAssignBtn);
    }

    private void setModeActive(Button active, Button inactive) {
        active.getStyleClass().removeAll("mode-pill", "mode-pill-active");
        active.getStyleClass().add("mode-pill-active");
        inactive.getStyleClass().removeAll("mode-pill", "mode-pill-active");
        inactive.getStyleClass().add("mode-pill");
    }

    @FXML
    private void toggleAssignDeadline(MouseEvent e) {
        toggleDeadlineRow("assign-dl-row", assignFields, assignDeadlineChip, true);
    }

    @FXML
    private void toggleDraftDeadline(MouseEvent e) {
        toggleDeadlineRow("draft-dl-row", draftFields, draftDeadlineChip, false);
    }

    private void toggleDeadlineRow(String id, HBox anchor, Label chip, boolean isAssign) {
        int idx = addTaskPanel.getChildren().indexOf(anchor);
        boolean exists = addTaskPanel.getChildren().stream()
                .anyMatch(n -> id.equals(n.getUserData()));
        if (exists) {
            addTaskPanel.getChildren().removeIf(n -> id.equals(n.getUserData()));
            chip.setText("📅  Set deadline");
            if (isAssign) taskViewModel.clearPendingAssignDeadline();
            else taskViewModel.clearPendingDraftDeadline();
        } else {
            addTaskPanel.getChildren().add(idx + 1, buildDateRow(id, chip, isAssign));
        }
    }

    private HBox buildDateRow(String id, Label chip, boolean isAssign) {
        HBox row = new HBox(6);
        row.setUserData(id);
        row.setAlignment(Pos.CENTER_LEFT);

        DatePicker dp = new DatePicker();
        dp.setPromptText("Pick a date...");
        dp.setPrefWidth(168);
        dp.setPrefHeight(32);
        dp.getStyleClass().add("date-picker");
        dp.setStyle("-fx-font-size:12px;");
        dp.setConverter(dateConverter());

        Button ok = new Button("✔  Set");
        ok.getStyleClass().add("add-task-submit-btn");
        ok.setPadding(new Insets(4, 12, 4, 12));
        ok.setOnAction(ev -> {
            LocalDate chosen = dp.getValue();
            if (isAssign) taskViewModel.setPendingAssignDeadline(chosen);
            else taskViewModel.setPendingDraftDeadline(chosen);
            chip.setText(chosen != null ? "📅  " + chosen.format(DATE_FMT) : "📅  Set deadline");
            addTaskPanel.getChildren().removeIf(n -> id.equals(n.getUserData()));
        });

        row.getChildren().addAll(
                nudge("−7d", dp, -7), nudge("−1d", dp, -1),
                dp,
                nudge("+1d", dp, 1), nudge("+7d", dp, 7),
                ok);
        return row;
    }

    private Button nudge(String label, DatePicker dp, int days) {
        Button b = new Button(label);
        b.getStyleClass().add("nudge-btn");
        b.setPadding(new Insets(3, 7, 3, 7));
        b.setOnAction(e -> {
            LocalDate base = dp.getValue() != null ? dp.getValue() : LocalDate.now();
            dp.setValue(base.plusDays(days));
        });
        return b;
    }

    @FXML
    private void onAssignTask(ActionEvent e) {
        if (!taskViewModel.isLeader()) return;
        taskViewModel.assignTask(
                taskTitleInput.getText().trim(),
                taskAssigneeInput.getText().trim(),
                taskViewModel.getPendingAssignDeadline());
        taskTitleInput.clear();
        taskAssigneeInput.clear();
        assignDeadlineChip.setText("📅  Set deadline");
        addTaskPanel.getChildren().removeIf(n -> "assign-dl-row".equals(n.getUserData()));
    }

    @FXML
    private void onSaveDraftTask(ActionEvent e) {
        if (!taskViewModel.isLeader()) return;
        taskViewModel.saveDraftTask(
                draftTitleInput.getText().trim(),
                taskViewModel.getPendingDraftDeadline());
        draftTitleInput.clear();
        draftDeadlineChip.setText("📅  Set deadline");
        addTaskPanel.getChildren().removeIf(n -> "draft-dl-row".equals(n.getUserData()));
    }

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
        if (taskViewModel.isLeader()) taskViewModel.setActiveFilter("DRAFTS");
    }

    private void renderTaskRows() {
        TaskCardScroll.getChildren().clear();
        List<Task> filtered = taskViewModel.getFilteredTasks();

        if (taskCountBadge != null)
            taskCountBadge.setText(filtered.size() + (filtered.size() == 1 ? " task" : " tasks"));

        if (filtered.isEmpty()) {
            Label empty = new Label("No tasks here yet.");
            empty.setStyle("-fx-text-fill:#3D3B55; -fx-font-size:13px;");
            empty.setPadding(new Insets(40, 28, 0, 28));
            TaskCardScroll.getChildren().add(empty);
            return;
        }

        for (Task task : filtered) {
            TaskCardScroll.getChildren().add(buildTaskCard(task));
        }
    }

    private Node buildTaskCard(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/Task/UI/Card/TaskCardUI.fxml"));
            loader.setControllerFactory(c -> new TaskCardUI_Controller(this, taskViewModel));
            Node card = loader.load();
            TaskCardUI_Controller ctrl = loader.getController();
            ctrl.bind(task);
            return card;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load TaskCardUI.fxml", e);
        }
    }

    public void onTaskSelected(Task task) {
        if (detailController == null) return;
        detailController.bind(task);
        openDetailPanel();
    }

    public void onStatusToggled(Task task) {
        TaskStatus next = nextStatus(task.getStatus());
        taskViewModel.updateStatus(task, next);
    }

    public void onDeleteTask(Task task) {
        taskViewModel.deleteTask(task);
    }

    public void onDeadlineChanged(Task task, LocalDate newDate) {
        taskViewModel.updateDeadline(task, newDate);
        renderTaskRows();
    }

    public void onReassigned(Task task, String newAssigneeId) {
        taskViewModel.reassign(task, newAssigneeId);
        renderTaskRows();
    }

    public void openAddTaskPanel() {
        if (!taskViewModel.isLeader()) return;
        if (!addPanelOpen) onAddTaskClicked();
    }

    public void closeDetailPanel() {
        if (detailContainer.getChildren().isEmpty()) return;
        Node panel = detailContainer.getChildren().get(0);
        TranslateTransition slide = new TranslateTransition(Duration.millis(220), panel);
        slide.setToX(370);
        slide.setOnFinished(e -> detailOverlay.setVisible(false));
        slide.play();
    }

    private void openDetailPanel() {
        detailOverlay.setVisible(true);
        Node panel = detailContainer.getChildren().get(0);
        TranslateTransition slide = new TranslateTransition(Duration.millis(250), panel);
        slide.setToX(0);
        slide.play();
    }

    private void updateFilterButtons(String active) {
        List<Button> btns = List.of(filterAll, filterMy, filterDue, filterCompleted, filterDrafts);
        List<String> keys = List.of("ALL", "MY", "DUE", "COMPLETED", "DRAFTS");
        for (int i = 0; i < btns.size(); i++) {
            Button b = btns.get(i);
            b.getStyleClass().removeAll("filter-tab-active", "filter-tab");
            b.getStyleClass().add(keys.get(i).equals(active) ? "filter-tab-active" : "filter-tab");
        }
    }

    private StringConverter<LocalDate> dateConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate d) {
                return d != null ? d.format(DATE_FMT) : "";
            }

            @Override
            public LocalDate fromString(String s) {
                try {
                    return LocalDate.parse(s, DATE_FMT);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    @FXML
    private void onScrimClicked() {
        closeDetailPanel();
    }

    private TaskStatus nextStatus(TaskStatus s) {
        return switch (s) {
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
}