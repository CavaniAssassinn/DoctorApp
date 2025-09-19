// za/ac/cput/web/AuthController.java
package za.ac.cput.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.auth.AuthService;
import za.ac.cput.auth.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }
    // in AuthController

    // Accept either fullName OR first+last. Role optional; default PATIENT.
    public record RegisterReq(
            String fullName,
            String firstName,
            String lastName,
            @NotBlank @Email String email,
            @NotBlank String password,
            String role,          // "PATIENT" or "DOCTOR" (optional)
            String doctorId       // only when DOCTOR
    ) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq r) {
        // Derive fullName if not provided
        String full = (r.fullName() != null && !r.fullName().isBlank())
                ? r.fullName().trim()
                : ((nullToEmpty(r.firstName()) + " " + nullToEmpty(r.lastName())).trim());

        if (full.isBlank()) {
            // final fallback: use email local-part
            full = r.email().split("@")[0];
        }
        if (full.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName or firstName+lastName required");
        }

        // Parse role safely; default to PATIENT
        Role role = Role.PATIENT;
        if (r.role() != null && !r.role().isBlank()) {
            try {
                role = Role.valueOf(r.role().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + r.role());
            }
        }

        try {
            Map<String,Object> payload = auth.register(full, r.email().trim(), r.password(), role, r.doctorId());
            return ResponseEntity.status(HttpStatus.CREATED).body(payload);
        } catch (DataIntegrityViolationException dup) {
            // e.g. unique email constraint
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
    }
    public record LoginReq(String identifier, String email, String password) {}

    @PostMapping("/login")
    public AuthService.LoginResp login(@RequestBody LoginReq r) {
        String id = (r.identifier() != null && !r.identifier().isBlank())
                ? r.identifier().trim()
                : (r.email() != null ? r.email().trim() : "");
        if (id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "identifier/email required");
        }
        if (r.password() == null || r.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password required");
        }
        return auth.login(id, r.password());
    }
    private static String nullToEmpty(String s){ return s == null ? "" : s.trim(); }
}
