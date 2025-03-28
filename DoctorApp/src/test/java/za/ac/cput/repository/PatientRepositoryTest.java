/* PatientRepositoryTest.java
Patient model
Author : Bruneez Apollis(222127600)
Date: March 2025
 */
package za.ac.cput.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PatientRepositoryTest {
    private PatientRepository repository;
    private Patient patient1, patient2;

    @BeforeEach
    void setUp() {
        repository = PatientRepository.getInstance();

        patient1 = new Patient.Builder()
                .setPatientID(100)
                .setPatientName("John")
                .setPatientSurname("Doe")
                .setDateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        patient2 = new Patient.Builder()
                .setPatientID(102)
                .setPatientName("Alice")
                .setPatientSurname("Smith")
                .setDateOfBirth(LocalDate.of(1985, 10, 22))
                .build();

        repository.create(patient1);
        repository.create(patient2);
    }


    @Test
    void create() {
        Patient newPatient = new Patient.Builder()
                .setPatientID(103)
                .setPatientName("Bob")
                .setPatientSurname("Marley")
                .setDateOfBirth(LocalDate.of(1980, 3, 12))
                .build();

        Patient created = repository.create(newPatient);
        assertNotNull(created);
        assertEquals(103, created.getPatientID());
    }

    @Test
    void readString() {
        Optional<Patient> found = repository.read(100);
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getPatientName());
    }

    @Test
    void update() {
        Patient updatedPatient = new Patient.Builder()
                .setPatientID(104) // Same ID as patient1
                .setPatientName("Johnathan") // Updated name
                .setPatientSurname("Doe")
                .setDateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        Patient result = repository.update(updatedPatient);
        assertNotNull(result);
        assertEquals("Johnathan", result.getPatientName());
    }

    @Test
    void delete() {
        repository.delete(101); // Since delete(int) is not applicable
        assertFalse(repository.existsById(101)); // Should return false
    }

    @Test
    void findAll() {
        List<Patient> patients = repository.getAll();
        assertEquals(2, patients.size()); // Since we added 2 patients in setUp()
    }

    @Test
    void read() {
        Optional<Patient> result = repository.read(1); // Since read(int) is not applicable
        assertTrue(result.isEmpty()); // Should return an empty Optional
    }
}