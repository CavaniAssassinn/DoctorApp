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
                javafx.application.Platform.runLater(this::openSearch);
            } catch (Exception ex){
                javafx.application.Platform.runLater(() -> {
                    statusLabel.setText("Login failed: " + ex.getMessage());
                    loginBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void openSearch(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/za/ac/cput/frontendjavafx/search.fxml"));
            Parent root = loader.load();
            SearchController controller = loader.getController();
            controller.setApi(api);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setTitle("Doctor Booking — Search & Book");
            stage.setScene(new Scene(root, 1024, 680));
        } catch (Exception ex){
            statusLabel.setText("UI error: " + ex.getMessage());
        }
    }
}
