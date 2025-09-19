package za.ac.cput.frontendjavafx.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


public class ApiClient {
    private final String base;                     // e.g. http://localhost:8080/api/v1
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // session-ish state
    private boolean doctor;
    private String patientId;
    private String doctorId;
    private String fullName;
    public String email;
    // in ApiClient
    public Optional<String> getEmail() { return Optional.ofNullable(email); }

    public ApiClient(String baseUrl) {
        this.base = baseUrl;
    }


    /** Login with an identifier:
     *  - Patient: pass their email (e.g. john@example.com)
     *  - Doctor: pass the doctor's UUID
     *  Password is ignored by the backend (dev-only).
     */


    // ---------- generic GET/POST overloads for TypeReference ----------
    private <T> T get(String path, TypeReference<T> ref) throws Exception {
        var req = HttpRequest.newBuilder().uri(URI.create(base + path)).GET().build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2) throw new RuntimeException("GET " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), ref);
    }

    private <T> T post(String path, Object bodyObj, TypeReference<T> ref) throws Exception {
        String json = mapper.writeValueAsString(bodyObj);
        var req = HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2 && res.statusCode() != 201)
            throw new RuntimeException("POST " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), ref);
    }

    // ---------- SEARCH DOCTORS ----------
    public List<Map<String,Object>> searchDoctors(String query, String city, String speciality) throws Exception {
        List<Map<String,Object>> all = get("/doctors", new TypeReference<List<Map<String,Object>>>(){});
        String q = norm(query), c = norm(city), s = norm(speciality);

        return all.stream().filter(d -> {
            // d is Map<String,Object> so getOrDefault is fine here
            String name   = str(d.getOrDefault("fullName", d.getOrDefault("name","")));
            String spec   = str(d.getOrDefault("speciality",""));
            String cityVal= str(d.getOrDefault("clinicCity",""));

            // If city still empty, try nested clinic map WITHOUT getOrDefault
            if (cityVal.isEmpty()) {
                Object clinic = d.get("clinic");
                if (clinic instanceof Map<?,?> m) {
                    Object cityObj = m.get("city");            // don't use getOrDefault on Map<?,?>
                    cityVal = Objects.toString(cityObj, "");   // safe conversion
                }
                // If your compiler doesn't support pattern variables:
                // if (clinic instanceof Map) {
                //     Map<?,?> m = (Map<?,?>) clinic;
                //     cityVal = Objects.toString(m.get("city"), "");
                // }
            }

            return contains(name, q) && contains(spec, s) && contains(cityVal, c);
        }).collect(java.util.stream.Collectors.toList());
    }


    private static String norm(String s){ return s==null? "": s.trim().toLowerCase(); }
    private static boolean contains(String hay, String needle){ return needle.isEmpty() || hay.toLowerCase().contains(needle); }
    private static String str(Object o){ return o==null? "": o.toString(); }

    // ---------- GET SLOTS FOR A DATE ----------
    public List<String> getSlots(String doctorId, LocalDate date) throws Exception {
        // get existing appointments for this doctor; filter to the chosen date
        AppointmentDto[] arr = get("/appointments/doctor/" + doctorId, AppointmentDto[].class);
        Set<String> taken = (arr == null) ? Set.of() :
                Arrays.stream(arr)
                        .map(a -> a.start) // ensure your AppointmentDto has 'start'
                        .filter(s -> s != null && s.startsWith(date.toString()))
                        .collect(Collectors.toSet());

        // simple default window: 09:00–17:00, 20-minute slots
        LocalTime start = LocalTime.of(9,0);
        LocalTime end   = LocalTime.of(17,0);
        int slotMinutes = 20;

        List<String> out = new ArrayList<>();
        for (LocalTime t = start; t.isBefore(end); t = t.plusMinutes(slotMinutes)) {
            String iso = date + "T" + (t.toString().length()==5 ? t + ":00" : t.toString()); // ensure HH:mm:ss
            if (!taken.contains(iso)) out.add(iso);
        }
        return out;
    }

    // ---------- BOOK (overload with explicit patientId) ----------
    public Map<String,Object> book(String doctorId, String patientId, String startIso, String reason) throws Exception {
        Map<String,String> req = new HashMap<>();
        req.put("doctorId", doctorId);
        req.put("patientId", patientId);
        req.put("start", startIso);
        req.put("reason", reason == null ? "Consultation" : reason);
        return post("/appointments", req, new TypeReference<Map<String,Object>>(){});
    }

    // ---------- expose current user id ----------
    public Optional<String> userId() {
        return doctor ? Optional.ofNullable(doctorId) : Optional.ofNullable(patientId);
    }

    // in ApiClient.java
    public void login(String identifier, String password) throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email", identifier);      // primary
        body.put("identifier", identifier); // fallback if server used it
        body.put("password", password);

        String json = mapper.writeValueAsString(body);
        var req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        var res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("Login failed: " + res.statusCode() + " " + res.body());
        }
        LoginResp lr = mapper.readValue(res.body(), LoginResp.class);
        this.doctor    = "DOCTOR".equalsIgnoreCase(lr.role);
        this.patientId = lr.patientId;
        this.doctorId  = lr.doctorId;
        this.fullName  = lr.fullName;
        this.email     = lr.email;                 // <— save email if backend returns it

    }


    // === Register (Patient) ===
    // ApiClient.java
    public void registerPatient(String fullName, String email, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("fullName", fullName);       // <- EXACT key the backend expects
        body.put("email", email);
        body.put("password", password);
        body.put("role", "PATIENT");
        post("/auth/register", body, new com.fasterxml.jackson.core.type.TypeReference<Map<String,Object>>(){});
    }
    public Map<String,Object> register(String fullName, String email, String password, String role) throws Exception {
        Map<String,String> body = new java.util.HashMap<>();
        body.put("fullName", fullName);
        body.put("email",    email);
        body.put("password", password);
        if (role != null && !role.isBlank()) body.put("role", role); // "PATIENT" or "DOCTOR"
        return post("/auth/register", body, new TypeReference<Map<String,Object>>() {});
    }

    // === Register (Doctor) — requires existing doctorId (UUID) ===
    public Map<String,Object> registerDoctor(String email, String password, String doctorId) throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("role", "DOCTOR");
        body.put("doctorId", doctorId);

        return post("/auth/register", body, new com.fasterxml.jackson.core.type.TypeReference<Map<String,Object>>(){});
    }

    public void updateAppointmentStatus(String apptId, String status) throws Exception {
        Map<String, String> body = Map.of("status", status);
        String json = mapper.writeValueAsString(body);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + "/appointments/" + apptId + "/status"))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        // Your backend returns 200 or 204 (void). Treat 2xx as success.
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("PATCH /appointments/" + apptId + "/status -> " + res.statusCode() + ": " + res.body());
        }
    }

    // ----- Dashboard helpers -----

    public DoctorDto[] getDoctors() throws Exception {
        return get("/doctors", DoctorDto[].class);
    }

    public java.util.Optional<DoctorDto> getMyDoctor() throws Exception {
        if (!isDoctor() || doctorId == null) return java.util.Optional.empty();
        try {
            return java.util.Optional.of(get("/doctors/" + doctorId, DoctorDto.class));
        } catch (Exception e) {
            // fallback if /doctors/{id} doesn’t exist: search list
            for (DoctorDto d : getDoctors()) if (doctorId.equals(d.id)) return java.util.Optional.of(d);
            return java.util.Optional.empty();
        }
    }
    public AppointmentDto[] getMyAppointments() throws Exception {
        requirePatient();
        return get("/appointments/me?patientId=" + patientId, AppointmentDto[].class);
    }

    public AppointmentDto[] getDoctorAppointments() throws Exception {
        requireDoctor();
        return get("/appointments/doctor/" + doctorId, AppointmentDto[].class);
    }

    /** Book an appointment for the current patient. */
    public AppointmentDto book(String doctorId, String startIso, String reason) throws Exception {
        requirePatient();
        var req = new BookReq(doctorId, this.patientId, startIso, reason);
        return post("/appointments", req, AppointmentDto.class);
    }

    // add near your other DTOs:
    public static class Availability {
        public int dayOfWeek;        // 1..7 (Mon=1)
        public String start;         // "HH:mm"
        public String end;           // "HH:mm"
        public int slotMinutes;
        public Availability(int d, String s, String e, int m){ dayOfWeek=d; start=s; end=e; slotMinutes=m; }
    }
    static class TimeOffReq { public final String start,end,reason;
        TimeOffReq(String s,String e,String r){ start=s; end=e; reason=r; }
    }
    static class Ack { public boolean ok; public Integer count; }

    // add these methods:
    public void setWeeklyAvailability(java.util.List<Availability> list) throws Exception {
        requireDoctor();
        String path = "/doctor/availability?doctorId=" + java.net.URLEncoder.encode(doctorId, java.nio.charset.StandardCharsets.UTF_8);
        post(path, list, Ack.class); // backend returns {"ok":true,"count":N}
    }

    public void addTimeOff(String startIso, String endIso, String reason) throws Exception {
        requireDoctor();
        String path = "/doctor/timeoff?doctorId=" + java.net.URLEncoder.encode(doctorId, java.nio.charset.StandardCharsets.UTF_8);
        post(path, new TimeOffReq(startIso, endIso, reason), Ack.class); // returns {"ok":true}
    }


    // ----- HTTP helpers -----

    private <T> T get(String path, Class<T> type) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2) throw new RuntimeException("GET " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), type);
    }

    private <T> T post(String path, Object bodyObj, Class<T> type) throws Exception {
        String json = mapper.writeValueAsString(bodyObj);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(base + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() / 100 != 2 && res.statusCode() != 201)
            throw new RuntimeException("POST " + path + " -> " + res.statusCode() + ": " + res.body());
        return mapper.readValue(res.body(), type);
    }



    private void requirePatient() {
        if (patientId == null) throw new IllegalStateException("Not logged in as patient");
    }
    private void requireDoctor() {
        if (doctorId == null) throw new IllegalStateException("Not logged in as doctor");
    }

    // ----- DTOs -----

    public static class LoginReq {
        public final String identifier;
        public final String password;
        public LoginReq(String identifier, String password) {
            this.identifier = identifier;
            this.password = password;
        }
    }
    public static class LoginResp {
        public String role;       // "PATIENT" or "DOCTOR"
        public String patientId;  // when PATIENT
        public String doctorId;   // when DOCTOR
        public String fullName;   // optional
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
        public String start;
        public String status;
    }

    public static class BookReq {
        public final String doctorId;
        public final String patientId;
        public final String start;
        public final String reason;
        public BookReq(String doctorId, String patientId, String start, String reason) {
            this.doctorId = doctorId; this.patientId = patientId; this.start = start; this.reason = reason;
        }
    }

    // ----- getters used by controllers -----
    public boolean isDoctor() { return doctor; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getFullName() { return fullName; }
}
