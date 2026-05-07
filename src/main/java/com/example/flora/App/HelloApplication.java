package com.example.flora.App;

import com.example.flora.Core.Constants.Path;
import com.example.flora.Core.Constants.WindowConstants;
import com.example.flora.Core.DI.AppContainer;
import com.example.flora.Features.Auth.UI.LoginUI_Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        AppContainer appContainer = new AppContainer();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(Path.LOGIN));

        fxmlLoader.setControllerFactory(e->new LoginUI_Controller(appContainer.getAuthViewModel(),appContainer));

        Scene scene = new Scene(fxmlLoader.load(),WindowConstants.width,WindowConstants.height);
        stage.setTitle("F.L.O.R.A");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
