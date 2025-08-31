package za.ac.cput.frontendjavafx.api;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiClient {
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();
    private String token;
    private String userId;
    private String role;

    public ApiClient(String baseUrl) { this.baseUrl = baseUrl; }

    private HttpRequest.Builder withAuth(HttpRequest.Builder b) {
        if (token != null) b.header("Authorization", "Bearer " + token);
        return b;
    }

    public boolean login(String email, String password) throws Exception {
        Map<String,String> body = Map.of("email", email, "password", password);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/auth/login"))
                .header("Content-Type","application/json")
                .POST(BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 == 2) {
            Map<String,Object> map = mapper.readValue(res.body(), new TypeReference<>() {});
            token = (String) map.get("accessToken");
            Map<String,Object> user = (Map<String,Object>) map.get("user");
            if (user != null) {
                userId = Objects.toString(user.get("id"), null);
                role   = Objects.toString(user.get("role"), null);
            }
            return token != null;
        }
        throw new RuntimeException("Login failed: " + res.statusCode() + " " + res.body());
    }

    public List<Map<String,Object>> searchDoctors(String q, String city, String speciality) throws Exception {
        StringBuilder url = new StringBuilder(baseUrl + "/doctors?");
        if (q != null && !q.isBlank()) url.append("q=").append(encode(q)).append("&");
        if (city != null && !city.isBlank()) url.append("city=").append(encode(city)).append("&");
        if (speciality != null && !speciality.isBlank()) url.append("speciality=").append(encode(speciality)).append("&");
        HttpRequest req = withAuth(HttpRequest.newBuilder(URI.create(url.toString()))).GET().build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) throw new RuntimeException("Search doctors failed: " + res.body());
        return mapper.readValue(res.body(), new TypeReference<>() {});
    }

    public List<String> getSlots(String doctorId, LocalDate date) throws Exception {
        String url = baseUrl + "/doctors/" + doctorId + "/slots?date=" + date;
        HttpRequest req = withAuth(HttpRequest.newBuilder(URI.create(url))).GET().build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) throw new RuntimeException("Get slots failed: " + res.body());
        return mapper.readValue(res.body(), new TypeReference<>() {});
    }

    public Map<String,Object> book(String doctorId, String patientId, String startIso, String reason) throws Exception {
        Map<String,Object> body = new HashMap<>();
        body.put("doctorId", doctorId);
        body.put("patientId", patientId);
        body.put("start", startIso);
        body.put("reason", reason);

        HttpRequest req = withAuth(HttpRequest.newBuilder(URI.create(baseUrl + "/appointments")))
                .header("Content-Type","application/json")
                .POST(BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) throw new RuntimeException("Book failed: " + res.body());
        return mapper.readValue(res.body(), new TypeReference<>() {});
    }

    public Optional<String> userId()  { return Optional.ofNullable(userId); }

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
