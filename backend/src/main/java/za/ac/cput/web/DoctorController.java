package za.ac.cput.web;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Doctor;
import za.ac.cput.repository.DoctorRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/doctors")
@CrossOrigin // remove if you already handle CORS globally
public class DoctorController {

    private final DoctorRepository repo;
    public DoctorController(DoctorRepository repo){ this.repo = repo; }

    // Public search: GET /doctors?q=...&city=...&speciality=...
    @GetMapping
    @PermitAll
    public List<Doctor> search(@RequestParam(required = false) String q,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false, name = "speciality") String spec) {
        return repo.search(emptyToNull(q), emptyToNull(city), emptyToNull(spec));
    }

    // Read one: authenticated patients or doctors
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public Doctor one(@PathVariable UUID id) {
        return repo.findById(id).orElseThrow();
    }

    // Create: only doctors can create (adjust to your policy)
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public Doctor create(@RequestBody @Valid Doctor doc) {
        return repo.save(doc);
    }

    private static String emptyToNull(String s){ return (s == null || s.isBlank()) ? null : s; }
}
