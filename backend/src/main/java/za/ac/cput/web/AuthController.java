// AuthController.java
package za.ac.cput.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Patient;
import za.ac.cput.repository.DoctorRepository;
import za.ac.cput.repository.PatientRepository;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final PatientRepository patients;
    private final DoctorRepository doctors;

    public AuthController(PatientRepository patients, DoctorRepository doctors) {
        this.patients = patients; this.doctors = doctors;
    }

    // Patient login: email + (optional) fullName; creates if missing
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Map<String,String> body){
        String email = body.get("email");
        String name  = body.getOrDefault("fullName", "User");
        if (email == null || email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email required");

        Patient p = patients.findByEmail(email)
                .orElseGet(() -> patients.save(new Patient(name, email, "")));

        return Map.of("role","PATIENT","patientId", p.getId().toString());
    }

    // Doctor login: accepts a doctorId
    @PostMapping("/doctor-login")
    public Map<String,String> doctorLogin(@RequestBody Map<String,String> body){
        String id = body.get("doctorId");
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "doctorId required");
        UUID docId = UUID.fromString(id);
        doctors.findById(docId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "doctor not found"));
        return Map.of("role","DOCTOR","doctorId", docId.toString());
    }
}
