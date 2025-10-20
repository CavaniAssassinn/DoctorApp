package za.ac.cput.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @PostMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public Map<String,Object> book(@RequestBody Map<String,String> req){
        // create appointment for this patient
        return Map.of("ok", true);
    }
}
