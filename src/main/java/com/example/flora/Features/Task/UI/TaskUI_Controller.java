package com.example.flora.Features.Task.UI;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Home.UI.HomeUI_Controller;
import com.example.flora.Features.Project.ViewModel.ProjectViewModel;
import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Task.UI.Detail.TaskDetailUI_Controller;
import com.example.flora.Features.Task.ViewModel.TaskViewModel;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.paint.Color;
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
            loader.setControllerFactory(c -> new TaskDetailUI_Controller(this));
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
            // peek at the name label inside inner HBox > VBox
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
        // Add task button
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
        dp.getStyleClass().add("date-picker");  // Date picker
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
            TaskCardScroll.getChildren().add(buildTaskRow(task));
        }
    }


    private VBox buildTaskRow(Task task) {
        boolean draft = task.isDraft();
        boolean isLeader = taskViewModel.isLeader();
        boolean isAssignee = !draft
                && task.getAssigneeId() != null
                && task.getAssigneeId().equalsIgnoreCase(taskViewModel.getCurrentUserId());


        VBox row = new VBox(0);
        row.setPadding(new Insets(0, 28, 0, 0));
        row.setStyle("-fx-cursor: default;");


        HBox body = new HBox(0);
        body.setAlignment(Pos.TOP_CENTER);


        Pane accentBar = new Pane();
        accentBar.setPrefWidth(4);
        accentBar.setMinHeight(56);
        String accentColor = draft ? "#FBB024" : switch (task.getStatus()) {
            case TODO -> "#3D3B55";
            case IN_PROGRESS -> "#7C6AF7";
            case IN_REVIEW -> "#F5A623";
            case DONE -> "#34D399";
        };
        accentBar.setStyle("-fx-background-color:" + accentColor + ";");
        HBox.setHgrow(accentBar, Priority.NEVER);


        VBox content = new VBox(0);
        HBox.setHgrow(content, Priority.ALWAYS);
        content.setStyle("-fx-background-color:#1A1829;");


        HBox titleRow = new HBox(10);
        titleRow.setPadding(new Insets(12, 16, 4, 16));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(draft ? "✏  " + task.getTitle() : task.getTitle());
        titleLbl.setTextFill(Color.web(draft ? "#FBB024" : "#EDE9F6"));
        titleLbl.setStyle("-fx-font-size:13px; -fx-font-family:'System Bold';");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);
        titleRow.getChildren().add(titleLbl);

        if (!draft && task.getCreatedAt() != null && !task.getCreatedAt().isBlank()) {
            Label createdChip = new Label("🗓 " + task.getCreatedAt());
            createdChip.setStyle("-fx-text-fill:#3D3B55; -fx-font-size:10px; -fx-background-color:#14121E; -fx-background-radius:8; -fx-padding:3 8 3 8;");
            titleRow.getChildren().add(createdChip);
        }


        HBox metaRow = new HBox(8);
        metaRow.setPadding(new Insets(0, 16, 12, 16));
        metaRow.setAlignment(Pos.CENTER_LEFT);

        HBox chips = new HBox(8);
        chips.setAlignment(Pos.CENTER_LEFT);
        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actions, Priority.ALWAYS);


        Label deadlineLbl = buildDeadlineChip(task);
        chips.getChildren().add(deadlineLbl);


        Label assigneeLbl = new Label(draft ? "Unassigned" : "@" + task.getAssigneeId());
        assigneeLbl.setStyle(draft
                ? "-fx-text-fill:#FBB024; -fx-font-size:11px; -fx-background-color:#2C2010; -fx-background-radius:20; -fx-padding:3 8 3 8;"
                : "-fx-text-fill:#9B97BC; -fx-font-size:11px; -fx-background-color:#1E1C2E; -fx-background-radius:20; -fx-padding:3 8 3 8;");


        Label statusLbl = new Label(statusLabel(task.getStatus()));
        statusLbl.setStyle(statusStyle(task.getStatus()));
        statusLbl.setPrefWidth(90);
        statusLbl.setAlignment(Pos.CENTER);

        chips.getChildren().addAll(assigneeLbl, statusLbl);


        VBox expandPanel = new VBox(8);
        expandPanel.setPadding(new Insets(0, 16, 10, 16));
        expandPanel.setVisible(false);
        expandPanel.setManaged(false);
        expandPanel.setStyle("-fx-border-color:#2D2845; -fx-border-width: 1 0 0 0;");


        if (isAssignee && task.getStatus() != TaskStatus.DONE) {
            Button changeStatusBtn = buildActionBtn("⟳ Status", "#7C6AF7");
            changeStatusBtn.setOnAction(ev -> toggleExpand(expandPanel, "cs",
                    () -> buildChangeStatusRow(task, statusLbl, accentBar, expandPanel)));
            actions.getChildren().add(changeStatusBtn);
        }


        if (isLeader) {
            Button doneBtn = buildActionBtn("✔ Done", "#34D399");
            doneBtn.setOnAction(ev -> {
                taskViewModel.markDone(task);
                task.setStatus(TaskStatus.DONE);
                statusLbl.setText("Done");
                statusLbl.setStyle(statusStyle(TaskStatus.DONE));
                accentBar.setStyle("-fx-background-color:#34D399;");
                doneBtn.setDisable(true);
            });
            doneBtn.setDisable(task.getStatus() == TaskStatus.DONE);

            Button dlBtn = buildActionBtn("📅 Deadline", "#F5A623");
            dlBtn.setOnAction(ev -> toggleExpand(expandPanel, "dl",
                    () -> buildDeadlineExpandRow(task, deadlineLbl, expandPanel)));

            Button raBtn = buildActionBtn("↺ Reassign", "#9B97BC");
            raBtn.setOnAction(ev -> toggleExpand(expandPanel, "ra",
                    () -> buildReassignExpandRow(task, assigneeLbl, expandPanel)));

            actions.getChildren().addAll(dlBtn, raBtn, doneBtn);
        }


        titleLbl.setOnMouseClicked(e -> onTaskSelected(task));
        titleLbl.setStyle(titleLbl.getStyle() + " -fx-cursor:hand;");

        metaRow.getChildren().addAll(chips, actions);
        content.getChildren().addAll(titleRow, metaRow, expandPanel);
        body.getChildren().addAll(accentBar, content);


        Pane sep = new Pane();
        sep.setPrefHeight(1);
        sep.setMaxHeight(1);
        sep.setStyle("-fx-background-color:#2C2A3E;");

        row.getChildren().addAll(body, sep);
        return row;
    }


    private void toggleExpand(VBox expandPanel, String tag, java.util.function.Supplier<Node> contentBuilder) {
        boolean alreadyOpen = expandPanel.isVisible() && tag.equals(expandPanel.getUserData());
        expandPanel.getChildren().clear();
        if (alreadyOpen) {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        } else {
            expandPanel.getChildren().add(contentBuilder.get());
            expandPanel.setUserData(tag);
            expandPanel.setVisible(true);
            expandPanel.setManaged(true);
            FadeTransition ft = new FadeTransition(Duration.millis(180), expandPanel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        }
    }

    private HBox buildDeadlineExpandRow(Task task, Label deadlineLbl, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 2, 0));

        Label lbl = smallLabel("New deadline:");
        LocalDate current = DateAndTime.parseDate(task.getDueDate());
        DatePicker dp = new DatePicker(current != null ? current : LocalDate.now());
        dp.setPrefWidth(160);
        dp.setPrefHeight(30);
        dp.getStyleClass().add("task-date-picker");
        dp.setStyle("-fx-font-size:12px;");
        dp.setConverter(dateConverter());

        Button saveBtn = buildActionBtn("✔ Save", "#34D399");
        saveBtn.setOnAction(ev -> {
            taskViewModel.updateDeadline(task, dp.getValue());
            refreshDeadlineChip(deadlineLbl, task);
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });
        Button cancelBtn = buildActionBtn("✕", "#4A475E");
        cancelBtn.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        row.getChildren().addAll(lbl,
                nudge("−7d", dp, -7), nudge("−1d", dp, -1), dp,
                nudge("+1d", dp, 1), nudge("+7d", dp, 7),
                saveBtn, cancelBtn);
        return row;
    }

    private HBox buildReassignExpandRow(Task task, Label assigneeLbl, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 2, 0));

        Label lbl = smallLabel("Reassign to:");
        TextField field = new TextField(task.getAssigneeId() != null ? task.getAssigneeId() : "");
        field.setPromptText("Username...");
        field.setPrefWidth(200);
        field.setPrefHeight(30);
        field.getStyleClass().add("task-input");

        Button saveBtn = buildActionBtn("✔ Reassign", "#34D399");
        saveBtn.setOnAction(ev -> {
            String newMember = field.getText().trim();
            taskViewModel.reassign(task, newMember);
            task.setAssigneeId(newMember);
            assigneeLbl.setText("@" + newMember);
            assigneeLbl.setStyle("-fx-text-fill:#9B97BC; -fx-font-size:11px; -fx-background-color:#1E1C2E; -fx-background-radius:20; -fx-padding:3 8 3 8;");
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });
        Button cancelBtn = buildActionBtn("✕", "#4A475E");
        cancelBtn.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        row.getChildren().addAll(lbl, field, saveBtn, cancelBtn);
        return row;
    }

    private HBox buildChangeStatusRow(Task task, Label statusLbl, Pane accentBar, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 4, 0));

        Label lbl = smallLabel("Set status:");
        row.getChildren().add(lbl);

        for (TaskStatus s : TaskStatus.values()) {
            Button sb = new Button(statusLabel(s));
            sb.setStyle(statusStyle(s) + (s == task.getStatus()
                    ? " -fx-cursor:hand; -fx-border-width:2;"
                    : " -fx-cursor:hand; -fx-opacity:0.50;"));
            sb.setOnAction(ev -> {
                taskViewModel.updateStatus(task, s);
                task.setStatus(s);
                statusLbl.setText(statusLabel(s));
                statusLbl.setStyle(statusStyle(s));
                // re-color accent bar
                String c = switch (s) {
                    case TODO -> "#3D3B55";
                    case IN_PROGRESS -> "#7C6AF7";
                    case IN_REVIEW -> "#F5A623";
                    case DONE -> "#34D399";
                };
                accentBar.setStyle("-fx-background-color:" + c + ";");
                ScaleTransition pop = new ScaleTransition(Duration.millis(200), statusLbl);
                pop.setFromX(0.80);
                pop.setFromY(0.80);
                pop.setToX(1.0);
                pop.setToY(1.0);
                pop.play();
                FadeTransition ft = new FadeTransition(Duration.millis(160), expandPanel);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(e -> {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    expandPanel.setOpacity(1);
                });
                ft.play();
            });
            row.getChildren().add(sb);
        }
        return row;
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


    private Label buildDeadlineChip(Task task) {
        boolean overdue = false;
        String text = "No due date";
        if (task.getDueDate() != null && !task.getDueDate().isBlank()) {
            text = "📅 " + task.getDueDate();
            LocalDate due = DateAndTime.parseDate(task.getDueDate());
            overdue = due != null && due.isBefore(LocalDate.now())
                    && task.getStatus() != TaskStatus.DONE;
        }
        Label chip = new Label(text);
        chip.setStyle(overdue
                ? "-fx-text-fill:#F87171; -fx-font-size:11px; -fx-background-color:#2C1010; -fx-background-radius:20; -fx-padding:3 8 3 8;"
                : "-fx-text-fill:#6B6882; -fx-font-size:11px; -fx-background-color:#14121E; -fx-background-radius:20; -fx-padding:3 8 3 8;");
        return chip;
    }

    private void refreshDeadlineChip(Label chip, Task task) {
        if (task.getDueDate() != null && !task.getDueDate().isBlank()) {
            chip.setText("📅 " + task.getDueDate());
            chip.setStyle("-fx-text-fill:#6B6882; -fx-font-size:11px; -fx-background-color:#14121E; -fx-background-radius:20; -fx-padding:3 8 3 8;");
        } else {
            chip.setText("No due date");
        }
    }

    private String statusLabel(TaskStatus s) {
        return switch (s) {
            case TODO -> "To Do";
            case IN_PROGRESS -> "In Progress";
            case IN_REVIEW -> "In Review";
            case DONE -> "Done";
        };
    }

    private String statusStyle(TaskStatus s) {
        return switch (s) {
            case TODO ->
                    "-fx-text-fill:#8BA3CC; -fx-background-color:#1C2233; -fx-background-radius:20; -fx-font-size:11px; -fx-font-family:'System Bold'; -fx-padding:3 8 3 8;";
            case IN_PROGRESS ->
                    "-fx-text-fill:#A78BFA; -fx-background-color:#1E1633; -fx-background-radius:20; -fx-font-size:11px; -fx-font-family:'System Bold'; -fx-padding:3 8 3 8;";
            case IN_REVIEW ->
                    "-fx-text-fill:#F5A623; -fx-background-color:#251900; -fx-background-radius:20; -fx-font-size:11px; -fx-font-family:'System Bold'; -fx-padding:3 8 3 8;";
            case DONE ->
                    "-fx-text-fill:#34D399; -fx-background-color:#0C2018; -fx-background-radius:20; -fx-font-size:11px; -fx-font-family:'System Bold'; -fx-padding:3 8 3 8;";
        };
    }

    private Button buildActionBtn(String text, String accentHex) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:#1E1C2E; -fx-text-fill:" + accentHex
                + "; -fx-font-size:11px; -fx-background-radius:8;"
                + " -fx-border-color:" + accentHex + "33; -fx-border-radius:8; -fx-border-width:1;"
                + " -fx-cursor:hand;");
        b.setPadding(new Insets(3, 10, 3, 10));
        return b;
    }

    private Label smallLabel(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web("#9D8FBF"));
        l.setStyle("-fx-font-size:12px;");
        return l;
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