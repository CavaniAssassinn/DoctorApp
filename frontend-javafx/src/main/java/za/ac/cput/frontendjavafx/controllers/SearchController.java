package za.ac.cput.frontendjavafx.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;
import java.util.*;

public class SearchController {
    @FXML private TextField  queryField;
    @FXML private TextField  cityField;
    @FXML private TextField  specialityField;
    @FXML private ListView<String> doctorsList;
    @FXML private DatePicker datePicker;
    @FXML private ListView<String> slotsList;
    @FXML private TextArea  reasonArea;
    @FXML private Label     statusLabel;

    private ApiClient api;
    private List<Map<String,Object>> doctors = new ArrayList<>();

    public void setApi(ApiClient api){ this.api = api; }

    @FXML
    public void initialize(){
        datePicker.setValue(LocalDate.now().plusDays(1));
        doctorsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        slotsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Clear slots when doctor selection changes
        doctorsList.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            slotsList.getItems().clear();
            if (n != null && n.intValue() >= 0) {
                status("Doctor selected — click “Load Slots”.");
            }
        });
    }

    /* -------------------- Actions -------------------- */

    @FXML
    public void onSearch(){
        status("Searching…");
        slotsList.getItems().clear();

        new Thread(() -> {
            try {
                final List<Map<String,Object>> found =
                        api.searchDoctors(val(queryField), val(cityField), val(specialityField));

                final var items = FXCollections.<String>observableArrayList();
                for (var d : found) {
                    String name = str(d.getOrDefault("fullName", d.getOrDefault("name", "Doctor")));
                    String spec = str(d.get("speciality"));

                    // city can be top-level "clinicCity" OR inside a "clinic" map
                    String city = str(d.get("clinicCity"));
                    if (city.isBlank()) {
                        Object clinic = d.get("clinic");
                        if (clinic instanceof Map<?,?> m) {
                            city = Objects.toString(m.get("city"), "");
                        }
                    }
                    String id = Objects.toString(d.get("id"), "");
                    items.add(name + " — " + spec + (city.isBlank() ? "" : " (" + city + ")") + " :: " + id);
                }

                doctors = found;
                runFx(() -> {
                    doctorsList.setItems(items);
                    status(found.isEmpty() ? "No doctors found." : "Found " + found.size() + ". Select one.");
                });
            } catch (Exception ex){
                runFx(() -> status("Search error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onLoadSlots(){
        int idx = doctorsList.getSelectionModel().getSelectedIndex();
        if (idx < 0) { status("Select a doctor first."); return; }

        final String doctorId = getDoctorId(idx);
        final LocalDate date  = Optional.ofNullable(datePicker.getValue())
                .orElse(LocalDate.now().plusDays(1));

        status("Loading slots…");
        new Thread(() -> {
            try {
                var slots = api.getSlots(doctorId, date);
                runFx(() -> {
                    slotsList.setItems(FXCollections.observableArrayList(slots));
                    status(slots.isEmpty() ? "No available slots on " + date
                            : "Pick a slot then click Book.");
                });
            } catch (Exception ex){
                runFx(() -> status("Slots error: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onBook(){
        String slot = slotsList.getSelectionModel().getSelectedItem();
        if (slot == null) { status("Pick a slot first."); return; }

        int idx = doctorsList.getSelectionModel().getSelectedIndex();
        if (idx < 0) { status("Select a doctor first."); return; }

        final String doctorId = getDoctorId(idx);
        final String reason   = Optional.ofNullable(reasonArea.getText())
                .map(String::trim).filter(s -> !s.isEmpty())
                .orElse("Consultation");

        status("Booking…");
        new Thread(() -> {
            try {
                // uses logged-in patient from ApiClient
                var dto = api.book(doctorId, slot, reason);
                runFx(() -> status("Booked ✓  ID: " + dto.id));
            } catch (Exception ex){
                runFx(() -> status("Book failed: " + ex.getMessage()));
            }
        }).start();
    }

    /* -------------------- Helpers -------------------- */

    private static String val(TextField tf){ return tf == null ? "" : tf.getText(); }
    public static String str(Object o){ return o == null ? "" : o.toString(); }

    private String getDoctorId(int index){
        return Objects.toString(doctors.get(index).get("id"), null);
    }

    private void status(String s){
        if (statusLabel != null) statusLabel.setText(s);
    }

    private static void runFx(Runnable r){
        javafx.application.Platform.runLater(r);
    }
}
