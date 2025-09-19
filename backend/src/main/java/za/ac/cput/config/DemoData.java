// src/main/java/za/ac/cput/config/DemoData.java
package za.ac.cput.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.ac.cput.auth.Role;
import za.ac.cput.auth.User;
import za.ac.cput.auth.UserRepository;
import za.ac.cput.domain.Doctor;
import za.ac.cput.domain.Patient;
import za.ac.cput.repository.DoctorRepository;
import za.ac.cput.repository.PatientRepository;

import java.util.UUID;

@Configuration
public class DemoData {

    private static final String DEFAULT_PASSWORD = "pass123"; // demo only

    @Bean
    CommandLineRunner seedEverything(
            DoctorRepository doctors,
            PatientRepository patients,
            UserRepository users,
            PasswordEncoder encoder
    ) {
        return args -> {
            // ===== Doctors (with users) =====
            seedDoctorWithUser(doctors, users, encoder,
                    "Nathan Antha",        "General Practitioner", "Cape Town",      "nathan@clinic.test");
            seedDoctorWithUser(doctors, users, encoder,
                    "Matthew Engelbrecht", "Cardiologist",         "Johannesburg",   "matthew@clinic.test");
            seedDoctorWithUser(doctors, users, encoder,
                    "Bruneez Appolis",     "Dermatologist",        "Pretoria",       "bruneez@clinic.test");
            seedDoctorWithUser(doctors, users, encoder,
                    "Nompu Bhebhe",        "Paediatrician",        "Durban",         "nompu@clinic.test");

            // ===== Patients (with users) =====
            seedPatientWithUser(patients, users, encoder,
                    "John Doe",   "john@demo.test",  "0123456789");
            seedPatientWithUser(patients, users, encoder,
                    "Alice Brown","alice@demo.test", "0215550000");
            seedPatientWithUser(patients, users, encoder,
                    "Bob Marley", "bob@demo.test",   "0837778888");
            seedPatientWithUser(patients, users, encoder,
                    "Jane Smith", "jane@demo.test",  "0821234567");
        };
    }

    private void seedDoctorWithUser(
            DoctorRepository doctors,
            UserRepository users,
            PasswordEncoder encoder,
            String fullName, String speciality, String city, String email
    ) {
        // Doctor
        Doctor d = doctors.findByFullNameIgnoreCase(fullName)
                .orElseGet(() -> doctors.save(new Doctor(fullName, speciality, city)));

        // User (role DOCTOR)
        if (!users.existsByEmail(email)) {
            User u = new User();
            u.setEmail(email);
            u.setFullName(fullName);
            u.setPasswordHash(encoder.encode(DEFAULT_PASSWORD));
            u.setRole(Role.DOCTOR);
            u.setDoctorId(d.getId());   // <-- critical: tie User to Doctor
            users.save(u);
        }
    }

    private void seedPatientWithUser(
            PatientRepository patients,
            UserRepository users,
            PasswordEncoder encoder,
            String fullName, String email, String phone
    ) {
        // Patient (unique by email)
        Patient p = patients.findByEmailIgnoreCase(email)
                .orElseGet(() -> patients.save(new Patient(fullName, email, phone)));

        // User (role PATIENT) with SAME email
        if (!users.existsByEmail(email)) {
            User u = new User();
            u.setEmail(email);
            u.setFullName(fullName);
            u.setPasswordHash(encoder.encode(DEFAULT_PASSWORD));
            u.setRole(Role.PATIENT);
            u.setPatientId(p.getId());  // <-- critical: tie User to Patient
            users.save(u);
        }
    }
}
