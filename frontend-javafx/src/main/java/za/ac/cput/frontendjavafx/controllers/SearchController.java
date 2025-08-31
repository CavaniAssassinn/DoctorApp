package za.ac.cput.frontendjavafx.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;
import java.util.*;

public class SearchController {
    @FXML private TextField queryField;
    @FXML private TextField cityField;
    @FXML private TextField specialityField;
    @FXML private ListView<String> doctorsList;
    @FXML private DatePicker datePicker;
    @FXML private ListView<String> slotsList;
    @FXML private TextArea reasonArea;
    @FXML private Label statusLabel;

    private ApiClient api;
    private List<Map<String,Object>> doctors = new ArrayList<>();

    public void setApi(ApiClient api){ this.api = api; }

    @FXML
    public void initialize(){
        datePicker.setValue(LocalDate.now().plusDays(1));
        doctorsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        slotsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    public void onSearch(){
        statusLabel.setText("Searching...");
        new Thread(() -> {
            try {
                doctors = api.searchDoctors(queryField.getText(), cityField.getText(), specialityField.getText());
                var items = FXCollections.<String>observableArrayList();
                for (var d : doctors) {
                    String name = Objects.toString(d.getOrDefault("fullName", d.getOrDefault("name", "Doctor")), "");
                    String spec = Objects.toString(d.getOrDefault("speciality",""), "");
                    Map<?,?> clinic = (Map<?,?>) d.getOrDefault("clinic", Collections.emptyMap());
                    String city = Objects.toString(clinic.get("city"), "");                     items.add(name + " — " + spec + " (" + city + ") :: " + d.get("id"));
                }
                javafx.application.Platform.runLater(() -> {
                    doctorsList.setItems(items);
                    statusLabel.setText("Found " + doctors.size());
                    slotsList.getItems().clear();
                });
            } catch (Exception ex){
                javafx.application.Platform.runLater(() -> statusLabel.setText("Search error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onLoadSlots(){
        int idx = doctorsList.getSelectionModel().getSelectedIndex();
        if (idx < 0) { statusLabel.setText("Select a doctor"); return; }
        String doctorId = Objects.toString(doctors.get(idx).get("id"), null);
        LocalDate date = datePicker.getValue();

        statusLabel.setText("Loading slots…");
        new Thread(() -> {
            try {
                var slots = api.getSlots(doctorId, date);
                javafx.application.Platform.runLater(() ->
                        slotsList.setItems(FXCollections.observableArrayList(slots)));
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText(slots.isEmpty() ? "No slots" : "Pick a slot then Book"));
            } catch (Exception ex){
                javafx.application.Platform.runLater(() -> statusLabel.setText("Slots error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onBook(){
        String slot = slotsList.getSelectionModel().getSelectedItem();
        if (slot == null) { statusLabel.setText("Pick a slot first"); return; }
        String reason = Optional.ofNullable(reasonArea.getText()).orElse("Consultation").trim();
        String patientId = api.userId().orElseThrow(() -> new RuntimeException("No user id"));

        statusLabel.setText("Booking…");
        new Thread(() -> {
            try {
                var res = api.book(extractDoctorId(), patientId, slot, reason);
                javafx.application.Platform.runLater(() ->
                        statusLabel.setText("Booked: " + res.get("id")));
            } catch (Exception ex){
                javafx.application.Platform.runLater(() -> statusLabel.setText("Book failed: " + ex.getMessage()));
            }
        }).start();
    }

    private String extractDoctorId(){
        int idx = doctorsList.getSelectionModel().getSelectedIndex();
        return Objects.toString(doctors.get(idx).get("id"), null);
    }
}
