package za.ac.cput.repository;

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
        repository.findAll().forEach(doctor -> repository.deleteString(doctor.getDoctorID()));
    }

    @Test
    public void testCreateDoctor() {
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D001")
                .setSpecialization("Cardiology")
                .isAvailable(true)
                .build();

        Doctor createdDoctor = repository.create(doctor);

        assertNotNull(createdDoctor);
        assertEquals(doctor.getDoctorID(), createdDoctor.getDoctorID());
    }

    @Test
    public void testReadDoctor() {
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D002")
                .setSpecialization("Pediatrics")
                .isAvailable(true)
                .build();
        repository.create(doctor);

        Optional<Doctor> foundDoctor = repository.readString("D002");

        assertTrue(foundDoctor.isPresent());
        assertEquals(doctor.getDoctorID(), foundDoctor.get().getDoctorID());
    }

    @Test
    public void testUpdateDoctor() {
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D003")
                .setSpecialization("Orthopedics")
                .isAvailable(true)
                .build();
        repository.create(doctor);

        Doctor updatedDoctor = new Doctor.DoctorBuilder()
                .setDoctorID("D003")
                .setSpecialization("Orthopedics")
                .isAvailable(false)
                .build();
        repository.update(updatedDoctor);

        Optional<Doctor> foundDoctor = repository.readString("D003");
        assertTrue(foundDoctor.isPresent());
        assertFalse(foundDoctor.get().isAvailable());
    }

    @Test
    public void testDeleteDoctor(){
        Doctor doctor = new Doctor.DoctorBuilder()
                .setDoctorID("D004")
                .setSpecialization("Dermatology")
                .isAvailable(true)
                .build();
        repository.create(doctor);

        boolean deleted = repository.deleteString("D004");

        assertTrue(deleted);
        assertFalse(repository.readString("D004").isPresent());
    }

    @Test
    public void testFindAllDoctors(){
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

        assertEquals(2, allDoctors.size());
    }
}