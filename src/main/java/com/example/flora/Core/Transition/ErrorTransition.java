package com.example.flora.Core.Transition;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ErrorTransition {
    public static void showToast(String message, Pane root, boolean isError) {
        Label toast = new Label(message);
        String bgColor = isError ? "#e74c3c" : "#2ecc71";
        toast.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; " +
                "-fx-padding: 10 20; -fx-background-radius: 10;");


        toast.setLayoutX(root.getWidth() / 2 - 100);
        toast.setLayoutY(root.getHeight() - 60);
        root.getChildren().add(toast);

        FadeTransition fade = new FadeTransition(Duration.seconds(2), toast);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(1.5));
        fade.setOnFinished(e -> root.getChildren().remove(toast));
        fade.play();
    }
}
