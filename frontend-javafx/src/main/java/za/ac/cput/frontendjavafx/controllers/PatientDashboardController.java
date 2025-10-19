package za.ac.cput.frontendjavafx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;
import java.util.*;

public class PatientDashboardController {

    private ApiClient api;

    // --- UI nodes used by your booking flow (ensure they exist in FXML) ---
    @FXML private Label statusLabel;                 // <-- add this to FXML too
    @FXML private ListView<String> doctorsList;      // list of doctors (displayed)
    @FXML private ListView<String> slotsList;        // list of time slots (displayed)
    @FXML private TextArea reasonArea;               // booking reason
    @FXML private DatePicker datePicker;             // date picker
    @FXML private Label welcomeLabel;
    @FXML private Label patName;

    // keep the raw doctor objects so we can pull the id later
    private List<Map<String,Object>> doctors = new ArrayList<>();

    public void setApi(ApiClient api) {
        this.api = api;
        initialize();
    }

    private void initialize() {  // Make it a regular method for simplicity
        if (api != null) {
            String userName = api.getFullName();
            if (userName != null && !userName.isEmpty()) {
                welcomeLabel.setText("Welcome, " + userName);
                patName.setText(userName);
            } else {
                welcomeLabel.setText("Welcome, Guest");
                patName.setText("Guest");
            }
        }
    }

    /* ------------------- actions (examples) ------------------- */
    @FXML
    public void onRefreshDoctors() { onSearch(); }

    @FXML
    public void onSearch() {
        setStatus("Searching doctors...");
        new Thread(() -> {
            try {
                // Plug in your real filters if you have search fields
                doctors = api.searchDoctors("", "", "");
                var items = FXCollections.<String>observableArrayList();
                for (var d : doctors) {
                    String name = Objects.toString(d.getOrDefault("fullName", "Doctor"));
                    String spec = Objects.toString(d.getOrDefault("speciality", ""));
                    String city = Objects.toString(d.getOrDefault("clinicCity", ""));
                    items.add(name + " — " + spec + " (" + city + ") :: " + d.get("id"));
                }
                javafx.application.Platform.runLater(() -> {
                    doctorsList.setItems(items);
                    slotsList.getItems().clear();
                    setStatus("Found " + doctors.size());
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> setStatus("Search error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onLoadSlots() {
        String doctorId = extractDoctorId();
        if (doctorId == null) { setStatus("Select a doctor"); return; }

        LocalDate date = (datePicker != null && datePicker.getValue() != null)
                ? datePicker.getValue() : LocalDate.now().plusDays(1);

        setStatus("Loading slots...");
        new Thread(() -> {
            try {
                var slots = api.getSlots(doctorId, date);
                javafx.application.Platform.runLater(() -> {
                    slotsList.setItems(FXCollections.observableArrayList(slots));
                    setStatus(slots.isEmpty() ? "No slots" : "Pick a slot then Book");
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> setStatus("Slots error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onBook() {
        String slot = slotsList.getSelectionModel().getSelectedItem();
        if (slot == null) { toast("Pick a slot first"); return; }

        String doctorId = extractDoctorId();
        String reason   = Optional.ofNullable(reasonArea.getText()).orElse("Consultation").trim();

        // Use the overload that uses the *current patient* from session if you have it,
        // otherwise pass api.getPatientId()
        new Thread(() -> {
            try {
                api.book(doctorId, slot, reason);           // or api.book(doctorId, api.getPatientId(), slot, reason)
                Platform.runLater(() -> {
                    toast("Booked ✔");
                    slotsList.getItems().remove(slot);      // optional visual feedback
                });
            } catch (Exception ex) {
                Platform.runLater(() -> toast("Booking failed: " + ex.getMessage()));
            }
        }).start();
    }

    private void toast(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private String extractDoctorId() {
        int idx = doctorsList.getSelectionModel().getSelectedIndex();
        if (idx < 0 || idx >= doctors.size()) return null;
        Object id = doctors.get(idx).get("id");
        return id == null ? null : id.toString();
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
        System.out.println(msg);
    }
}
