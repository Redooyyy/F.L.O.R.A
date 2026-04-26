package com.example.flora.Core.Transition;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SceneTransition {

    private final Stage stage;

    public SceneTransition(Stage stage) {
        this.stage = stage;
    }

    // LOGIN → OVERVIEW (soft entrance animation)
    public void switchFromLogin(String toFxml) throws IOException {

        Parent newRoot = FXMLLoader.load(getClass().getResource(toFxml));

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
}