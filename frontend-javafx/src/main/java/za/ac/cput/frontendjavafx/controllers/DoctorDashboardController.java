package za.ac.cput.frontendjavafx.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Duration;
import za.ac.cput.frontendjavafx.api.ApiClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DoctorDashboardController {

    /* ===== Header & sidebar ===== */
    @FXML private Label headerDocName;
    @FXML private Label headerEmail;     // we set this blank unless you later expose email from ApiClient
    @FXML private Label sideName;
    @FXML private Label sideSpec;

    /* ===== Main lists ===== */
    @FXML private ListView<String> todayList;    // “Today’s Schedule”
    @FXML private ListView<String> latestList;   // “Latest Appointments”

    /* ===== KPI labels ===== */
    @FXML private Label kpiTotal;
    @FXML private Label kpiCompleted;
    @FXML private Label kpiCancelled;
    @FXML private Label kpiFollowup;

    /* ===== Misc ===== */
    @FXML private Button refreshBtn;
    @FXML private Label statusLabel;

    private ApiClient api;
    private Timeline autorefresh;

    /* ========================== Wiring ========================== */

    /** Called from LoginController after loading the FXML. */
    public void setApi(ApiClient api) {
        this.api = api;

        // Header & sidebar labels from logged-in user
        final String displayName = (api.isDoctor() ? "Dr. " : "") + safe(api.getFullName());
        Platform.runLater(() -> {
            if (headerDocName != null) headerDocName.setText(displayName);
            if (sideName != null)      sideName.setText(displayName);
            if (headerEmail != null)   headerEmail.setText(""); // set real email here if your ApiClient exposes it
        });

        // Try fetch speciality for the doctor (if backend supports it)
        if (api.isDoctor() && api.getDoctorId() != null) {
            new Thread(() -> {
                try {
                    api.getMyDoctor().ifPresent(d ->
                            Platform.runLater(() -> { if (sideSpec != null) sideSpec.setText(safe(d.speciality)); })
                    );
                } catch (Exception ignored) { /* keep defaults */ }
            }).start();
        }

        // Initial load
        loadDashboard();
    }

    @FXML
    private void initialize() {
        // Optional auto-refresh every 30s; you can remove if not desired
        autorefresh = new Timeline(new KeyFrame(Duration.seconds(30), e -> loadDashboard()));
        autorefresh.setCycleCount(Timeline.INDEFINITE);
        autorefresh.play();
    }

    /* ========================== Actions ========================== */

    @FXML
    private void onClickRefresh() {
        loadDashboard();
    }

    @FXML
    private void onLogout() {
        if (autorefresh != null) autorefresh.stop();
        ((Stage) headerDocName.getScene().getWindow()).close();
    }

    /* ========================== Data load ========================== */

    /** Pull appointments for the logged-in doctor and update lists + KPIs. */
    private void loadDashboard() {
        if (api == null) return;
        setStatus("Loading…");

        new Thread(() -> {
            try {
                ApiClient.AppointmentDto[] appts = api.getDoctorAppointments();
                if (appts == null) appts = new ApiClient.AppointmentDto[0];

                // Sort by start (newest first for the "latest" list)
                List<ApiClient.AppointmentDto> all = new ArrayList<>(List.of(appts));
                all.sort(Comparator.comparing(a -> a.start));           // oldest -> newest
                all.sort(Comparator.comparing((ApiClient.AppointmentDto a) -> a.start).reversed()); // newest first

                // Build “Latest Appointments” (newest first)
                List<String> latestRows = new ArrayList<>();
                for (ApiClient.AppointmentDto a : all) latestRows.add(render(a));

                // Build “Today’s Schedule”
                LocalDate today = LocalDate.now();
                List<String> todayRows = new ArrayList<>();
                for (ApiClient.AppointmentDto a : all) {
                    try {
                        LocalDateTime dt = LocalDateTime.parse(a.start);
                        if (dt.toLocalDate().equals(today)) {
                            String row = dt.toLocalTime() + " — " + shortId(a.patientId) + " — " + safe(a.status);
                            todayRows.add(row);
                        }
                    } catch (Exception ignored) { /* skip bad timestamp */ }
                }

                // KPIs
                final int total      = all.size();
                final int completed  = (int) all.stream().filter(x -> "COMPLETED".equalsIgnoreCase(x.status)).count();
                final int cancelled  = (int) all.stream().filter(x -> "CANCELLED".equalsIgnoreCase(x.status)).count();
                final int followup   = (int) all.stream().filter(x -> "FOLLOWUP".equalsIgnoreCase(x.status)).count();

                // Push to UI
                final var latestObs = FXCollections.observableArrayList(latestRows);
                final var todayObs  = FXCollections.observableArrayList(todayRows);
                final String msg    = total == 0 ? "No appointments yet"
                        : (todayRows.isEmpty() ? "Loaded " + total + " (none today)"
                        : "Loaded " + total);

                Platform.runLater(() -> {
                    if (todayList != null)  todayList.setItems(todayObs);
                    if (latestList != null) latestList.setItems(latestObs);

                    if (kpiTotal != null)     kpiTotal.setText(Integer.toString(total));
                    if (kpiCompleted != null) kpiCompleted.setText(Integer.toString(completed));
                    if (kpiCancelled != null) kpiCancelled.setText(Integer.toString(cancelled));
                    if (kpiFollowup != null)  kpiFollowup.setText(Integer.toString(followup));

                    setStatus(msg);
                });

            } catch (Exception ex) {
                Platform.runLater(() -> setStatus("Load failed: " + ex.getMessage()));
            }
        }).start();
    }

    /* ========================== Helpers ========================== */

    private static String render(ApiClient.AppointmentDto a) {
        // “YYYY-MM-DD HH:mm — <patientId-short> — STATUS”
        try {
            LocalDateTime dt = LocalDateTime.parse(a.start);
            return dt.toLocalDate() + " " + dt.toLocalTime() + " — " + shortId(a.patientId) + " — " + safe(a.status);
        } catch (Exception e) {
            return safe(a.start) + " — " + shortId(a.patientId) + " — " + safe(a.status);
        }
    }

    private static String shortId(String id) {
        if (id == null) return "-";
        return id.length() <= 8 ? id : id.substring(0, 8);
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg == null ? "" : msg);
    }
}
