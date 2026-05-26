package com.example.flora.Features.Project.UI;

import com.example.flora.Features.Project.ViewModel.ProjectDetailViewModel;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class MemberSectionHandler {

    private final ProjectDetailViewModel viewModel;


    private TextField inviteSearchField;
    private Label inviteFeedback;
    private VBox memberList;

    public MemberSectionHandler(ProjectDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }


    public void bind(TextField inviteSearchField, Label inviteFeedback, VBox memberList) {
        this.inviteSearchField = inviteSearchField;
        this.inviteFeedback = inviteFeedback;
        this.memberList = memberList;
    }


    public void sendInvite(ActionEvent event) {
        String username = inviteSearchField.getText().trim();
        boolean ok = viewModel.sendInvite(username);
        inviteFeedback.setText(ok ? "✔ Invite sent to @" + username : "✖ Could not send invite.");
        inviteFeedback.getStyleClass().removeAll("feedback-ok", "feedback-err");
        inviteFeedback.getStyleClass().add(ok ? "feedback-ok" : "feedback-err");
        inviteSearchField.clear();

        PauseTransition w = new PauseTransition(Duration.seconds(2.5));
        w.setOnFinished(e -> inviteFeedback.setText(""));
        w.play();
    }


    public void renderMembers() {
        memberList.getChildren().clear();
        String leaderId = viewModel.getCurrentProject().getOwnerId();
        for (String username : viewModel.getMembers()) {
            memberList.getChildren().add(buildMemberRow(username, username.equals(leaderId)));
        }
    }


    private HBox buildMemberRow(String username, boolean isProjectLeader) {
        HBox row = new HBox(12);
        row.getStyleClass().add("detail-list-row");
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("member-avatar");
        avatar.setPrefSize(36, 36);
        avatar.setAlignment(Pos.CENTER);

        Label name = new Label("@" + username);
        name.setTextFill(Color.web("#EDE9F6"));
        name.getStyleClass().add("row-title");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(isProjectLeader ? "LEADER" : "MEMBER");
        badge.getStyleClass().add(isProjectLeader ? "role-badge-leader" : "role-badge-member");
        badge.setPadding(new Insets(3, 8, 3, 8));

        row.getChildren().addAll(avatar, name, spacer, badge);


        if (viewModel.isLeader() && !username.equals(viewModel.getCurrentUserId())) {
            Button removeBtn = new Button("Remove");
            removeBtn.getStyleClass().add("slide-close-btn");
            removeBtn.setOnAction(e -> viewModel.removeMember(username));
            row.getChildren().add(removeBtn);
        }
        return row;
    }
}