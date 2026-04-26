package com.example.flora.App;

import com.example.flora.Core.Constants.WindowConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/Home/UI/HomeUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),WindowConstants.width,WindowConstants.height);
        stage.setTitle("Hello!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
