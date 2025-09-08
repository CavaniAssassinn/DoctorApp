package za.ac.cput.frontendjavafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    @FXML private TextField confirmPasswordField;
    @FXML private CheckBox checkBoxTA;
    @FXML private Button registerButton;
    @FXML private Button backtoLoginButton;

    private ApiClient api;

    public void setApi(ApiClient api){ this.api = api; }

    @FXML
    public void onRegister(ActionEvent event) {
        registerButton.setDisable(true);

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String userName = userNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneNumberField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        boolean isTA = checkBoxTA.isSelected();

        // Basic validation
        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            // No status label, so just re-enable button and return
            registerButton.setDisable(false);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            registerButton.setDisable(false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerButton.setDisable(false);
            return;
        }

        new Thread(() -> {
            try {
                // Placeholder for registration API call
                api.login(email, password);

                javafx.application.Platform.runLater(() -> {
                    // Close registration window after success
                    Stage stage = (Stage) registerButton.getScene().getWindow();
                    stage.close();
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    // On failure, re-enable button
                    registerButton.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    public void onBackToLogin(ActionEvent event) {
        Stage stage = (Stage) backtoLoginButton.getScene().getWindow();
        stage.close();
    }
}

