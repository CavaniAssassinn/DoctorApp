package main.java.za.ac.cput.test;
import main.java.za.ac.cput.domain.Doctor;
import main.java.za.ac.cput.factory.FactoryDoctor;
import main.java.za.ac.cput.repository.RepositoryDoctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
public class RepositoryDoctorTest {
    private RepositoryDoctor repository;
    private Doctor doctor1;
    private Doctor doctor2;

    @BeforeEach
    void setUp(){
        repository = new RepositoryDoctor();
        doctor1 = FactoryDoctor.createDoctor("D1 ", "Cardiology ", true);
        doctor2 = FactoryDoctor.createDoctor("D2 ", "Gynecology ", false);
    }

    @Test
    @DisplayName("Test Adding Doctors")
    void testAddDoctor(){
        repository.addDoctor(doctor1);
        repository.addDoctor(doctor2);

        assertEquals(2, repository.getDoctorCount());
    }

    @Test
    @DisplayName("Test Removing a Doctor")
    void testRemoveDoctor(){
        repository.addDoctor(doctor1);
        repository.addDoctor(doctor2);

        assertTrue(repository.removeDoctor("D1 "));
        assertEquals(1, repository.getDoctorCount());
        assertFalse(repository.removeDoctor("D3 "));
    }

    @Test
    @DisplayName("Test to find Doctor by ID")
    void testFindDoctorById() {
        repository.addDoctor(doctor1);
        repository.addDoctor(doctor2);

        Optional<Doctor> foundDoctor = repository.findDoctorById("D1 ");
        assertTrue(foundDoctor.isPresent());
        assertEquals("Cariology " ,foundDoctor.get().getSpecialization());

        Optional<Doctor> notFoundDoctor = repository.findDoctorById("D3 ");
        assertFalse(notFoundDoctor.isPresent());
    }

    @Test
    @DisplayName("Test Doctor Count")
    void testDoctorCount(){
        assertEquals(0, repository.getDoctorCount());
        repository.addDoctor(doctor1);
        assertEquals(1, repository.getDoctorCount());
        repository.addDoctor(doctor2);
        assertEquals(2, repository.getDoctorCount());
    }
}

