module com.example.flora {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Only open UI packages (FXML needs reflection)
    opens com.example.flora.Features.Auth.UI
            to javafx.fxml;


    // Export only what’s needed
    exports com.example.flora.App;
}