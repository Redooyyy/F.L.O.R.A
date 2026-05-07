module com.example.flora {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires eu.hansolo.tilesfx;

    // Only open UI packages (FXML needs reflection)
    opens com.example.flora.Features.Auth.UI to javafx.fxml;
    opens com.example.flora.Features.test.ui to javafx.fxml;
    opens com.example.flora.Features.Home.UI to javafx.fxml;
    opens com.example.flora.Features.Home.UI.Cards to javafx.fxml;
    opens com.example.flora.Features.Project.UI to javafx.fxml;
    opens com.example.flora.Features.Project.UI.Card to javafx.fxml;
    opens com.example.flora.Features.Overview.UI to javafx.fxml;

    // Export only what’s needed
    exports com.example.flora.App;
}