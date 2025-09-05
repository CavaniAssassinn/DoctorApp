package za.ac.cput.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.service.DoctorScheduleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
public class DoctorScheduleController {

    private final DoctorScheduleService svc;

    public DoctorScheduleController(DoctorScheduleService svc){ this.svc = svc; }

    // PUT /doctor/availability?doctorId=...
    @PutMapping("/doctor/availability")
    public ResponseEntity<Void> setAvailability(@RequestParam(required = false) UUID doctorId,
                                                @RequestBody List<DoctorScheduleService.AvailabilityDTO> rules){
        UUID id = requireDoctorId(doctorId);
        svc.setWeeklyAvailability(id, rules);
        return ResponseEntity.noContent().build();
    }

    // POST /doctor/timeoff?doctorId=...
    record TimeOffDTO(String start, String end, String reason) {}
    @PostMapping("/doctor/timeoff")
    public ResponseEntity<Void> addTimeOff(@RequestParam(required = false) UUID doctorId,
                                           @RequestBody TimeOffDTO dto){
        UUID id = requireDoctorId(doctorId);
        svc.addTimeOff(id, LocalDateTime.parse(dto.start()), LocalDateTime.parse(dto.end()), dto.reason());
        return ResponseEntity.ok().build();
    }

    // GET /doctors/{id}/slots?date=YYYY-MM-DD
    @GetMapping("/doctors/{id}/slots")
    public List<String> slots(@PathVariable UUID id, @RequestParam LocalDate date){
        return svc.getAvailableSlots(id, date)
                .stream()
                .map(LocalDateTime::toString) // "2025-09-02T09:00"
                .collect(Collectors.toList());
    }

    /** For now, require doctorId via query param. Later, derive from the authenticated principal/JWT. */
    private static UUID requireDoctorId(UUID id){
        if (id == null) throw new IllegalArgumentException("doctorId query parameter is required (temporary until JWT is wired)");
        return id;

    }


}
