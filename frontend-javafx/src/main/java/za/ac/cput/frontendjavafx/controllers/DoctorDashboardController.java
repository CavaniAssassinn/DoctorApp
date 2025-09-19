package za.ac.cput.frontendjavafx.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorDashboardController {

    // Header + left card
    @FXML private Label headerDocName;
    @FXML private Label headerSpec;
    @FXML private Label headerEmail;
    @FXML private Label docName;
    @FXML private Label docDepartment;

    // Schedule panel
    @FXML private ListView<String> apptList;
    @FXML private Label selTime, selPatient, selStatus, selReason;
    @FXML private Button markCompletedBtn, cancelBtn, refreshBtn;
    @FXML private Label statusLabel;
    @FXML private ImageView companyLogo;

    // Availability / Time-off inputs (only if present in FXML)
    @FXML private ComboBox<Integer> dayOfWeekBox;
    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private TextField slotMinutesField;
    @FXML private Button saveAvailabilityBtn;
    @FXML private DatePicker timeOffDate;
    @FXML private TextField timeOffStartField;
    @FXML private TextField timeOffEndField;
    @FXML private TextField timeOffReasonField;
    @FXML private Button addTimeOffBtn;

    private ApiClient api;
    private final List<ApiClient.AppointmentDto> todays = new ArrayList<>();
    private int selectedIndex = -1;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public void setApi(ApiClient api){
        this.api = api;

        String displayName = (api.isDoctor() ? "Dr. " : "") + safe(api.getFullName());

        Platform.runLater(() -> {
            if (headerDocName != null) headerDocName.setText(displayName);
            if (docName != null)      docName.setText(displayName);
            if (headerEmail != null)  headerEmail.setText(api.getEmail().orElse(""));
        });

        // Fetch speciality for logged-in doctor and reflect in header/left card
        if (api.isDoctor() && api.getDoctorId() != null) {
            new Thread(() -> {
                try {
                    var maybe = api.getMyDoctor();
                    maybe.ifPresent(d -> Platform.runLater(() -> {
                        if (docDepartment != null) docDepartment.setText(safe(d.speciality));
                        if (headerSpec != null)     headerSpec.setText(safe(d.speciality));
                    }));
                } catch (Exception ignored) { }
            }).start();
        }

        onRefresh();
    }

    @FXML
    public void initialize(){
        if (apptList != null) {
            apptList.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
                selectedIndex = newV == null ? -1 : newV.intValue();
                showSelected();
            });
        }
        if (dayOfWeekBox != null) {
            for (int i = 1; i <= 7; i++) dayOfWeekBox.getItems().add(i);
            dayOfWeekBox.getSelectionModel().select(Integer.valueOf(1));
        }
        if (slotMinutesField != null) slotMinutesField.setText("20");
        if (timeOffDate != null) timeOffDate.setValue(LocalDate.now().plusDays(1));
    }

    /* =====================  Schedule  ===================== */

    @FXML
    public void onRefresh() {
        if (api == null) return;
        setStatus("Loading…");
        new Thread(() -> {
            try {
                ApiClient.AppointmentDto[] all = api.getDoctorAppointments();
                LocalDate today = LocalDate.now();

                todays.clear();
                if (all != null) {
                    for (var a : all) {
                        LocalDate d = parseDate(a.start);
                        if (today.equals(d)) todays.add(a);
                    }
                    todays.sort(Comparator.comparing(a -> parseDateTime(a.start)));
                }

                var items = FXCollections.<String>observableArrayList();
                for (var a : todays) {
                    LocalDateTime dt = parseDateTime(a.start);
                    String when = dt == null ? a.start : dt.toLocalTime().format(timeFmt);
                    String shortPatient = a.patientId == null ? "-" : shortId(a.patientId);
                    items.add(when + " — patient " + shortPatient + " — " + a.status);
                }

                Platform.runLater(() -> {
                    apptList.setItems(items);
                    apptList.getSelectionModel().clearSelection();
                    clearDetails();
                    setStatus(items.isEmpty() ? "No appointments today" : "Loaded " + items.size());
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> setStatus("Load failed: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onMarkCompleted() {
        ApiClient.AppointmentDto a = current();
        if (a == null) { setStatus("Pick an appointment"); return; }
        if ("COMPLETED".equalsIgnoreCase(a.status)) { setStatus("Already completed"); return; }
        updateStatus(a.id, "COMPLETED");
    }

    @FXML
    public void onCancel() {
        ApiClient.AppointmentDto a = current();
        if (a == null) { setStatus("Pick an appointment"); return; }
        if ("CANCELLED".equalsIgnoreCase(a.status)) { setStatus("Already cancelled"); return; }
        updateStatus(a.id, "CANCELLED");
    }

    private void updateStatus(String apptId, String status) {
        setStatus("Updating…");
        new Thread(() -> {
            try {
                api.updateAppointmentStatus(apptId, status);
                Platform.runLater(() -> {
                    setStatus("Updated");
                    onRefresh();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> setStatus("Update failed: " + ex.getMessage()));
            }
        }).start();
    }

    private void showSelected() {
        ApiClient.AppointmentDto a = current();
        if (a == null) { clearDetails(); return; }

        LocalDateTime dt = parseDateTime(a.start);
        String when = dt == null ? a.start : dt.toLocalDate() + " " + dt.toLocalTime().format(timeFmt);

        selTime.setText(when);
        selPatient.setText(a.patientId == null ? "-" : a.patientId);
        selStatus.setText(a.status == null ? "-" : a.status);
        selReason.setText("Consultation"); // placeholder
        updateActionButtons();
    }

    private void updateActionButtons() {
        ApiClient.AppointmentDto a = current();
        boolean canAct = (a != null) && !"COMPLETED".equalsIgnoreCase(a.status) && !"CANCELLED".equalsIgnoreCase(a.status);
        if (markCompletedBtn != null) markCompletedBtn.setDisable(!canAct);
        if (cancelBtn != null)       cancelBtn.setDisable(!canAct);
    }

    private void clearDetails() {
        if (selTime != null)    selTime.setText("-");
        if (selPatient != null) selPatient.setText("-");
        if (selStatus != null)  selStatus.setText("-");
        if (selReason != null)  selReason.setText("-");
        if (markCompletedBtn != null) markCompletedBtn.setDisable(true);
        if (cancelBtn != null)       cancelBtn.setDisable(true);
    }

    private ApiClient.AppointmentDto current() {
        if (selectedIndex < 0 || selectedIndex >= todays.size()) return null;
        return todays.get(selectedIndex);
    }

    private static String shortId(String id) {
        return (id == null || id.length() <= 8) ? String.valueOf(id) : id.substring(0, 8);
    }

    private static LocalDate parseDate(String iso) {
        try { return LocalDate.parse(iso.substring(0, 10)); } catch (Exception ignored) { return null; }
    }
    private static LocalDateTime parseDateTime(String iso) {
        try { return LocalDateTime.parse(iso); } catch (Exception ignored) { return null; }
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    /* =================  Availability / Time-off  ================= */

    @FXML
    public void onSaveAvailability(){
        try {
            int d = dayOfWeekBox.getValue();
            String s = startField.getText().trim();
            String e = endField.getText().trim();
            int m = Integer.parseInt(slotMinutesField.getText().trim());
            java.util.List<ApiClient.Availability> list = java.util.List.of(new ApiClient.Availability(d, s, e, m));

            setStatus("Saving availability…");
            new Thread(() -> {
                try {
                    api.setWeeklyAvailability(list);
                    Platform.runLater(() -> setStatus("Availability saved."));
                } catch (Exception ex){
                    ex.printStackTrace();
                    Platform.runLater(() -> setStatus("Save failed: " + ex.getMessage()));
                }
            }).start();
        } catch (Exception ex){
            setStatus("Input error: " + ex.getMessage());
        }
    }

    @FXML
    public void onAddTimeOff(){
        LocalDate d = timeOffDate.getValue();
        if (d == null) { setStatus("Pick a date"); return; }
        String startIso = d + "T" + timeOffStartField.getText().trim();
        String endIso   = d + "T" + timeOffEndField.getText().trim();
        String reason   = timeOffReasonField.getText();

        setStatus("Adding time off…");
        new Thread(() -> {
            try {
                api.addTimeOff(startIso, endIso, reason);
                Platform.runLater(() -> setStatus("Time off added."));
            } catch (Exception ex){
                ex.printStackTrace();
                Platform.runLater(() -> setStatus("Time off failed: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onLogout() {
        Stage stage = (Stage) (docName.getScene().getWindow());
        stage.close();
    }

    /* ---------- helpers ---------- */
    private static String safe(String s){ return s == null ? "" : s; }
}
