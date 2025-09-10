package za.ac.cput.frontendjavafx.controllers;

import javafx.fxml.FXML;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;
import java.util.List;

    public class DoctorController {

        // ===== Availability Section =====
        @FXML
        private ComboBox<Integer> dayOfWeekBox; // 1..7
        @FXML private TextField startField;           // "09:00"
        @FXML private TextField endField;             // "13:00"
        @FXML private TextField slotMinutesField;     // "20"
        @FXML private Button saveAvailabilityBtn;

        // ===== Time Off Section =====
        @FXML private DatePicker timeOffDate;
        @FXML private TextField timeOffStartField;    // "09:00"
        @FXML private TextField timeOffEndField;      // "12:00"
        @FXML private TextField timeOffReasonField;
        @FXML private Button addTimeOffBtn;

        // ===== Dashboard UI Stats =====
        @FXML private Label totalAppointmentsLabel;
        @FXML private Label completedAppointmentsLabel;
        @FXML private Label cancelledAppointmentsLabel;
        @FXML private Label followUpsLabel;

        @FXML private ListView<String> patientsByDepartmentList;
        @FXML private ListView<String> latestAppointmentsList;

        @FXML private Label statusLabel;

        private ApiClient api;

        public void setApi(ApiClient api){ this.api = api; }

        @FXML
        public void initialize() {
            // ComboBox setup
            for (int i = 1; i <= 7; i++) dayOfWeekBox.getItems().add(i);
            dayOfWeekBox.getSelectionModel().select(Integer.valueOf(1));

            slotMinutesField.setText("20");
            timeOffDate.setValue(LocalDate.now().plusDays(1));

            // Load initial dashboard data
            loadDashboardStats();
            loadLatestAppointments();
            loadPatientsByDepartment();
        }

        // ===== Availability =====
        @FXML
        public void onSaveAvailability() {
            try {
                int d = dayOfWeekBox.getValue();
                String s = startField.getText().trim();
                String e = endField.getText().trim();
                int m = Integer.parseInt(slotMinutesField.getText().trim());
                List<ApiClient.Availability> list = List.of(new ApiClient.Availability(d, s, e, m));

                statusLabel.setText("Saving availability...");
                new Thread(() -> {
                    try {
                        api.setWeeklyAvailability(list);
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Availability saved."));
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> statusLabel.setText("Save failed: " + ex.getMessage()));
                    }
                }).start();
            } catch (Exception ex) {
                statusLabel.setText("Input error: " + ex.getMessage());
            }
        }

        // ===== Time Off =====
        @FXML
        public void onAddTimeOff() {
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
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Time off failed: " + ex.getMessage()));
                }
            }).start();
        }

        // ===== Dashboard Loading Methods =====
        private void loadDashboardStats() {
            new Thread(() -> {
                try {
                  //====
                    ApiClient.DashboardStats stats = api.getDoctorDashboardStats();
                    javafx.application.Platform.runLater(() -> {
                        totalAppointmentsLabel.setText(String.valueOf(stats.totalAppointments));
                        completedAppointmentsLabel.setText(String.valueOf(stats.completedAppointments));
                        cancelledAppointmentsLabel.setText(String.valueOf(stats.cancelledAppointments));
                        followUpsLabel.setText(String.valueOf(stats.followUps));
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Failed to load stats: " + ex.getMessage()));
                }
            }).start();
        }

        private void loadLatestAppointments() {
            new Thread(() -> {
                try {
                    List<String> latest = api.getLatestAppointments();
                    javafx.application.Platform.runLater(() -> {
                        latestAppointmentsList.getItems().setAll(latest);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Failed to load appointments."));
                }
            }).start();
        }

        private void loadPatientsByDepartment() {
            new Thread(() -> {
                try {
                    List<String> patients = api.getPatientsByDepartment();
                    javafx.application.Platform.runLater(() -> {
                        patientsByDepartmentList.getItems().setAll(patients);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> statusLabel.setText("Failed to load patients."));
                }
            }).start();
        }
    }

}
