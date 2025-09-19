// src/main/java/za/ac/cput/auth/AuthService.java
package za.ac.cput.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.repository.PatientRepository;
import za.ac.cput.domain.Patient;
import java.util.UUID;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final PatientRepository patients;

    public AuthService(UserRepository users, PasswordEncoder encoder , PatientRepository patients) {
        this.users = users;
        this.encoder = encoder;
        this.patients = patients;

    }
    private UUID ensurePatientLink(User u) {
        if (u.getRole() != Role.PATIENT) return null;
        if (u.getPatientId() != null) return u.getPatientId();

        // 1) Try to find existing Patient by email
        var existing = patients.findByEmailIgnoreCase(u.getEmail());
        if (existing.isPresent()) {
            var p = existing.get();
            u.setPatientId(p.getId());
            users.save(u);
            return p.getId();
        }
        Patient p = new Patient(u.getFullName(), u.getEmail(), null);
        patients.save(p);
        u.setPatientId(p.getId());
        users.save(u);
        return p.getId();
    }
    /* -------- DTO returned to clients (controller/JavaFX) -------- */
    public static record LoginResp(String role, String patientId, String doctorId, String fullName) {}

    /* -------- Public API used by AuthController -------- */

    /** Bridge that dispatches to patient/doctor registration. */
    public Map<String, Object> register(String fullName,
                                        String email,
                                        String rawPassword,
                                        Role role,
                                        String doctorId) {
        final Role r = (role == null) ? Role.PATIENT : role;
        return switch (r) {
            case PATIENT -> registerPatient(fullName, email, rawPassword);
            case DOCTOR  -> registerDoctor(fullName, email, rawPassword, doctorId);
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unsupported role: " + r);
            // add more roles if you later extend Role enum
        };
    }

    /** Login by email + password. Returns role + ids + full name. */
    public LoginResp login(String email, String rawPassword) {
        String e = norm(email);
        var u = users.findByEmailIgnoreCase(e)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (u.getPasswordHash() == null || !encoder.matches(rawPassword, u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Make sure PATIENT users have a linked patientId
        UUID pid = (u.getRole() == Role.PATIENT) ? ensurePatientLink(u) : u.getPatientId();
        UUID did = u.getDoctorId();

        return new LoginResp(
                u.getRole().name(),
                pid == null ? null : pid.toString(),
                did == null ? null : did.toString(),
                u.getFullName()
        );
    }

    /* -------- Registration helpers -------- */

    @Transactional
    public Map<String, Object> registerPatient(String fullName,
                                               String email,
                                               String rawPassword) {
        validateBasics(fullName, email, rawPassword);

        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User u = new User();
        u.setFullName(fullName.trim());
        u.setEmail(norm(email));
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole(Role.PATIENT);
        // If you also create a Patient row elsewhere, set u.setPatientId(patientUuid) here.
        users.save(u);

        return Map.of(
                "ok", true,
                "id", u.getId().toString(),
                "role", u.getRole().name(),
                "fullName", u.getFullName()
        );
    }

    @Transactional
    public Map<String, Object> registerDoctor(String fullName,
                                              String email,
                                              String rawPassword,
                                              String doctorId) {
        validateBasics(fullName, email, rawPassword);

        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User u = new User();
        u.setFullName(fullName.trim());
        u.setEmail(norm(email));
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole(Role.DOCTOR);

        // If you already have a Doctor entity/UUID, link it:
        Optional.ofNullable(parseUuid(doctorId)).ifPresent(u::setDoctorId);

        users.save(u);

        return Map.of(
                "ok", true,
                "id", u.getId().toString(),
                "role", u.getRole().name(),
                "fullName", u.getFullName()
        );
    }

    /* -------- Utilities -------- */

    private static void validateBasics(String fullName, String email, String rawPassword) {
        if (isBlank(fullName))  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName required");
        if (isBlank(email))     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email required");
        if (isBlank(rawPassword)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password required");
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String norm(String s) { return (s == null) ? "" : s.trim().toLowerCase(); }

    private static UUID parseUuid(String s) {
        if (isBlank(s)) return null;
        try { return UUID.fromString(s.trim()); } catch (Exception ignored) { return null; }
    }
}
