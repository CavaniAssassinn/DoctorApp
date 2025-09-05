package za.ac.cput.web;

import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Doctor;
import za.ac.cput.repository.DoctorRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorRepository repo;
    public DoctorController(DoctorRepository repo){ this.repo = repo; }

    // /doctors?q=...&city=...&speciality=...
    @GetMapping
    public List<Doctor> search(@RequestParam(required=false) String q,
                               @RequestParam(required=false) String city,
                               @RequestParam(required=false, name="speciality") String spec){
        return repo.search(emptyToNull(q), emptyToNull(city), emptyToNull(spec));
    }

    @PostMapping
    public Doctor create(@RequestBody Doctor doc){ return repo.save(doc); }

    @GetMapping("/{id}")
    public Doctor one(@PathVariable UUID id){ return repo.findById(id).orElseThrow(); }

    private static String emptyToNull(String s){ return (s == null || s.isBlank()) ? null : s; }
}
