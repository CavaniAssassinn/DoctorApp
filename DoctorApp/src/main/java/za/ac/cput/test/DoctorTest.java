package za.ac.cput.test;
import za.ac.cput.domain.Doctor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {
    Doctor doctor1;
    Doctor doctor2;
    Doctor doctor3;

    @BeforeEach
    void setUp(){
        doctor1 = new Doctor.DoctorBuilder()
                .setDoctorID("D1")
                .setSpecialization("Cardiology")
                .isAvailable(true)
                .build();

        doctor2 = new Doctor.DoctorBuilder()
                .setDoctorID("D2")
                .setSpecialization("Gynecology")
                .isAvailable(false)
                .build();

        doctor3 = new Doctor.DoctorBuilder()
                .setDoctorID("D3")
                .setSpecialization("Neurology")
                .isAvailable(true)
                .build();
    }



    @Test
    void getDoctorID() {
        assertEquals("D1", doctor1.getDoctorID());
        assertEquals("D2", doctor2.getDoctorID());
        assertEquals("D3", doctor3.getDoctorID());
    }

    @Test
    void getSpecialization() {
        assertEquals("Cardiology", doctor1.getSpecialization());
        assertEquals("Gynecology", doctor2.getSpecialization());
        assertEquals("Neurology", doctor3.getSpecialization());
    }

    @Test
    void isAvailable() {
        assertTrue(doctor1.isAvailable());
        assertFalse(doctor2.isAvailable());
        assertTrue(doctor3.isAvailable());
    }

    @Test
    @DisplayName("Test Display for Doctor Info")
    void displayDoctorInfo() {
        doctor1.displayDoctorInfo();
        doctor2.displayDoctorInfo();
        doctor3.displayDoctorInfo();
    }
}
