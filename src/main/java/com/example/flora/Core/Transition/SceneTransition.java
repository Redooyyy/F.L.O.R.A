package com.example.flora.Core.Transition;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;

public class SceneTransition {

    private final Stage stage;

    public SceneTransition(Stage stage) {
        this.stage = stage;
    }

    // LOGIN → OVERVIEW (soft entrance animation)
    public void switchFromLogin(String toFxml,Callback<Class<?>, Object> controllerFactory) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(toFxml));
        loader.setControllerFactory(controllerFactory);
        Parent newRoot = loader.load();
        Scene scene = stage.getScene();
        Parent oldRoot = scene.getRoot();

        newRoot.setOpacity(0);
        newRoot.setScaleX(0.95);
        newRoot.setScaleY(0.95);

        // OUT (login disappears softly)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldRoot);
        fadeOut.setToValue(0);

        ParallelTransition out = new ParallelTransition(fadeOut);

        out.setOnFinished(e -> {

            scene.setRoot(newRoot);

            // IN (app enters smoothly)
            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(350), newRoot);
            scaleIn.setFromX(0.95);
            scaleIn.setFromY(0.95);
            scaleIn.setToX(1);
            scaleIn.setToY(1);

            new ParallelTransition(fadeIn, scaleIn).play();
        });

        out.play();
    }


    // NORMAL NAVIGATION (Overview → Profile etc.)
    public void switchScene(String toFxml) throws IOException {

        Parent newRoot = FXMLLoader.load(getClass().getResource(toFxml));

        Scene scene = stage.getScene();
        Parent oldRoot = scene.getRoot();

        newRoot.setOpacity(0);
        newRoot.setTranslateX(40);

        // OUT (slide left)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldRoot);
        fadeOut.setToValue(0);

        TranslateTransition moveOut = new TranslateTransition(Duration.millis(200), oldRoot);
        moveOut.setToX(-40);

        ParallelTransition out = new ParallelTransition(fadeOut, moveOut);

        out.setOnFinished(e -> {

            scene.setRoot(newRoot);

            // IN (slide in)
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition moveIn = new TranslateTransition(Duration.millis(250), newRoot);
            moveIn.setFromX(40);
            moveIn.setToX(0);

            new ParallelTransition(fadeIn, moveIn).play();
        });

        out.play();
    }

    public void loadingContent(AnchorPane contentPane, AnchorPane newPane) {
        newPane.setOpacity(0);
        newPane.setTranslateX(40);

        AnchorPane.setBottomAnchor(newPane, 0.0);
        AnchorPane.setTopAnchor(newPane, 0.0);
        AnchorPane.setLeftAnchor(newPane, 0.0);
        AnchorPane.setRightAnchor(newPane, 0.0);


        AnchorPane oldPane = contentPane.getChildren().isEmpty()
                ? null
                : (AnchorPane) contentPane.getChildren().get(0);

        if (oldPane != null) {

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldPane);
            fadeOut.setToValue(0);

            TranslateTransition moveOut = new TranslateTransition(Duration.millis(200), oldPane);
            moveOut.setToX(-40);

            ParallelTransition out = new ParallelTransition(fadeOut, moveOut);
            out.setOnFinished(e -> {
                contentPane.getChildren().clear();
                contentPane.getChildren().add(newPane);
                playFadeIn(newPane);
            });
            out.play();
        } else {

            contentPane.getChildren().add(newPane);
            playFadeIn(newPane);
        }
    }

    public void switchToLogin(String toFxml, Callback<Class<?>, Object> controllerFactory) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(toFxml));
        loader.setControllerFactory(controllerFactory);
        Parent newRoot = loader.load();
        Scene scene = stage.getScene();
        Parent oldRoot = scene.getRoot();

        newRoot.setOpacity(0);
        newRoot.setScaleX(1.05);
        newRoot.setScaleY(1.05);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), oldRoot);
        fadeOut.setToValue(0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), oldRoot);
        scaleOut.setToX(0.95);
        scaleOut.setToY(0.95);

        ParallelTransition out = new ParallelTransition(fadeOut, scaleOut);
        out.setOnFinished(e -> {
            scene.setRoot(newRoot);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), newRoot);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(350), newRoot);
            scaleIn.setFromX(1.05);
            scaleIn.setFromY(1.05);
            scaleIn.setToX(1);
            scaleIn.setToY(1);

            new ParallelTransition(fadeIn, scaleIn).play();
        });

        out.play();
    }

    // Error / mismatch shaking effect
    public void shakeAndHighlight(Node node) {
        String def = node.getStyle();
        node.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 0;");
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();

        tt.setOnFinished(e -> {
            node.setStyle(def);
        });
    }

    private void playFadeIn(AnchorPane pane) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition moveIn = new TranslateTransition(Duration.millis(250), pane);
        moveIn.setFromX(40);
        moveIn.setToX(0);

        new ParallelTransition(fadeIn, moveIn).play();
    }
}