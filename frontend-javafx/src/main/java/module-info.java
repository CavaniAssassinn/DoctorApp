module za.ac.cput.frontendjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    // Jackson modules
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    // FXML reflection access
    opens za.ac.cput.frontendjavafx.controllers to javafx.fxml;
    opens za.ac.cput.frontendjavafx to javafx.fxml;

    exports za.ac.cput.frontendjavafx;
}
