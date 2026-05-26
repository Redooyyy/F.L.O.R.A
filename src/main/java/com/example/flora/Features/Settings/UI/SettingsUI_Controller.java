package com.example.flora.Features.Settings.UI;

import com.example.flora.Features.Project.model.Project;
import com.example.flora.Features.Settings.viewmodel.SettingsViewModel;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SettingsUI_Controller implements Initializable {

    @FXML
    private Button navPersonal;
    @FXML
    private Button navNotifications;
    @FXML
    private Button navSecurity;
    @FXML
    private Button navProjects;
    @FXML
    private Button navAppearance;

    @FXML
    private AnchorPane panelPersonal;
    @FXML
    private AnchorPane panelNotifications;
    @FXML
    private AnchorPane panelSecurity;
    @FXML
    private AnchorPane panelProjects;
    @FXML
    private AnchorPane panelAppearance;

    @FXML
    private Circle avatarCircle;
    @FXML
    private Label avatarInitials;
    @FXML
    private Pane colorSwatch1, colorSwatch2, colorSwatch3;
    @FXML
    private Pane colorSwatch4, colorSwatch5, colorSwatch6;
    @FXML
    private TextField fieldDisplayName;
    @FXML
    private TextField fieldEmail;
    @FXML
    private TextArea fieldBio;
    @FXML
    private Label personalStatusLabel;

    @FXML
    private CheckBox toggleTaskAssign;
    @FXML
    private CheckBox toggleBugReport;
    @FXML
    private CheckBox toggleMention;
    @FXML
    private Label notifStatusLabel;

    @FXML
    private PasswordField fieldCurrentPwd;
    @FXML
    private PasswordField fieldNewPwd;
    @FXML
    private PasswordField fieldConfirmPwd;
    @FXML
    private Label securityStatusLabel;

    @FXML
    private Label projectCountChip;
    @FXML
    private VBox projectListVBox;
    @FXML
    private Label projectsStatusLabel;

    @FXML
    private VBox themeCardDark;
    @FXML
    private VBox themeCardLight;
    @FXML
    private Label darkCheckmark;


    @FXML
    private AnchorPane renameOverlay;
    @FXML
    private VBox renameDialog;
    @FXML
    private TextField renameField;
    @FXML
    private AnchorPane deleteOverlay;
    @FXML
    private VBox deleteDialog;
    @FXML
    private Label deleteConfirmLabel;


    private final SettingsViewModel viewModel;
    private AnchorPane activePanel;
    private Button activeNavBtn;
    private Project pendingRenameProject;
    private Project pendingDeleteProject;

    public SettingsUI_Controller(SettingsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.init();
        fieldDisplayName.textProperty().bindBidirectional(viewModel.displayNameProperty());
        fieldEmail.textProperty().bindBidirectional(viewModel.emailProperty());
        fieldBio.textProperty().bindBidirectional(viewModel.bioProperty());

        toggleTaskAssign.selectedProperty().bindBidirectional(viewModel.notifyTaskAssignProperty());
        toggleBugReport.selectedProperty().bindBidirectional(viewModel.notifyBugReportProperty());
        toggleMention.selectedProperty().bindBidirectional(viewModel.notifyMentionProperty());


        viewModel.displayNameProperty().addListener((obs, o, n) -> updateAvatarInitials(n));
        viewModel.avatarColorProperty().addListener((obs, o, n) -> applyAvatarColor(n));


        viewModel.getLeaderProjects().addListener(
                (javafx.collections.ListChangeListener<Project>) c -> renderProjectList());


        initDemoIfEmpty();
        renderProjectList();
        updateAvatarInitials(viewModel.displayNameProperty().get());
        applyAvatarColor(viewModel.avatarColorProperty().get());


        activePanel = panelPersonal;
        activeNavBtn = navPersonal;
        List.of(panelNotifications, panelSecurity, panelProjects, panelAppearance)
                .forEach(p -> p.setVisible(false));


        animatePanelIn(panelPersonal);


        renameOverlay.setVisible(false);
        deleteOverlay.setVisible(false);
        renameDialog.setTranslateY(40);
        deleteDialog.setTranslateY(40);
    }


    @FXML
    private void onNavPersonal() {
        switchTo(panelPersonal, navPersonal);
    }

    @FXML
    private void onNavNotifications() {
        switchTo(panelNotifications, navNotifications);
    }

    @FXML
    private void onNavSecurity() {
        switchTo(panelSecurity, navSecurity);
    }

    @FXML
    private void onNavProjects() {
        switchTo(panelProjects, navProjects);
        renderProjectList();
    }

    @FXML
    private void onNavAppearance() {
        switchTo(panelAppearance, navAppearance);
    }

    private void switchTo(AnchorPane target, Button btn) {
        if (activePanel == target) return;


        FadeTransition fadeOut = new FadeTransition(Duration.millis(120), activePanel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        AnchorPane prev = activePanel;
        fadeOut.setOnFinished(e -> {
            prev.setVisible(false);
            target.setVisible(true);
            animatePanelIn(target);
        });
        fadeOut.play();


        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().removeAll("nav-btn-active", "nav-btn");
            activeNavBtn.getStyleClass().add("nav-btn");
        }
        btn.getStyleClass().removeAll("nav-btn", "nav-btn-active");
        btn.getStyleClass().add("nav-btn-active");

        activePanel = target;
        activeNavBtn = btn;
    }

    private void animatePanelIn(AnchorPane panel) {
        panel.setOpacity(0);
        panel.setTranslateX(18);

        FadeTransition fade = new FadeTransition(Duration.millis(220), panel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), panel);
        slide.setFromX(18);
        slide.setToX(0);

        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
    }


    @FXML
    private void onSavePersonal() {
        viewModel.savePersonalInfo();
        flashStatus(personalStatusLabel,
                viewModel.saveSuccessProperty().get(),
                viewModel.statusMessageProperty().get());
        flashStatus(notifStatusLabel,
                viewModel.saveSuccessProperty().get(),
                viewModel.statusMessageProperty().get());
    }

    @FXML
    private void onToggleChanged() {
        // no-op — bindings keep VM in sync; save explicitly
    }

    @FXML
    private void onColorSwatch(javafx.scene.input.MouseEvent e) {
        if (!(e.getSource() instanceof Pane swatch)) return;
        String style = swatch.getStyle();
        int start = style.indexOf('#');
        if (start < 0) return;
        String hex = style.substring(start, start + 7);
        viewModel.avatarColorProperty().set(hex);
        applyAvatarColor(hex);
    }

    private void updateAvatarInitials(String name) {
        if (name == null || name.isBlank()) {
            avatarInitials.setText("?");
            return;
        }
        String[] parts = name.trim().split("\\s+");
        String initials = parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)).toUpperCase() + String.valueOf(parts[1].charAt(0)).toUpperCase()
                : String.valueOf(parts[0].charAt(0)).toUpperCase();
        avatarInitials.setText(initials);


        ScaleTransition bounce = new ScaleTransition(Duration.millis(180), avatarCircle);
        bounce.setFromX(0.92);
        bounce.setFromY(0.92);
        bounce.setToX(1.0);
        bounce.setToY(1.0);
        bounce.setAutoReverse(false);
        bounce.play();
    }

    private void applyAvatarColor(String hex) {
        try {
            Color c = Color.web(hex);
            avatarCircle.setFill(c);
            avatarInitials.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-family: 'System Bold';");
        } catch (Exception ignored) {
        }
    }


    @FXML
    private void onChangePassword() {
        String cur = fieldCurrentPwd.getText();
        String newPwd = fieldNewPwd.getText();
        String confirm = fieldConfirmPwd.getText();

        if (cur.isBlank() || newPwd.isBlank()) {
            flashStatus(securityStatusLabel, false, "Please fill in all password fields.");
            return;
        }
        if (!newPwd.equals(confirm)) {
            flashStatus(securityStatusLabel, false, "Passwords do not match.");
            return;
        }
        if (newPwd.length() < 6) {
            flashStatus(securityStatusLabel, false, "Password must be at least 6 characters.");
            return;
        }
        // TODO: wire to AuthService
        fieldCurrentPwd.clear();
        fieldNewPwd.clear();
        fieldConfirmPwd.clear();
        flashStatus(securityStatusLabel, true, "Password updated successfully!");
    }


    private void renderProjectList() {
        projectListVBox.getChildren().clear();
        List<Project> projects = viewModel.getLeaderProjects();

        projectCountChip.setText(projects.size() + " project" + (projects.size() == 1 ? "" : "s"));

        if (projects.isEmpty()) {
            Label empty = new Label("You don't lead any projects yet.");
            empty.getStyleClass().add("field-hint");
            empty.setPadding(new Insets(16, 0, 0, 0));
            projectListVBox.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            AnchorPane row = buildProjectRow(projects.get(i));
            row.setOpacity(0);
            row.setTranslateY(12);
            projectListVBox.getChildren().add(row);


            int delay = i * 60;
            FadeTransition ft = new FadeTransition(Duration.millis(250), row);
            ft.setDelay(Duration.millis(delay));
            ft.setFromValue(0);
            ft.setToValue(1);

            TranslateTransition tt = new TranslateTransition(Duration.millis(250), row);
            tt.setDelay(Duration.millis(delay));
            tt.setFromY(12);
            tt.setToY(0);

            new ParallelTransition(ft, tt).play();
        }
    }

    private AnchorPane buildProjectRow(Project project) {

        HBox row = new HBox(12);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getStyleClass().add("project-row-card");
        row.setPadding(new Insets(16, 20, 16, 20));
        HBox.setHgrow(row, Priority.ALWAYS);


        Pane dot = new Pane();
        dot.getStyleClass().add("project-row-dot");
        dot.setPrefSize(8, 8);
        dot.setMinSize(8, 8);


        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(project.getName());
        name.getStyleClass().add("project-row-name");
        Label meta = new Label("Created " + nvl(project.getCreatedAt(), "—") + "  ·  ID: " + project.getId());
        meta.getStyleClass().add("project-row-meta");
        info.getChildren().addAll(name, meta);


        Button renameBtn = new Button("✏ Rename");
        renameBtn.getStyleClass().add("project-action-btn");
        renameBtn.setOnAction(e -> openRenameDialog(project));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.getStyleClass().add("project-delete-btn");
        deleteBtn.setOnAction(e -> openDeleteDialog(project));

        row.getChildren().addAll(dot, info, renameBtn, deleteBtn);

        AnchorPane wrap = new AnchorPane(row);
        AnchorPane.setTopAnchor(row, 0.0);
        AnchorPane.setBottomAnchor(row, 0.0);
        AnchorPane.setLeftAnchor(row, 0.0);
        AnchorPane.setRightAnchor(row, 0.0);
        return wrap;
    }


    private void openRenameDialog(Project project) {
        pendingRenameProject = project;
        renameField.setText(project.getName());
        renameOverlay.setVisible(true);
        renameOverlay.setOpacity(0);

        FadeTransition bgFade = new FadeTransition(Duration.millis(180), renameOverlay);
        bgFade.setToValue(1);

        renameDialog.setTranslateY(30);
        TranslateTransition dlgSlide = new TranslateTransition(Duration.millis(240), renameDialog);
        dlgSlide.setToY(0);

        new ParallelTransition(bgFade, dlgSlide).play();
        renameField.requestFocus();
    }

    @FXML
    private void onConfirmRename() {
        if (pendingRenameProject == null) return;
        String newName = renameField.getText().trim();
        if (newName.isBlank()) {
            flashStatus(projectsStatusLabel, false, "Project name cannot be empty.");
            closeRenameDialog();
            return;
        }
        viewModel.renameProject(pendingRenameProject, newName);
        flashStatus(projectsStatusLabel,
                viewModel.saveSuccessProperty().get(),
                viewModel.statusMessageProperty().get());
        pendingRenameProject = null;
        closeRenameDialog();
    }

    @FXML
    private void onCancelRename() {
        pendingRenameProject = null;
        closeRenameDialog();
    }

    private void closeRenameDialog() {
        FadeTransition ft = new FadeTransition(Duration.millis(160), renameOverlay);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            renameOverlay.setVisible(false);
            renameDialog.setTranslateY(40);
        });
        ft.play();
    }


    private void openDeleteDialog(Project project) {
        pendingDeleteProject = project;
        deleteConfirmLabel.setText(
                "Are you sure you want to delete \"" + project.getName() + "\"?\n" +
                        "This will permanently remove the project and all its tasks and bugs.");
        deleteOverlay.setVisible(true);
        deleteOverlay.setOpacity(0);

        FadeTransition bgFade = new FadeTransition(Duration.millis(180), deleteOverlay);
        bgFade.setToValue(1);

        deleteDialog.setTranslateY(30);
        TranslateTransition dlgSlide = new TranslateTransition(Duration.millis(240), deleteDialog);
        dlgSlide.setToY(0);

        new ParallelTransition(bgFade, dlgSlide).play();
    }

    @FXML
    private void onConfirmDelete() {
        if (pendingDeleteProject == null) return;
        viewModel.deleteProject(pendingDeleteProject);
        flashStatus(projectsStatusLabel,
                viewModel.saveSuccessProperty().get(),
                viewModel.statusMessageProperty().get());
        pendingDeleteProject = null;
        closeDeleteDialog();
    }

    @FXML
    private void onCancelDelete() {
        pendingDeleteProject = null;
        closeDeleteDialog();
    }

    private void closeDeleteDialog() {
        FadeTransition ft = new FadeTransition(Duration.millis(160), deleteOverlay);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            deleteOverlay.setVisible(false);
            deleteDialog.setTranslateY(40);
        });
        ft.play();
    }


    @FXML
    private void onSelectDark() {

        ScaleTransition bounce = new ScaleTransition(Duration.millis(140), themeCardDark);
        bounce.setFromX(0.96);
        bounce.setFromY(0.96);
        bounce.setToX(1.0);
        bounce.setToY(1.0);
        bounce.play();
    }


    private void flashStatus(Label label, boolean success, String msg) {
        label.setText(msg);
        label.getStyleClass().removeAll("status-idle", "status-ok", "status-error");
        label.getStyleClass().add(success ? "status-ok" : "status-error");
        label.setOpacity(1.0);

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(500), label);
            ft.setToValue(0);
            ft.setOnFinished(ev -> {
                label.setText("");
                label.getStyleClass().removeAll("status-ok", "status-error");
                label.getStyleClass().add("status-idle");
                label.setOpacity(1.0);
            });
            ft.play();
        });
        pause.play();
    }


    private void initDemoIfEmpty() {
        if (viewModel.displayNameProperty().get().isBlank()) {
            viewModel.displayNameProperty().set("Bushra Rahman");
            viewModel.emailProperty().set("bushra@flora.app");
            viewModel.bioProperty().set("Project lead & developer at Flora.");
        }
    }

    private String nvl(String v, String fallback) {
        return (v != null && !v.isBlank()) ? v : fallback;
    }
}