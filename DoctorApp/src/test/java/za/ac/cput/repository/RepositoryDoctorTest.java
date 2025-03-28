/* RepositoryDoctorTest.java
Doctor model class
Author : Matthew Michael Engelbrecht(222381086)
Date : March 2025
 */package za.ac.cput.repository;

import za.ac.cput.domain.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryDoctorTest {
    private RepositoryDoctor repository;

    @BeforeEach
    public void setUp() {
        repository = RepositoryDoctor.getInstance();
        repository.clear();  // Reset the repository before each test

        // Create doctors for testing
        repository.create(new Doctor.DoctorBuilder()
                .setDoctorID("D001")
                .setSpecialization("Cardiology")
                .isAvailable(true)
                .build());
        repository.create(new Doctor.DoctorBuilder()
                .setDoctorID("D002")
                .setSpecialization("Pediatrics")
                .isAvailable(true)
                .build());
        // Add other doctors if needed...
    }

    @Test
    public void testCreateDoctor() {
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D007")  // Ensure this ID is unique
                .setSpecialization("Neurology")
                .isAvailable(true)
                .build();

        Doctor createdDoctor = repository.create(doctor);

        assertNotNull(createdDoctor);
        assertEquals(doctor.getDoctorID(), createdDoctor.getDoctorID());
    }

    @Test
    public void testReadDoctor() {
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D008")
                .setSpecialization("Orthopedics")
                .isAvailable(true)
                .build();
        repository.create(doctor);

        Optional<Doctor> foundDoctor = repository.readString("D008");

        assertTrue(foundDoctor.isPresent());
        assertEquals(doctor.getDoctorID(), foundDoctor.get().getDoctorID());
    }

    @Test
    public void testDeleteDoctor() {
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D009")
                .setSpecialization("Dermatology")
                .isAvailable(true)
                .build();
        repository.create(doctor);

        repository.delete("D009");

        Optional<Doctor> deletedDoctor = repository.read("D009");
        assertTrue(deletedDoctor.isEmpty(), "Doctor should be deleted, but it was found.");
    }

    @Test
    public void testFindAllDoctors() {
        Doctor doctor1 = new Doctor.DoctorBuilder()
                .setDoctorID("D005")
                .setSpecialization("Cardiology")
                .isAvailable(true)
                .build();

        Doctor doctor2 = new Doctor.DoctorBuilder()
                .setDoctorID("D006")
                .setSpecialization("Pediatrics")
                .isAvailable(false)
                .build();
        repository.create(doctor1);
        repository.create(doctor2);

        List<Doctor> allDoctors = repository.findAll();

        assertNotNull(allDoctors);
        assertEquals(4, allDoctors.size());
    }
}
