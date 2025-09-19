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

public class RegistrationController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField userNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox checkBoxTA;
    @FXML private Button registerButton;
    @FXML private Button backtoLoginButton;
    @FXML private ComboBox<String> roleCombo;

    private ApiClient api;

    public void setApi(ApiClient api){ this.api = api; }



    @FXML
    public void onRegister(ActionEvent event) {
        registerButton.setDisable(true);

        if (api == null) {
            showError("Internal error: API client not set (open this screen from Login).");
            registerButton.setDisable(false);
            return;
        }

        final String firstName = firstNameField.getText().trim();
        final String lastName  = lastNameField.getText().trim();
        final String email     = emailField.getText().trim();
        final String password  = passwordField.getText();
        final String confirm   = confirmPasswordField.getText();
        final String role      = (roleCombo != null && roleCombo.getValue()!=null)
                ? roleCombo.getValue().trim().toUpperCase()
                : "PATIENT";

        // validations...
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all required fields.");
            registerButton.setDisable(false);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            registerButton.setDisable(false);
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            registerButton.setDisable(false);
            return;
        }
        if (!checkBoxTA.isSelected()) {
            showError("Please accept the Terms & Conditions.");
            registerButton.setDisable(false);
            return;
        }

        final String fullName = firstName + " " + lastName;

        new Thread(() -> {
            try {
                // 1) register with role
                api.register(fullName, email, password, role);

                // 2) auto-login
                api.login(email, password);

                // 3) go to dashboard
                javafx.application.Platform.runLater(() -> {
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

                        Stage stage = (Stage) registerButton.getScene().getWindow();
                        stage.setTitle(api.isDoctor() ? "Doctor Dashboard" : "Patient Dashboard");
                        stage.setScene(new Scene(root, 1060, 720));
                    } catch (Exception uiEx) {
                        showError("Navigation error: " + uiEx.getMessage());
                        registerButton.setDisable(false);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace(); // console
                javafx.application.Platform.runLater(() -> {
                    String msg = (ex.getMessage()==null || ex.getMessage().isBlank())
                            ? ex.getClass().getSimpleName() : ex.getMessage();
                    showError("Registration failed: " + msg);
                    registerButton.setDisable(false);
                });
            }
        }).start();
    }




    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
    private void showInfo(String header, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(header);
        a.showAndWait();
    }


    @FXML
    public void onBackToLogin(ActionEvent event) {
        Stage stage = (Stage) backtoLoginButton.getScene().getWindow();
        stage.close();
    }
}

