package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Bug.model.Bug;
import com.example.flora.Features.Bug.model.BugSeverity;
import com.example.flora.Features.Bug.model.BugStatus;
import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;


public class BugSectionHandler {

    private final ProjectDetailViewModel viewModel;


    private Button reportBugToggleBtn;
    private VBox reportBugPanel;
    private TextField bugTitleInput;
    private TextField bugReporterInput;
    private ComboBox<String> bugSeverityCombo;
    private Label bugReportFeedback;
    private Button bugFilterAll;
    private Button bugFilterOpen;
    private Button bugFilterProgress;
    private Button bugFilterClosed;
    private VBox bugList;
    private ScrollPane bugScroll;

    private boolean reportBugPanelOpen = false;

    public BugSectionHandler(ProjectDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void bind(Button reportBugToggleBtn, VBox reportBugPanel,
                     TextField bugTitleInput, TextField bugReporterInput,
                     ComboBox<String> bugSeverityCombo, Label bugReportFeedback,
                     Button bugFilterAll, Button bugFilterOpen,
                     Button bugFilterProgress, Button bugFilterClosed,
                     VBox bugList, ScrollPane bugScroll) {

        this.reportBugToggleBtn = reportBugToggleBtn;
        this.reportBugPanel = reportBugPanel;
        this.bugTitleInput = bugTitleInput;
        this.bugReporterInput = bugReporterInput;
        this.bugSeverityCombo = bugSeverityCombo;
        this.bugReportFeedback = bugReportFeedback;
        this.bugFilterAll = bugFilterAll;
        this.bugFilterOpen = bugFilterOpen;
        this.bugFilterProgress = bugFilterProgress;
        this.bugFilterClosed = bugFilterClosed;
        this.bugList = bugList;
        this.bugScroll = bugScroll;
    }


    public void toggleReportBugPanel(ActionEvent e) {
        reportBugPanelOpen = !reportBugPanelOpen;
        reportBugPanel.setVisible(reportBugPanelOpen);
        reportBugPanel.setManaged(reportBugPanelOpen);

        AnchorPane.setTopAnchor(bugScroll, reportBugPanelOpen ? 230.0 : 92.0);

        if (reportBugPanelOpen) {
            reportBugPanel.setOpacity(0);
            reportBugPanel.setTranslateY(-12);

            FadeTransition ft = new FadeTransition(Duration.millis(220), reportBugPanel);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            javafx.animation.TranslateTransition tt =
                    new javafx.animation.TranslateTransition(Duration.millis(220), reportBugPanel);
            tt.setFromY(-12);
            tt.setToY(0);
            tt.play();

            reportBugToggleBtn.setText("✕  Cancel");
            reportBugToggleBtn.getStyleClass().removeAll("btn-decline");
            reportBugToggleBtn.getStyleClass().add("slide-close-btn");

            bugTitleInput.clear();
            bugReporterInput.clear();
            bugSeverityCombo.getSelectionModel().selectFirst();
            bugReportFeedback.setText("");
        } else {
            reportBugToggleBtn.setText("🐛  Report Bug");
            reportBugToggleBtn.getStyleClass().removeAll("slide-close-btn");
            reportBugToggleBtn.getStyleClass().add("btn-decline");
        }
    }

    public void reportBug(ActionEvent event) {
        String title = bugTitleInput.getText().trim();
        String reporter = bugReporterInput.getText().trim();
        String severityRaw = bugSeverityCombo.getValue();

        if (title.isEmpty()) {
            showBugFeedback("⚠  Please enter a bug title.", false);
            return;
        }
        if (reporter.isEmpty()) {
            showBugFeedback("⚠  Reporter username is required.", false);
            return;
        }
        if (severityRaw == null) {
            showBugFeedback("⚠  Please select a severity.", false);
            return;
        }

        viewModel.reportBug(title, reporter, parseSeverity(severityRaw));
        showBugFeedback("✔  Bug reported successfully!", true);

        bugTitleInput.clear();
        bugReporterInput.clear();
        bugSeverityCombo.getSelectionModel().selectFirst();

        PauseTransition pause = new PauseTransition(Duration.seconds(1.4));
        pause.setOnFinished(e -> {
            if (reportBugPanelOpen) toggleReportBugPanel(null);
        });
        pause.play();
    }

    private void showBugFeedback(String msg, boolean ok) {
        bugReportFeedback.setText(msg);
        bugReportFeedback.setStyle(
                "-fx-text-fill: " + (ok ? "#34D399" : "#F87171") + "; -fx-font-size:12px;");
    }

    private BugSeverity parseSeverity(String raw) {
        if (raw == null) return BugSeverity.MEDIUM;
        String upper = raw.toUpperCase();
        if (upper.contains("CRITICAL")) return BugSeverity.CRITICAL;
        if (upper.contains("HIGH")) return BugSeverity.HIGH;
        if (upper.contains("LOW")) return BugSeverity.LOW;
        return BugSeverity.MEDIUM;
    }


    public void bugFilterAll(ActionEvent e) {
        setBugFilter("ALL", bugFilterAll);
    }

    public void bugFilterOpen(ActionEvent e) {
        setBugFilter("OPEN", bugFilterOpen);
    }

    public void bugFilterProgress(ActionEvent e) {
        setBugFilter("IN_PROGRESS", bugFilterProgress);
    }

    public void bugFilterClosed(ActionEvent e) {
        setBugFilter("CLOSED", bugFilterClosed);
    }

    private void setBugFilter(String filter, Button active) {
        viewModel.setActiveBugFilter(filter);
        for (Button b : new Button[]{bugFilterAll, bugFilterOpen, bugFilterProgress, bugFilterClosed}) {
            b.getStyleClass().removeAll("task-filter-btn-active", "task-filter-btn");
            b.getStyleClass().add(b == active ? "task-filter-btn-active" : "task-filter-btn");
        }
    }


    public void renderBugs() {
        bugList.getChildren().clear();
        List<Bug> list = viewModel.getFilteredBugs();
        if (list.isEmpty()) {
            bugList.getChildren().add(DetailUIHelper.emptyLabel("No bugs in this view."));
            return;
        }
        int delay = 0;
        for (Bug bug : list) {
            VBox card = buildBugCard(bug);
            bugList.getChildren().add(card);
            DetailUIHelper.staggerIn(card, delay);
            delay += 40;
        }
    }


    private VBox buildBugCard(Bug bug) {
        VBox card = new VBox(0);
        card.getStyleClass().add("detail-list-row");
        card.setStyle(DetailUIHelper.severityLeftBorder(bug.getSeverity()));

        HBox line1 = new HBox(10);
        line1.setPadding(new Insets(10, 14, 10, 14));
        line1.setAlignment(Pos.CENTER_LEFT);

        if (bug.isUnclaimed() && !bug.isClosed()) {
            Label dot = new Label("●");
            dot.setStyle("-fx-text-fill: #F87171; -fx-font-size: 10px;"
                    + "-fx-effect: dropshadow(gaussian, rgba(248,113,113,0.70), 6,0,0,0);");
            line1.getChildren().add(dot);
        }

        Label titleLbl = new Label("🐛  " + bug.getTitle());
        titleLbl.setTextFill(Color.web("#EDE9F6"));
        titleLbl.setWrapText(true);
        titleLbl.getStyleClass().add("row-title");
        HBox.setHgrow(titleLbl, Priority.ALWAYS);

        Label sevBadge = new Label(bug.getSeverity().displayName());
        sevBadge.getStyleClass().add(DetailUIHelper.severityBadgeClass(bug.getSeverity()));
        sevBadge.setPadding(new Insets(3, 8, 3, 8));

        Label reporterChip = new Label("👤 @" + bug.getReportedByUserId());
        reporterChip.getStyleClass().add("unclaimed-chip");
        reporterChip.setPadding(new Insets(3, 8, 3, 8));

        Label fixerLbl = new Label(bug.isUnclaimed() ? "Unclaimed" : "🔧 @" + bug.getFixingUserId());
        fixerLbl.getStyleClass().add(bug.isUnclaimed() ? "fixer-unclaimed" : "pill-label");
        fixerLbl.setPadding(new Insets(3, 8, 3, 8));

        Label statusLbl = new Label(bug.getStatus().displayName());
        statusLbl.getStyleClass().add(DetailUIHelper.bugStatusStyleClass(bug.getStatus()));
        statusLbl.setPadding(new Insets(3, 8, 3, 8));
        statusLbl.setAlignment(Pos.CENTER);

        line1.getChildren().addAll(titleLbl, reporterChip, sevBadge, fixerLbl, statusLbl);


        VBox expandPanel = new VBox(8);
        expandPanel.setPadding(new Insets(4, 14, 10, 14));
        expandPanel.setStyle("-fx-border-color: #2D2845; -fx-border-width: 1 0 0 0;");
        expandPanel.setVisible(false);
        expandPanel.setManaged(false);

        String currentUserId = viewModel.getCurrentUserId();
        boolean isFixer = !bug.isUnclaimed() && currentUserId.equals(bug.getFixingUserId());
        boolean isLeader = viewModel.isLeader();

        if (!bug.isClosed()) {
            HBox actionRow = new HBox(8);
            actionRow.setAlignment(Pos.CENTER_LEFT);
            actionRow.setPadding(new Insets(6, 0, 2, 0));

            if (isFixer) {
                Button markFixed = new Button("✅  Mark as Fixed");
                markFixed.getStyleClass().add("btn-accept");
                markFixed.setPadding(new Insets(4, 12, 4, 12));
                markFixed.setOnAction(e -> {
                    viewModel.markBugFixed(bug);
                    FadeTransition ft = new FadeTransition(Duration.millis(300), card);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(ev -> renderBugs());
                    ft.play();
                });
                actionRow.getChildren().add(markFixed);

                Button updateStatusBtn = new Button("⟳ Update Status");
                updateStatusBtn.getStyleClass().add("update-btn");
                updateStatusBtn.setPadding(new Insets(4, 12, 4, 12));
                updateStatusBtn.setOnAction(e -> {
                    BugStatus next = bug.getStatus() == BugStatus.IN_PROGRESS
                            ? BugStatus.OPEN : BugStatus.IN_PROGRESS;
                    viewModel.updateBugStatus(bug, next);
                    statusLbl.setText(next.displayName());
                    statusLbl.getStyleClass().removeAll(
                            "bug-status-open", "bug-status-progress", "bug-status-closed",
                            "status-todo", "status-progress", "status-done");
                    statusLbl.getStyleClass().add(DetailUIHelper.bugStatusStyleClass(next));
                    ScaleTransition pop = new ScaleTransition(Duration.millis(180), statusLbl);
                    pop.setFromX(0.8);
                    pop.setFromY(0.8);
                    pop.setToX(1.0);
                    pop.setToY(1.0);
                    pop.play();
                });
                actionRow.getChildren().add(updateStatusBtn);
            }

            if (!isLeader && bug.isUnclaimed()) {
                Button claimBtn = new Button("🔧  I'll fix it");
                claimBtn.getStyleClass().add("btn-accept");
                claimBtn.setPadding(new Insets(4, 12, 4, 12));
                claimBtn.setOnAction(e -> {
                    viewModel.claimBug(bug);
                    fixerLbl.setText("🔧 @" + currentUserId);
                    fixerLbl.getStyleClass().removeAll("fixer-unclaimed");
                    fixerLbl.getStyleClass().add("pill-label");
                    statusLbl.setText(BugStatus.IN_PROGRESS.displayName());
                    statusLbl.getStyleClass().removeAll("bug-status-open", "status-todo");
                    statusLbl.getStyleClass().add(DetailUIHelper.bugStatusStyleClass(BugStatus.IN_PROGRESS));
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    renderBugs();
                });
                actionRow.getChildren().add(claimBtn);
            }

            if (isLeader) {
                String btnLabel = bug.isUnclaimed() ? "👤  Assign →" : "🔄  Re-assign →";
                Button assignBugBtn = new Button(btnLabel);
                assignBugBtn.getStyleClass().add("update-btn");
                assignBugBtn.setPadding(new Insets(4, 12, 4, 12));
                assignBugBtn.setOnAction(e -> {
                    boolean open = "assign".equals(expandPanel.getUserData()) && expandPanel.isVisible();
                    expandPanel.getChildren().clear();
                    if (!open) {
                        expandPanel.getChildren().add(buildBugAssignRow(bug, expandPanel, fixerLbl, statusLbl));
                        expandPanel.setUserData("assign");
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
                actionRow.getChildren().add(assignBugBtn);
            }

            if (!actionRow.getChildren().isEmpty())
                expandPanel.getChildren().add(actionRow);
        }

        card.getChildren().addAll(line1, expandPanel);


        line1.setStyle("-fx-cursor: hand;");
        line1.setOnMouseClicked(e -> {
            if (expandPanel.getChildren().isEmpty()) return;
            boolean open = expandPanel.isVisible();
            if (!open) {
                expandPanel.setOpacity(0);
                expandPanel.setVisible(true);
                expandPanel.setManaged(true);
                FadeTransition ft = new FadeTransition(Duration.millis(200), expandPanel);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            } else {
                FadeTransition ft = new FadeTransition(Duration.millis(180), expandPanel);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(ev -> {
                    expandPanel.setVisible(false);
                    expandPanel.setManaged(false);
                    expandPanel.setOpacity(1);
                });
                ft.play();
            }
        });

        return card;
    }


    private HBox buildBugAssignRow(Bug bug, VBox expandPanel, Label fixerLbl, Label statusLbl) {
        HBox aRow = new HBox(8);
        aRow.setAlignment(Pos.CENTER_LEFT);
        aRow.setPadding(new Insets(2, 0, 2, 0));

        boolean isReassign = !bug.isUnclaimed();
        Label lbl = new Label(isReassign ? "Re-assign to:" : "Assign to member:");
        lbl.setTextFill(Color.web("#9D8FBF"));
        lbl.setStyle("-fx-font-size:12px;");

        TextField f = new TextField(isReassign ? bug.getFixingUserId() : "");
        f.setPromptText("Username...");
        f.setPrefWidth(200);
        f.setPrefHeight(30);
        f.getStyleClass().add("modal-text-field");

        Button ok = new Button(isReassign ? "✔ Re-assign" : "✔ Assign");
        ok.getStyleClass().add("btn-accept");
        ok.setPadding(new Insets(3, 10, 3, 10));
        ok.setOnAction(ev -> {
            String assignee = f.getText().trim();
            if (!assignee.isEmpty()) {
                boolean success = viewModel.assignBug(bug, assignee);
                if (success) {
                    fixerLbl.setText("🔧 @" + assignee);
                    fixerLbl.getStyleClass().removeAll("fixer-unclaimed");
                    fixerLbl.getStyleClass().add("pill-label");
                    statusLbl.setText(BugStatus.IN_PROGRESS.displayName());
                    statusLbl.getStyleClass().removeAll("bug-status-open", "status-todo");
                    statusLbl.getStyleClass().add(DetailUIHelper.bugStatusStyleClass(BugStatus.IN_PROGRESS));
                    ScaleTransition pop = new ScaleTransition(Duration.millis(180), fixerLbl);
                    pop.setFromX(0.8); pop.setFromY(0.8);
                    pop.setToX(1.0);   pop.setToY(1.0);
                    pop.play();
                } else {
                    Label err = new Label("⚠ Assign failed. Check username or permissions.");
                    err.setStyle("-fx-text-fill:#F87171; -fx-font-size:11px;");
                    expandPanel.getChildren().add(err);
                    return;
                }
            }
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });
        Button cx = new Button("✕");
        cx.getStyleClass().add("slide-close-btn");
        cx.setPadding(new Insets(3, 8, 3, 8));
        cx.setOnAction(ev -> {
            expandPanel.setVisible(false);
            expandPanel.setManaged(false);
        });

        aRow.getChildren().addAll(lbl, f, ok, cx);
        return aRow;
    }
}