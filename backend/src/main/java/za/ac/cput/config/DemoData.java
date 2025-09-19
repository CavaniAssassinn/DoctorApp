// src/main/java/za/ac/cput/config/DemoData.java
package za.ac.cput.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.ac.cput.auth.Role;
import za.ac.cput.auth.User;
import za.ac.cput.auth.UserRepository;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.Doctor;
import za.ac.cput.domain.Patient;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.repository.DoctorRepository;
import za.ac.cput.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class DemoData {

    private static final String DEMO_PASSWORD = "pass123"; // demo only

    @Bean
    CommandLineRunner seedEverything(
            DoctorRepository doctors,
            PatientRepository patients,
            AppointmentRepository appts,
            UserRepository users,
            PasswordEncoder encoder
    ) {
        return args -> {
            // ===== Doctors (with users) =====
            // ---- Doctors ----
            Doctor d1 = ensureDoctor(doctors, "Nathan Antha",       "Cardiology",   "Cape Town");
            Doctor d2 = ensureDoctor(doctors, "Matthew Engelbrecht", "Dermatology",  "Stellenbosch");
            Doctor d3 = ensureDoctor(doctors, "Bruneez Appolis",     "Pediatrics",   "Johannesburg");
            Doctor d4 = ensureDoctor(doctors, "Nompu Bhebhe",        "Neurology",    "Durban");

            // ---- Patients ----
            Patient p1 = ensurePatient(patients, "Ava Adams",   "ava@demo.com",   "0831111111");
            Patient p2 = ensurePatient(patients, "Liam Lewis",  "liam@demo.com",  "0832222222");
            Patient p3 = ensurePatient(patients, "Noah Naidoo", "noah@demo.com",  "0833333333");
            Patient p4 = ensurePatient(patients, "Zoe Zulu",    "zoe@demo.com",   "0834444444");

            // ---- Users (Doctors) ----
            ensureDoctorUser(users, encoder, d1, "nathan@clinic.com");
            ensureDoctorUser(users, encoder, d2, "matthew@clinic.com");
            ensureDoctorUser(users, encoder, d3, "bruneez@clinic.com");
            ensureDoctorUser(users, encoder, d4, "nompu@clinic.com");

            // ---- Users (Patients) ----
            ensurePatientUser(users, encoder, p1, "ava@demo.com");
            ensurePatientUser(users, encoder, p2, "liam@demo.com");
            ensurePatientUser(users, encoder, p3, "noah@demo.com");
            ensurePatientUser(users, encoder, p4, "zoe@demo.com");

            LocalDate today = LocalDate.now();
            scheduleIfMissing(appts, d1, p1, LocalDateTime.of(today, LocalTime.of(9,  0)));
            scheduleIfMissing(appts, d1, p2, LocalDateTime.of(today, LocalTime.of(9, 30)));
            scheduleIfMissing(appts, d1, p3, LocalDateTime.of(today, LocalTime.of(10, 0)));
            scheduleIfMissing(appts, d2, p4, LocalDateTime.of(today, LocalTime.of(11, 0)));
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
            u.setPasswordHash(encoder.encode(DEMO_PASSWORD));
            u.setRole(Role.DOCTOR);
            u.setDoctorId(d.getId());   // <-- critical: tie User to Doctor
            users.save(u);
        }
    }

    private static Doctor ensureDoctor(DoctorRepository repo, String name, String spec, String city) {
        return repo.findByFullNameIgnoreCase(name)
                .orElseGet(() -> repo.save(new Doctor(name, spec, city)));
    }

    private static Patient ensurePatient(PatientRepository repo, String name, String email, String phone) {
        return repo.findByEmailIgnoreCase(email)
                .orElseGet(() -> repo.save(new Patient(name, email, phone)));
    }

    private static void ensureDoctorUser(UserRepository users, PasswordEncoder enc, Doctor d, String email) {
        Optional<User> u = users.findByEmailIgnoreCase(email);
        if (u.isPresent()) return;
        User nu = new User();
        nu.setFullName(d.getFullName());
        nu.setEmail(email.toLowerCase());
        nu.setPasswordHash(enc.encode(DEMO_PASSWORD));
        nu.setRole(Role.DOCTOR);
        nu.setDoctorId(d.getId());
        users.save(nu);
    }

    private static void ensurePatientUser(UserRepository users, PasswordEncoder enc, Patient p, String email) {
        Optional<User> u = users.findByEmailIgnoreCase(email);
        if (u.isPresent()) return;
        User nu = new User();
        nu.setFullName(p.getFullName());
        nu.setEmail(email.toLowerCase());
        nu.setPasswordHash(enc.encode(DEMO_PASSWORD));
        nu.setRole(Role.PATIENT);
        nu.setPatientId(p.getId());
        users.save(nu);
    }

    private static void scheduleIfMissing(AppointmentRepository appts, Doctor d, Patient p, LocalDateTime start) {
        if (appts.existsByDoctor_IdAndStartTime(d.getId(), start)) return;
        appts.save(new Appointment(d, p, start));  // default status = SCHEDULED
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
            u.setPasswordHash(encoder.encode(DEMO_PASSWORD));
            u.setRole(Role.PATIENT);
            u.setPatientId(p.getId());  // <-- critical: tie User to Patient
            users.save(u);
        }
    }
}
