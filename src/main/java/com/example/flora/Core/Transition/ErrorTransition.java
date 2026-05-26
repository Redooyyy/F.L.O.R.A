package com.example.flora.Core.Transition;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public final class ErrorTransition {
    private static final double ENTER_MS = 320;
    private static final double HOLD_S = 2.4;
    private static final double EXIT_MS = 380;


    private static final double SHAKE_BY = 9;
    private static final double SHAKE_MS = 52;
    private static final int SHAKE_N = 6;


    private static final String RED_BG = "rgba(220,53,69,0.93)";
    private static final String GREEN_BG = "rgba(32,178,90,0.93)";
    private static final String INFO_BG = "rgba(59,130,246,0.93)";
    private static final String WARN_BG = "rgba(245,158,11,0.93)";

    private ErrorTransition() {
    }


    public enum ToastType {ERROR, SUCCESS, INFO, WARNING}


    public static void showToast(String message, Pane root, ToastType type) {
        Label toast = new Label(message);
        toast.setStyle(toastStyle(type));
        toast.setAlignment(Pos.CENTER);
        toast.setPadding(new Insets(11, 28, 11, 28));
        toast.setOpacity(0);
        toast.setWrapText(false);


        toast.setLayoutX(root.getWidth() / 2 - 100);
        toast.setLayoutY(root.getHeight() - 560);
        root.getChildren().add(toast);


        FadeTransition fadeIn = new FadeTransition(Duration.millis(ENTER_MS), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(ENTER_MS), toast);
        slideIn.setFromY(-18);
        slideIn.setToY(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        PauseTransition hold = new PauseTransition(Duration.seconds(HOLD_S));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(EXIT_MS), toast);
        fadeOut.setToValue(0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(EXIT_MS), toast);
        slideOut.setByY(-12);
        slideOut.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition exit = new ParallelTransition(fadeOut, slideOut);
        exit.setOnFinished(e -> root.getChildren().remove(toast));

        new SequentialTransition(
                new ParallelTransition(fadeIn, slideIn),
                hold,
                exit
        ).play();
    }

    public static void showError(String message, Pane root) {
        showToast(message, root, ToastType.ERROR);
    }


    public static void showSuccess(String message, Pane root) {
        showToast(message, root, ToastType.SUCCESS);
    }


    public static void shakeField(TextField field) {
        String st = field.getStyle();
        markFieldError(field);

        TranslateTransition shake = new TranslateTransition(Duration.millis(SHAKE_MS), field);
        shake.setFromX(0);
        shake.setByX(SHAKE_BY);
        shake.setCycleCount(SHAKE_N);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> {
            field.setTranslateX(0);
            field.setStyle(st);
        });
        shake.play();


        field.textProperty().addListener((obs, o, n) -> clearFieldError(field));
    }

    public static void markFieldError(TextField field) {
        field.setStyle(
                "-fx-border-color: #dc3545;" +
                        "-fx-border-width: 1.8;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
    }

    public static void clearFieldError(TextField field) {
        field.setStyle(field.getStyle()); // -_- a useless function just like me -_-
    }

    public static void clearAllErrors(TextField... fields) {
        for (TextField f : fields) clearFieldError(f);
    }


    public static void showInlineError(Label label, String message) {
        label.setText(message);
        label.setStyle(
                "-fx-text-fill: #dc3545;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 3 0 0 4;"
        );
        label.setVisible(true);
        label.setManaged(true);

        FadeTransition fade = new FadeTransition(Duration.millis(200), label);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(200), label);
        slide.setFromY(-5);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();
    }

    public static void clearInlineError(Label label) {
        if (label == null) return;
        label.setText("");
        label.setVisible(false);
        label.setManaged(false);
    }


    public static void failField(TextField field, String message, Pane root) {
        shakeField(field);
        showError(message, root);
    }

    public static void failFields(String message, Pane root, TextField... fields) {
        for (TextField f : fields) shakeField(f);
        showError(message, root);
    }


    private static String toastStyle(ToastType type) {
        String bg = switch (type) {
            case ERROR -> RED_BG;
            case SUCCESS -> GREEN_BG;
            case INFO -> INFO_BG;
            case WARNING -> WARN_BG;
        };
        String shadow = switch (type) {
            case ERROR -> "rgba(220,53,69,0.45)";
            case SUCCESS -> "rgba(32,178,90,0.4)";
            case INFO -> "rgba(59,130,246,0.4)";
            case WARNING -> "rgba(245,158,11,0.4)";
        };
        String emoji = switch (type) {
            case ERROR -> "✕  ";
            case SUCCESS -> "✓  ";
            case INFO -> "ℹ  ";
            case WARNING -> "⚠  ";
        };

        return "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13.5px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 30;" +
                "-fx-effect: dropshadow(gaussian, " + shadow + ", 18, 0.1, 0, 5);";
    }
}