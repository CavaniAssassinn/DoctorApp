package za.ac.cput.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.Doctor;
import za.ac.cput.domain.Patient;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.repository.DoctorRepository;
import za.ac.cput.repository.PatientRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/appointments")
@Validated
public class AppointmentController {
    private final AppointmentRepository appts;
    private final DoctorRepository doctors;
    private final PatientRepository patients;

    public AppointmentController(AppointmentRepository appts, DoctorRepository doctors, PatientRepository patients) {
        this.appts = appts; this.doctors = doctors; this.patients = patients;
    }

    // --- DTOs ---
    public record BookReq(
            @NotBlank String doctorId,
            @NotBlank String patientId,
            // basic ISO-8601 date-time (e.g. 2025-09-02T09:00:00) — pattern is optional, parse still validates
            @NotBlank
            @Pattern(regexp = "^[0-9T:\\-\\.\\+]+$", message = "start must be ISO-8601 (e.g. 2025-09-02T09:00:00)")
            String start,
            String reason
    ) {}

    public record AppointmentResp(String id, String doctorId, String patientId, String start, String status) {
        static AppointmentResp of(Appointment a){
            return new AppointmentResp(
                    a.getId().toString(),
                    a.getDoctor().getId().toString(),
                    a.getPatient().getId().toString(),
                    a.getStartTime().toString(),
                    a.getStatus().name()
            );
        }
    }

    // --- Create/Book ---
    @PostMapping
    public ResponseEntity<AppointmentResp> book(@RequestBody @Validated BookReq req){
        UUID doctorId = parseUuid(req.doctorId(), "doctorId");
        UUID patientId = parseUuid(req.patientId(), "patientId");
        LocalDateTime start = parseDateTime(req.start(), "start");

        if (appts.existsByDoctor_IdAndStartTime(doctorId, start)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "slot already booked");
        }

        Doctor d = doctors.findById(doctorId).orElseThrow(() -> notFound("doctor", doctorId));
        Patient p = patients.findById(patientId).orElseThrow(() -> notFound("patient", patientId));

        Appointment a = appts.save(new Appointment(d, p, start));
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResp.of(a));
    }

    // --- "Mine" by patient ---
    @GetMapping("/me")
    public List<AppointmentResp> mine(@RequestParam UUID patientId){
        return appts.findByPatient_IdOrderByStartTimeAsc(patientId)
                .stream().map(AppointmentResp::of).toList();
    }

    // --- Update status ---
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable UUID id, @RequestBody Map<String,String> body){
        String newStatus = Optional.ofNullable(body.get("status"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required"));
        Appointment.Status s;
        try {
            s = Appointment.Status.valueOf(newStatus.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status");
        }

        Appointment a = appts.findById(id).orElseThrow(() -> notFound("appointment", id));
        a.setStatus(s);
        appts.save(a);
    }
    // --- By doctor (used by JavaFX to show today's/any day's bookings) ---
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentResp> byDoctor(@PathVariable UUID doctorId) {
        return appts.findByDoctor_Id(doctorId)
                .stream()
                .sorted(Comparator.comparing(Appointment::getStartTime))
                .map(AppointmentResp::of)
                .toList();
    }


    // --- Helpers & error mappers ---
    private static UUID parseUuid(String s, String field){
        try { return UUID.fromString(s); }
        catch (IllegalArgumentException e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must be a valid UUID"); }
    }
    private static LocalDateTime parseDateTime(String s, String field){
        try { return LocalDateTime.parse(s); }
        catch (DateTimeParseException e){ throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must be ISO-8601 (e.g. 2025-09-02T09:00:00)"); }
    }
    private static ResponseStatusException notFound(String what, UUID id){
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found: " + id);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleValidation(MethodArgumentNotValidException ex){
        Map<String,String> m = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> m.put(err.getField(), err.getDefaultMessage()));
        return m;
    }
}
