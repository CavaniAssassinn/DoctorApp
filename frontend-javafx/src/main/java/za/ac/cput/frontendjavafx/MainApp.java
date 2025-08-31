package za.ac.cput.frontendjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static String BASE_URL = "http://localhost:8080/api/v1";

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/za/ac/cput/frontendjavafx/login.fxml")
        );
        stage.setTitle("Doctor Booking — Login");
        stage.setScene(new Scene(root, 860, 560));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
