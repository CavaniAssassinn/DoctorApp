package za.ac.cput.factory;

import za.ac.cput.domain.Doctor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FactoryDoctorTest {

    @Test
    public void createDoctor_ValidInput() {
        String doctorID = "D001";
        String specialization = "Cardiology";
        boolean availability = true;

        Doctor doctor = FactoryDoctor.createDoctor(doctorID, specialization, availability);

        assertNotNull(doctor);
        assertEquals(doctorID, doctor.getDoctorID());
        assertEquals(specialization, doctor.getSpecialization());
        assertTrue(doctor.isAvailable());
    }

    @Test
    public void createDoctor_NullDoctorID() {
        String doctorID = null;
        String specialization = "Cardiology";
        boolean availability = true;

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            FactoryDoctor.createDoctor(doctorID, specialization, availability);
        });
        assertEquals("Doctor ID  cannot be null or empty", exception.getMessage());
    }

    @Test
    public void createDoctor_EmptyDoctorID() {
        String doctorID = "";
        String specialization = "Cardiology";
        boolean availability = true;

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            FactoryDoctor.createDoctor(doctorID, specialization, availability);
        });
    }

    @Test
    public void createDoctor_NullSpecialization() {
        String doctorID = "D002";
        String specialization = null;
        boolean availability = true;

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            FactoryDoctor.createDoctor(doctorID, specialization, availability);
        });
        assertEquals("Specialization cannot be null or empty", exception.getMessage());
    }

    @Test
    public void createDoctor_EmptySpecialization() {
        String doctorID = "D002";
        String specialization = "";
        boolean availability = true;

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            FactoryDoctor.createDoctor(doctorID, specialization, availability);
        });
        assertEquals("Specialization cannot be null or empty", exception.getMessage());
    }

    @Test
    public void createDoctor_ValidAvailability(){
        String doctorID = "D003";
        String specialization = "Pediatrics";
        boolean availability = false;

        Doctor doctor = FactoryDoctor.createDoctor(doctorID, specialization, availability);

        assertNotNull(doctor);
        assertEquals(doctorID, doctor.getDoctorID());
        assertEquals(specialization, doctor.getSpecialization());
        assertFalse(doctor.isAvailable());
    }
}