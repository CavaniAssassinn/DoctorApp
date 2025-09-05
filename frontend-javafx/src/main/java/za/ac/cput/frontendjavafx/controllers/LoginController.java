package za.ac.cput.frontendjavafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import za.ac.cput.frontendjavafx.MainApp;
import za.ac.cput.frontendjavafx.api.ApiClient;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Label statusLabel;

    private final ApiClient api = new ApiClient(MainApp.BASE_URL);

    @FXML
    public void onLogin(ActionEvent e){
        statusLabel.setText("Logging in...");
        loginBtn.setDisable(true);
        new Thread(() -> {
            try {
                api.login(emailField.getText(), passwordField.getText());
                javafx.application.Platform.runLater(this::openDashboard);
            } catch (Exception ex){
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Login failed: " + ex.getMessage());
                    loginBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void openDashboard(){
        try {
            String fxml = api.isDoctor()
                    ? "/za/ac/cput/frontendjavafx/doctor-dashboard.fxml"
                    : "/za/ac/cput/frontendjavafx/patient-dashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            if (api.isDoctor()) {
                DoctorDashboardController c = loader.getController();
                c.setApi(api);
            } else {
                PatientDashboardController c = loader.getController();
                c.setApi(api);
            }

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle(api.isDoctor() ? "Doctor Dashboard" : "Patient Dashboard");
            stage.setScene(new Scene(root, 1060, 720));
        } catch (Exception ex){
            statusLabel.setText("UI error: " + ex.getMessage());
        }
    }
}
