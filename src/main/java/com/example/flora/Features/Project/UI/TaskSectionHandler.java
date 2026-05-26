package com.example.flora.Features.Project.UI;

import com.example.flora.Core.Helper.DateAndTime;
import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import com.example.flora.Features.Task.model.Task;
import com.example.flora.Features.Task.model.TaskStatus;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel.DATE_FMT;


public class TaskSectionHandler {

    private final ProjectDetailViewModel viewModel;


    private Button addTaskToggleBtn;
    private MenuButton filterMenuBtn;
    private VBox addTaskPanel;
    private Button modeAssignBtn;
    private Button modeDraftBtn;
    private HBox assignFields;
    private HBox draftFields;
    private TextField taskTitleInput;
    private TextField taskAssigneeInput;
    private Label assignDeadlineChip;
    private TextField draftTitleInput;
    private Label draftDeadlineChip;
    private VBox taskList;
    private ScrollPane taskScroll;

    private boolean addPanelOpen = false;

    public TaskSectionHandler(ProjectDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void bind(Button addTaskToggleBtn, MenuButton filterMenuBtn,
                     VBox addTaskPanel, Button modeAssignBtn, Button modeDraftBtn,
                     HBox assignFields, HBox draftFields,
                     TextField taskTitleInput, TextField taskAssigneeInput,
                     Label assignDeadlineChip, TextField draftTitleInput,
                     Label draftDeadlineChip, VBox taskList, ScrollPane taskScroll) {

        this.addTaskToggleBtn = addTaskToggleBtn;
        this.filterMenuBtn = filterMenuBtn;
        this.addTaskPanel = addTaskPanel;
        this.modeAssignBtn = modeAssignBtn;
        this.modeDraftBtn = modeDraftBtn;
        this.assignFields = assignFields;
        this.draftFields = draftFields;
        this.taskTitleInput = taskTitleInput;
        this.taskAssigneeInput = taskAssigneeInput;
        this.assignDeadlineChip = assignDeadlineChip;
        this.draftTitleInput = draftTitleInput;
        this.draftDeadlineChip = draftDeadlineChip;
        this.taskList = taskList;
        this.taskScroll = taskScroll;
    }


    public void toggleAddTaskPanel(ActionEvent e) {
        addPanelOpen = !addPanelOpen;
        addTaskPanel.setVisible(addPanelOpen);
        addTaskPanel.setManaged(addPanelOpen);
        addTaskToggleBtn.setText(addPanelOpen ? "✕  Cancel" : "➕  Add Task");
        addTaskToggleBtn.getStyleClass().removeAll("btn-accept", "slide-close-btn");
        addTaskToggleBtn.getStyleClass().add(addPanelOpen ? "slide-close-btn" : "btn-accept");
        AnchorPane.setTopAnchor(taskScroll, addPanelOpen ? 170.0 : 52.0);
    }

    public void switchToAssignMode(ActionEvent e) {
        assignFields.setVisible(true);
        assignFields.setManaged(true);
        draftFields.setVisible(false);
        draftFields.setManaged(false);
        setModeActive(modeAssignBtn, modeDraftBtn);
    }

    public void switchToDraftMode(ActionEvent e) {
        assignFields.setVisible(false);
        assignFields.setManaged(false);
        draftFields.setVisible(true);
        draftFields.setManaged(true);
        setModeActive(modeDraftBtn, modeAssignBtn);
    }

    private void setModeActive(Button active, Button inactive) {
        active.getStyleClass().removeAll("task-filter-btn", "task-filter-btn-active");
        active.getStyleClass().add("task-filter-btn-active");
        inactive.getStyleClass().removeAll("task-filter-btn", "task-filter-btn-active");
        inactive.getStyleClass().add("task-filter-btn");
    }


    public void toggleAssignDeadline(MouseEvent e) {
        toggleDeadlineRow("assign-dl-row", assignFields, assignDeadlineChip, true);
    }

    public void toggleDraftDeadline(MouseEvent e) {
        toggleDeadlineRow("draft-dl-row", draftFields, draftDeadlineChip, false);
    }

    private void toggleDeadlineRow(String id, HBox anchor, Label chip, boolean isAssign) {
        int anchorIdx = addTaskPanel.getChildren().indexOf(anchor);
        boolean exists = addTaskPanel.getChildren().stream()
                .anyMatch(n -> id.equals(n.getUserData()));
        if (exists) {
            addTaskPanel.getChildren().removeIf(n -> id.equals(n.getUserData()));
            chip.setText("📅  Set deadline");
            if (isAssign) viewModel.clearPendingAssignDeadline();
            else viewModel.clearPendingDraftDeadline();
        } else {
            addTaskPanel.getChildren().add(anchorIdx + 1, buildInlineDateRow(id, chip, isAssign));
        }
    }

    private HBox buildInlineDateRow(String id, Label chip, boolean isAssign) {
        HBox row = new HBox(8);
        row.setUserData(id);
        row.setAlignment(Pos.CENTER_LEFT);

        DatePicker dp = new DatePicker();
        dp.setPromptText("Pick a date...");
        dp.setPrefWidth(170);
        dp.setPrefHeight(32);
        dp.getStyleClass().add("modal-text-field");
        dp.setStyle("-fx-font-size: 12px;");
        dp.setConverter(DetailUIHelper.dateConverter());

        Button okBtn = new Button("✔  Set");
        okBtn.getStyleClass().add("btn-accept");
        okBtn.setPadding(new Insets(3, 10, 3, 10));
        okBtn.setOnAction(ev -> {
            LocalDate chosen = dp.getValue();
            if (isAssign) viewModel.setPendingAssignDeadline(chosen);
            else viewModel.setPendingDraftDeadline(chosen);
            chip.setText(chosen != null ? "📅  " + chosen.format(DATE_FMT) : "📅  Set deadline");
            addTaskPanel.getChildren().removeIf(n -> id.equals(n.getUserData()));
        });

        row.getChildren().addAll(
                DetailUIHelper.quickAdjust("−7d", dp, -7),
                DetailUIHelper.quickAdjust("−1d", dp, -1),
                dp,
                DetailUIHelper.quickAdjust("+1d", dp, 1),
                DetailUIHelper.quickAdjust("+7d", dp, 7),
                okBtn);
        return row;
    }


    public void filterMyTasks(ActionEvent e) {
        setTaskFilter("👤  My Tasks", "MY");
    }

    public void filterAllTasks(ActionEvent e) {
        setTaskFilter("📋  All Tasks", "ALL");
    }

    public void filterCompleted(ActionEvent e) {
        setTaskFilter("✅  Completed", "COMPLETED");
    }

    public void filterDue(ActionEvent e) {
        setTaskFilter("⏳  Due / Overdue", "DUE");
    }

    public void filterDrafts(ActionEvent e) {
        setTaskFilter("✏  Drafts", "DRAFTS");
    }

    public void filterByAssignee(ActionEvent e) {
        filterMenuBtn.setText("👥  By Assignee");
        renderGroupedByAssignee();
    }

    private void setTaskFilter(String label, String mode) {
        filterMenuBtn.setText(label);
        viewModel.setActiveTaskFilter(mode);
    }


    public void renderTasks() {
        taskList.getChildren().clear();
        List<Task> list = viewModel.getFilteredTasks();
        if (list.isEmpty()) {
            taskList.getChildren().add(DetailUIHelper.emptyLabel("No tasks for this filter."));
        } else {
            for (Task t : list) taskList.getChildren().add(buildTaskCard(t));
        }
    }

    private void renderGroupedByAssignee() {
        taskList.getChildren().clear();
        Map<String, List<Task>> grouped = new LinkedHashMap<>();
        viewModel.getTasks().stream()
                .filter(t -> !viewModel.isDraft(t))
                .forEach(t -> grouped.computeIfAbsent(t.getAssigneeId(), k -> new ArrayList<>()).add(t));

        if (grouped.isEmpty()) {
            taskList.getChildren().add(DetailUIHelper.emptyLabel("No tasks yet."));
            return;
        }
        for (Map.Entry<String, List<Task>> entry : grouped.entrySet()) {
            taskList.getChildren().add(buildAssigneeHeader(entry.getKey(), entry.getValue().size()));
            for (Task t : entry.getValue()) {
                VBox card = buildTaskCard(t);
                card.setPadding(new Insets(0, 0, 0, 18));
                taskList.getChildren().add(card);
            }
        }
    }


    private VBox buildTaskCard(Task t) {
        boolean draft = viewModel.isDraft(t);

        VBox card = new VBox(0);
        card.getStyleClass().add("detail-list-row");
        card.setPrefWidth(750);

        // Title row
        HBox titleRow = new HBox(10);
        titleRow.setPadding(new Insets(10, 14, 4, 14));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(draft ? "✏ " + t.getTitle() : t.getTitle());
        titleLbl.setTextFill(Color.web(draft ? "#FBB024" : "#EDE9F6"));
        titleLbl.getStyleClass().add("row-title");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);
        titleRow.getChildren().add(titleLbl);

        if (!draft && t.getCreatedAt() != null && !t.getCreatedAt().isBlank()) {
            Label createdChip = new Label("🗓 " + t.getCreatedAt());
            createdChip.getStyleClass().add("created-at-chip");
            createdChip.setPadding(new Insets(3, 8, 3, 8));
            titleRow.getChildren().add(createdChip);
        }

        HBox metaRow = new HBox(10);
        metaRow.setPadding(new Insets(0, 14, 10, 14));
        metaRow.setAlignment(Pos.CENTER_LEFT);

        HBox chips = new HBox(10);
        chips.setAlignment(Pos.CENTER_LEFT);
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(actions, Priority.ALWAYS);

        Label deadlineChip = DetailUIHelper.buildDeadlineChip(t);

        Label assigneeLbl = new Label(draft ? "Unassigned" : "@" + t.getAssigneeId());
        assigneeLbl.getStyleClass().add(draft ? "fixer-unclaimed" : "pill-label");
        assigneeLbl.setPadding(new Insets(3, 8, 3, 8));

        Label statusLbl = new Label(t.getStatus().name());
        statusLbl.getStyleClass().add(DetailUIHelper.statusStyleClass(t.getStatus()));
        statusLbl.setPadding(new Insets(3, 8, 3, 8));
        statusLbl.setPrefWidth(95);
        statusLbl.setAlignment(Pos.CENTER);

        chips.getChildren().addAll(deadlineChip, assigneeLbl, statusLbl);

        VBox expandPanel = new VBox(8);
        expandPanel.setPadding(new Insets(0, 14, 10, 14));
        expandPanel.setVisible(false);
        expandPanel.setManaged(false);
        expandPanel.setStyle("-fx-border-color: #2D2845; -fx-border-width: 1 0 0 0;");

        boolean isAssignee = !draft
                && t.getAssigneeId() != null
                && t.getAssigneeId().equalsIgnoreCase(viewModel.getCurrentUserId());

        if (isAssignee && t.getStatus() != TaskStatus.DONE) {
            Button changeStatusBtn = new Button("⟳ Status");
            changeStatusBtn.getStyleClass().add("update-btn");
            changeStatusBtn.setPadding(new Insets(3, 8, 3, 8));
            changeStatusBtn.setOnAction(ev -> {
                boolean open = expandPanel.isVisible();
                expandPanel.getChildren().clear();
                if (!open || !"cs".equals(expandPanel.getUserData())) {
                    expandPanel.getChildren().add(buildChangeStatusRow(t, statusLbl, expandPanel));
                    expandPanel.setUserData("cs");
                    expandPanel.setVisible(true);
                    expandPanel.setManaged(true);
                    FadeTransition ft = new FadeTransition(Duration.millis(200), expandPanel);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                } else {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                }
            });
            actions.getChildren().add(changeStatusBtn);
        }

        if (viewModel.isLeader()) {
            Button doneBtn = new Button("✔ Done");
            doneBtn.getStyleClass().add("btn-accept");
            doneBtn.setPadding(new Insets(3, 8, 3, 8));
            doneBtn.setOnAction(ev -> {
                viewModel.markTaskDone(t);
                statusLbl.setText(TaskStatus.DONE.name());
                statusLbl.getStyleClass().removeAll("status-todo", "status-progress", "status-review");
                statusLbl.getStyleClass().add("status-done");
            });

            Button editDlBtn = new Button("📅 Deadline");
            editDlBtn.getStyleClass().add("update-btn");
            editDlBtn.setPadding(new Insets(3, 8, 3, 8));
            editDlBtn.setOnAction(ev -> {
                boolean open = expandPanel.isVisible();
                expandPanel.getChildren().clear();
                if (!open || !"dl".equals(expandPanel.getUserData())) {
                    expandPanel.getChildren().add(buildDeadlineExpandRow(t, deadlineChip, expandPanel));
                    expandPanel.setUserData("dl");
                    expandPanel.setVisible(true);
                    expandPanel.setManaged(true);
                } else {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                }
            });

            Button reassignBtn = new Button("↺ Reassign");
            reassignBtn.getStyleClass().add("update-btn");
            reassignBtn.setPadding(new Insets(3, 8, 3, 8));
            reassignBtn.setOnAction(ev -> {
                boolean open = expandPanel.isVisible();
                expandPanel.getChildren().clear();
                if (!open || !"ra".equals(expandPanel.getUserData())) {
                    expandPanel.getChildren().add(buildReassignExpandRow(t, assigneeLbl, expandPanel));
                    expandPanel.setUserData("ra");
                    expandPanel.setVisible(true);
                    expandPanel.setManaged(true);
                } else {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                }
            });

            actions.getChildren().addAll(editDlBtn, reassignBtn, doneBtn);
        }

        metaRow.getChildren().addAll(chips, actions);
        card.getChildren().addAll(titleRow, metaRow, expandPanel);
        return card;
    }


    private HBox buildDeadlineExpandRow(Task t, Label deadlineChip, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 2, 0));

        Label lbl = new Label("New deadline:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size: 12px;");

        LocalDate current = DateAndTime.parseDate(t.getDueDate());
        DatePicker dp = new DatePicker(current != null ? current : LocalDate.now());
        dp.setPrefWidth(160);
        dp.setPrefHeight(30);
        dp.getStyleClass().add("modal-text-field");
        dp.setStyle("-fx-font-size: 12px;");
        dp.setConverter(DetailUIHelper.dateConverter());

        Button saveBtn = new Button("✔ Save");
        saveBtn.getStyleClass().add("btn-accept");
        saveBtn.setPadding(new Insets(3, 10, 3, 10));
        saveBtn.setOnAction(ev -> {
            viewModel.updateTaskDeadline(t, dp.getValue());
            DetailUIHelper.refreshDeadlineChip(deadlineChip, t);
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("slide-close-btn");
        cancelBtn.setPadding(new Insets(3, 8, 3, 8));
        cancelBtn.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        row.getChildren().addAll(lbl,
                DetailUIHelper.quickAdjust("−7d", dp, -7), DetailUIHelper.quickAdjust("−1d", dp, -1),
                dp,
                DetailUIHelper.quickAdjust("+1d", dp, 1), DetailUIHelper.quickAdjust("+7d", dp, 7),
                saveBtn, cancelBtn);
        return row;
    }

    private HBox buildReassignExpandRow(Task t, Label assigneeLabel, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 2, 0));

        Label lbl = new Label("Reassign to:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size: 12px;");

        TextField field = new TextField(t.getAssigneeId() != null ? t.getAssigneeId() : "");
        field.setPromptText("Username...");
        field.setPrefWidth(200);
        field.setPrefHeight(30);
        field.getStyleClass().add("modal-text-field");

        Button saveBtn = new Button("✔ Reassign");
        saveBtn.getStyleClass().add("btn-accept");
        saveBtn.setPadding(new Insets(3, 10, 3, 10));
        saveBtn.setOnAction(ev -> {
            String newMember = field.getText().trim();
            viewModel.reassignTask(t, newMember);
            assigneeLabel.setText("@" + newMember);
            assigneeLabel.getStyleClass().removeAll("fixer-unclaimed");
            assigneeLabel.getStyleClass().add("pill-label");
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("slide-close-btn");
        cancelBtn.setPadding(new Insets(3, 8, 3, 8));
        cancelBtn.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        row.getChildren().addAll(lbl, field, saveBtn, cancelBtn);
        return row;
    }

    private HBox buildChangeStatusRow(Task t, Label statusLbl, VBox expandPanel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 4, 0));

        Label lbl = new Label("Set status:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size: 12px;");

        TaskStatus[] allStatuses = TaskStatus.values();
        Button[] statusBtns = new Button[allStatuses.length];
        for (int i = 0; i < allStatuses.length; i++) {
            TaskStatus s = allStatuses[i];
            Button sb = new Button(DetailUIHelper.statusLabel(s));
            sb.getStyleClass().add(DetailUIHelper.statusStyleClass(s));
            sb.setPadding(new Insets(4, 10, 4, 10));
            sb.setStyle(s == t.getStatus() ? "-fx-cursor: hand; -fx-border-width: 2;"
                    : "-fx-cursor: hand; -fx-opacity: 0.50;");
            sb.setOnAction(ev -> {
                viewModel.updateTaskStatus(t, s);
                statusLbl.setText(s.name());
                statusLbl.getStyleClass().removeAll("status-todo", "status-progress", "status-review", "status-done");
                statusLbl.getStyleClass().add(DetailUIHelper.statusStyleClass(s));
                ScaleTransition pop = new ScaleTransition(Duration.millis(200), statusLbl);
                pop.setFromX(0.80);
                pop.setFromY(0.80);
                pop.setToX(1.0);
                pop.setToY(1.0);
                pop.play();
                FadeTransition ft = new FadeTransition(Duration.millis(180), expandPanel);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(e -> {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    expandPanel.setOpacity(1);
                });
                ft.play();
            });
            statusBtns[i] = sb;
        }
        row.getChildren().add(lbl);
        row.getChildren().addAll(statusBtns);
        return row;
    }


    public void assignTask(ActionEvent event) {
        viewModel.assignTask(
                taskTitleInput.getText().trim(),
                taskAssigneeInput.getText().trim(),
                viewModel.getPendingAssignDeadline());
        taskTitleInput.clear();
        taskAssigneeInput.clear();
        assignDeadlineChip.setText("📅  Set deadline");
        addTaskPanel.getChildren().removeIf(n -> "assign-dl-row".equals(n.getUserData()));
    }

    public void saveDraftTask(ActionEvent event) {
        viewModel.saveDraftTask(draftTitleInput.getText().trim(), viewModel.getPendingDraftDeadline());
        draftTitleInput.clear();
        draftDeadlineChip.setText("📅  Set deadline");
        addTaskPanel.getChildren().removeIf(n -> "draft-dl-row".equals(n.getUserData()));
    }


    private HBox buildAssigneeHeader(String assignee, int count) {
        HBox h = new HBox(10);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(12, 14, 4, 14));

        Label av = new Label(assignee.substring(0, 1).toUpperCase());
        av.getStyleClass().add("member-avatar");
        av.setPrefSize(26, 26);
        av.setAlignment(Pos.CENTER);

        Label name = new Label("@" + assignee);
        name.setTextFill(Color.web("#C4B5F5"));
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label cnt = new Label(count + (count == 1 ? " task" : " tasks"));
        cnt.getStyleClass().add("card-section-label");
        cnt.setPadding(new Insets(2, 8, 2, 8));
        cnt.setStyle("-fx-background-color: rgba(109,79,194,0.15);"
                + "-fx-background-radius: 10; -fx-border-color: rgba(109,79,194,0.30);"
                + "-fx-border-radius: 10; -fx-border-width: 1;");

        h.getChildren().addAll(av, name, cnt);
        return h;
    }
}