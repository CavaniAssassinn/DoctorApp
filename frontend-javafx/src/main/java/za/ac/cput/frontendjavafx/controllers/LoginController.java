package za.ac.cput.frontendjavafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import za.ac.cput.frontendjavafx.MainApp;
import za.ac.cput.frontendjavafx.api.ApiClient;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
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
                // log to console
                System.err.println("Login failed");
                ex.printStackTrace();

                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Login failed: " + ex.getMessage());
                    loginBtn.setDisable(false);
                });
            }
        }).start();
    }

    /** Open the registration dialog (single source of truth; point your FXML button to #onRegister). */
    @FXML
    private void onRegister(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/za/ac/cput/frontendjavafx/registration.fxml"));
            Parent root = loader.load();

            // share the same ApiClient instance with the registration controller
            RegistrationController rc = loader.getController();
            rc.setApi(api);

            Stage owner = (Stage) registerBtn.getScene().getWindow();
            Stage dialog = new Stage();
            dialog.setTitle("Create Account");
            dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setScene(new Scene(root));
            dialog.show();
        } catch (Exception ex) {
            System.err.println("Failed to load registration.fxml");
            ex.printStackTrace();

            statusLabel.setText("UI error: " + ex.getMessage());
        }
    }

    private void openDashboard(){
        try {
            String fxml = api.isDoctor()
                    ? "/za/ac/cput/frontendjavafx/doctorDashboard.fxml"
                    : "/za/ac/cput/frontendjavafx/patientDashboard.fxml";

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
            System.err.println("Failed to load dashboard FXML");
            ex.printStackTrace();
            System.err.println("Failed to load dashboard FXML");
            ex.printStackTrace();   // ← this reveals the exact resource that failed
            statusLabel.setText("UI error: " + ex.getMessage());
            statusLabel.setText("UI error: " + ex.getMessage());
        }
    }
}
