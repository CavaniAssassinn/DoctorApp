package za.ac.cput.frontendjavafx.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class ApiClient {

    // ---------------- core ----------------
    private final String base;                     // e.g. http://localhost:8080/api/v1
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // ---------------- session state ----------------
    private boolean doctor;
    private String patientId;
    private String doctorId;
    private String fullName;
    private String email;
    private String bearerToken;                    // "Bearer <jwt>"

    public ApiClient(String baseUrl) { this.base = baseUrl; }

    // getters used by controllers
    public boolean isDoctor() { return doctor; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getFullName() { return fullName; }
    public Optional<String> getEmail() { return Optional.ofNullable(email); }
    public Optional<String> getToken() { return Optional.ofNullable(bearerToken); }
    public Optional<String> userId() { return doctor ? Optional.ofNullable(doctorId) : Optional.ofNullable(patientId); }

    // ---------------- auth ----------------

    /** Login (email + password). Expects backend to return {token, role, patientId, doctorId, fullName, email}. */
    public void login(String identifier, String password) throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email", identifier);
        body.put("identifier", identifier);
        body.put("password", password);

        String json = mapper.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2)
            throw new RuntimeException("Login failed: " + res.statusCode() + " " + res.body());

        LoginResp lr = mapper.readValue(res.body(), LoginResp.class);

        this.doctor    = "DOCTOR".equalsIgnoreCase(lr.role);
        this.patientId = lr.patientId;
        this.doctorId  = lr.doctorId;
        this.fullName  = lr.fullName;
        this.email     = lr.email;
        this.bearerToken = (lr.token == null || lr.token.isBlank()) ? null : "Bearer " + lr.token;
    }

    /** Register a patient. */
    public void registerPatient(String fullName, String email, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("email", email);
        body.put("password", password);
        body.put("role", "PATIENT");
        post("/auth/register", body, new TypeReference<Map<String,Object>>() {});
    }

    /** Register with explicit role (PATIENT/DOCTOR). */
    public Map<String,Object> register(String fullName, String email, String password, String role) throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("fullName", fullName);
        body.put("email",    email);
        body.put("password", password);
        if (role != null && !role.isBlank()) body.put("role", role);
        return post("/auth/register", body, new TypeReference<Map<String,Object>>() {});
    }

    /** Link an existing doctor UUID to a new DOCTOR user. */
    public Map<String,Object> registerDoctor(String email, String password, String doctorId) throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("role", "DOCTOR");
        body.put("doctorId", doctorId);
        return post("/auth/register", body, new TypeReference<Map<String,Object>>() {});
    }

    // ---------------- doctors & appointments ----------------

    public DoctorDto[] getDoctors() throws Exception {
        return get("/doctors", DoctorDto[].class);
    }

    public Optional<DoctorDto> getMyDoctor() throws Exception {
        if (!isDoctor() || doctorId == null) return Optional.empty();
        try {
            return Optional.of(get("/doctors/" + doctorId, DoctorDto.class));
        } catch (Exception e) {
            for (DoctorDto d : getDoctors()) if (doctorId.equals(d.id)) return Optional.of(d);
            return Optional.empty();
        }
    }

    public AppointmentDto[] getMyAppointments() throws Exception {
        requirePatient();
        return get("/appointments/me?patientId=" + url(patientId), AppointmentDto[].class);
    }

    public AppointmentDto[] getDoctorAppointments() throws Exception {
        requireDoctor();
        return get("/appointments/doctor/" + doctorId, AppointmentDto[].class);
    }

    /** Book for current patient. */
    public AppointmentDto book(String doctorId, String startIso, String reason) throws Exception {
        requirePatient();
        var req = new BookReq(doctorId, this.patientId, startIso, reason);
        return post("/appointments", req, AppointmentDto.class);
    }

    /** Book with explicit patientId (admin/secretary style). */
    public Map<String,Object> book(String doctorId, String patientId, String startIso, String reason) throws Exception {
        Map<String,String> req = new HashMap<>();
        req.put("doctorId", doctorId);
        req.put("patientId", patientId);
        req.put("start", startIso);
        req.put("reason", (reason == null || reason.isBlank()) ? "Consultation" : reason);
        return post("/appointments", req, new TypeReference<Map<String,Object>>() {});
    }

    public void updateAppointmentStatus(String apptId, String status) throws Exception {
        Map<String, String> body = Map.of("status", status);
        String json = mapper.writeValueAsString(body);
        HttpRequest.Builder b = withAuth(HttpRequest.newBuilder()
                .uri(URI.create(base + "/appointments/" + apptId + "/status"))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)));
        HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2)
            throw new RuntimeException("PATCH /appointments/" + apptId + "/status -> " + res.statusCode() + ": " + res.body());
    }

    // ---------------- availability / time off (doctor) ----------------

    public static class Availability {
        public int dayOfWeek; public String start; public String end; public int slotMinutes;
        public Availability(int d, String s, String e, int m){ dayOfWeek=d; start=s; end=e; slotMinutes=m; }
    }
    static class TimeOffReq { public final String start,end,reason; TimeOffReq(String s,String e,String r){ start=s; end=e; reason=r; } }
    static class Ack { public boolean ok; public Integer count; }

    public void setWeeklyAvailability(List<Availability> list) throws Exception {
        requireDoctor();
        String path = "/doctor/availability?doctorId=" + url(doctorId);
        post(path, list, Ack.class);
    }

    public void addTimeOff(String startIso, String endIso, String reason) throws Exception {
        requireDoctor();
        String path = "/doctor/timeoff?doctorId=" + url(doctorId);
        post(path, new TimeOffReq(startIso, endIso, reason), Ack.class);
    }

    // ---------------- search & slots helpers ----------------

    public List<Map<String,Object>> searchDoctors(String query, String city, String speciality) throws Exception {
        List<Map<String,Object>> all = get("/doctors", new TypeReference<List<Map<String,Object>>>(){});
        String q = norm(query), c = norm(city), s = norm(speciality);

        return all.stream().filter(d -> {
            String name    = str(d.getOrDefault("fullName", d.getOrDefault("name","")));
            String spec    = str(d.getOrDefault("speciality",""));
            String cityVal = str(d.getOrDefault("clinicCity",""));

            if (cityVal.isEmpty()) {
                Object clinic = d.get("clinic");
                if (clinic instanceof Map<?,?> m) {
                    cityVal = Objects.toString(m.get("city"), "");
                }
            }
            return contains(name, q) && contains(spec, s) && contains(cityVal, c);
        }).collect(Collectors.toList());
    }

    public List<String> getSlots(String doctorId, LocalDate date) throws Exception {
        AppointmentDto[] arr = get("/appointments/doctor/" + doctorId, AppointmentDto[].class);
        Set<String> taken = (arr == null) ? Set.of() :
                Arrays.stream(arr)
                        .map(a -> a.start)
                        .filter(s -> s != null && s.startsWith(date.toString()))
                        .collect(Collectors.toSet());

        LocalTime start = LocalTime.of(9,0), end = LocalTime.of(17,0);
        int slotMinutes = 20;
        List<String> out = new ArrayList<>();
        for (LocalTime t = start; t.isBefore(end); t = t.plusMinutes(slotMinutes)) {
            String iso = date + "T" + (t.toString().length()==5 ? t + ":00" : t.toString());
            if (!taken.contains(iso)) out.add(iso);
        }
        return out;
    }

    // ---------------- HTTP helpers (JWT header) ----------------

    private HttpRequest.Builder withAuth(HttpRequest.Builder b) {
        if (bearerToken != null) b.header("Authorization", bearerToken);
        return b;
    }

    private <T> T get(String path, Class<T> type) throws Exception {
        HttpRequest req = withAuth(HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .GET()).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2)
            throw new RuntimeException("GET " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), type);
    }

    private <T> T get(String path, TypeReference<T> ref) throws Exception {
        HttpRequest req = withAuth(HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .GET()).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2)
            throw new RuntimeException("GET " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), ref);
    }

    private <T> T post(String path, Object bodyObj, Class<T> type) throws Exception {
        String json = mapper.writeValueAsString(bodyObj);
        HttpRequest req = withAuth(HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2 && res.statusCode() != 201)
            throw new RuntimeException("POST " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), type);
    }

    private <T> T post(String path, Object bodyObj, TypeReference<T> ref) throws Exception {
        String json = mapper.writeValueAsString(bodyObj);
        HttpRequest req = withAuth(HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))).build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2 && res.statusCode() != 201)
            throw new RuntimeException("POST " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), ref);
    }

    // ---------------- DTOs ----------------

    public static class LoginResp {
        public String token;     // JWT
        public String role;      // "PATIENT" or "DOCTOR"
        public String patientId; // when PATIENT
        public String doctorId;  // when DOCTOR
        public String fullName;
        public String email;
    }

    public static class DoctorDto {
        public String id;
        public String fullName;
        public String speciality;
        public String clinicCity;
        @Override public String toString() { return fullName + " (" + speciality + ", " + clinicCity + ")"; }
    }

    public static class AppointmentDto {
        public String id;
        public String doctorId;
        public String patientId;
        public String start;   // ISO-8601
        public String status;  // SCHEDULED/COMPLETED/CANCELLED/…
    }

    public static class BookReq {
        public final String doctorId, patientId, start, reason;
        public BookReq(String doctorId, String patientId, String start, String reason) {
            this.doctorId = doctorId; this.patientId = patientId; this.start = start; this.reason = reason;
        }
    }

    // ---------------- small utils ----------------

    private static String url(String s){ return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    private static String norm(String s){ return (s == null) ? "" : s.trim().toLowerCase(); }
    private static String str(Object o){ return (o == null) ? "" : o.toString(); }
    private static boolean contains(String hay, String needle){
        if (needle == null || needle.isBlank()) return true;
        return hay != null && hay.toLowerCase().contains(needle.toLowerCase());
    }
    private void requirePatient(){ if (patientId == null) throw new IllegalStateException("Not logged in as patient"); }
    private void requireDoctor(){ if (doctorId == null) throw new IllegalStateException("Not logged in as doctor"); }
}
