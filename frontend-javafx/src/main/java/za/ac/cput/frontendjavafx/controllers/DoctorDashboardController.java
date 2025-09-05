package za.ac.cput.frontendjavafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;

public class DoctorDashboardController {
    @FXML private ComboBox<Integer> dayOfWeekBox; // 1..7
    @FXML private TextField startField;           // "09:00"
    @FXML private TextField endField;             // "13:00"
    @FXML private TextField slotMinutesField;     // "20"
    @FXML private Button saveAvailabilityBtn;

    @FXML private DatePicker timeOffDate;
    @FXML private TextField timeOffStartField;    // "09:00"
    @FXML private TextField timeOffEndField;      // "12:00"
    @FXML private TextField timeOffReasonField;
    @FXML private Button addTimeOffBtn;

    @FXML private Label statusLabel;

    private ApiClient api;

    public void setApi(ApiClient api){ this.api = api; }

    @FXML
    public void initialize(){
        for (int i = 1; i <= 7; i++) dayOfWeekBox.getItems().add(i);
        dayOfWeekBox.getSelectionModel().select(Integer.valueOf(1));
        slotMinutesField.setText("20");
        timeOffDate.setValue(LocalDate.now().plusDays(1));
    }

    @FXML
    public void onSaveAvailability(){
        try {
            int d = dayOfWeekBox.getValue();
            String s = startField.getText().trim();
            String e = endField.getText().trim();
            int m = Integer.parseInt(slotMinutesField.getText().trim());
            java.util.List<ApiClient.Availability> list = java.util.List.of(new ApiClient.Availability(d, s, e, m));

            statusLabel.setText("Saving availability...");
            new Thread(() -> {
                try {
                    api.setWeeklyAvailability(list);
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Availability saved."));
                } catch (Exception ex){
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Save failed: " + ex.getMessage()));
                }
            }).start();
        } catch (Exception ex){
            statusLabel.setText("Input error: " + ex.getMessage());
        }
    }

    @FXML
    public void onAddTimeOff(){
        LocalDate d = timeOffDate.getValue();
        if (d == null) { statusLabel.setText("Pick a date"); return; }
        String startIso = d.toString() + "T" + timeOffStartField.getText().trim();
        String endIso   = d.toString() + "T" + timeOffEndField.getText().trim();
        String reason   = timeOffReasonField.getText();

        statusLabel.setText("Adding time off...");
        new Thread(() -> {
            try {
                api.addTimeOff(startIso, endIso, reason);
                javafx.application.Platform.runLater(() -> statusLabel.setText("Time off added."));
            } catch (Exception ex){
                javafx.application.Platform.runLater(() -> statusLabel.setText("Time off failed: " + ex.getMessage()));
            }
        }).start();
    }
}
