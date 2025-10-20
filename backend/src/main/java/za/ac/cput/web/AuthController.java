package za.ac.cput.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.auth.AuthService;
import za.ac.cput.auth.Role;
import za.ac.cput.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    private final JwtUtil jwt;                       // <— inject JWT util

    public AuthController(AuthService auth, JwtUtil jwt) {
        this.auth = auth;
        this.jwt  = jwt;
    }

    // ---------- Register ----------
    public record RegisterReq(
            String fullName,
            String firstName,
            String lastName,
            @NotBlank @Email String email,
            @NotBlank String password,
            String role,      // "PATIENT" or "DOCTOR"
            String doctorId
    ) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq r) {
        String full = (r.fullName() != null && !r.fullName().isBlank())
                ? r.fullName().trim()
                : ((nullToEmpty(r.firstName()) + " " + nullToEmpty(r.lastName())).trim());
        if (full.isBlank()) full = r.email().split("@")[0];

        Role role = Role.PATIENT;
        if (r.role() != null && !r.role().isBlank()) {
            try { role = Role.valueOf(r.role().trim().toUpperCase()); }
            catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + r.role());
            }
        }

        try {
            Map<String,Object> payload = auth.register(full, r.email().trim(), r.password(), role, r.doctorId());
            return ResponseEntity.status(HttpStatus.CREATED).body(payload);
        } catch (DataIntegrityViolationException dup) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
    }

    // ---------- Login ----------
    public record LoginReq(String identifier, String email, String password) {}

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody LoginReq r) {
        String id = (r.identifier() != null && !r.identifier().isBlank())
                ? r.identifier().trim()
                : (r.email() != null ? r.email().trim() : "");
        if (id.isBlank())      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "identifier/email required");
        if (r.password() == null || r.password().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password required");

        var lr = auth.login(id, r.password());   // role, patientId, doctorId, fullName

        // Claims you want in the token:
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role",      lr.role());
        claims.put("patientId", lr.patientId());
        claims.put("doctorId",  lr.doctorId());
        claims.put("fullName",  lr.fullName());
        claims.put("email",     id);

        String token = jwt.generateToken(id, claims); // subject first, then claims

        Map<String,Object> body = new java.util.HashMap<>();
        body.put("token", token);
        body.put("role", lr.role());
        body.put("patientId", lr.patientId());
        body.put("doctorId", lr.doctorId());
        body.put("fullName", lr.fullName());
        body.put("email", id);



        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // optional convenience
                .body(body);
    }

    private static String nullToEmpty(String s){ return s == null ? "" : s.trim(); }
}
