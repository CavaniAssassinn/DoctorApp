package za.ac.cput.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Appointment;
import za.ac.cput.util.Helper;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentFactoryTest {

    private int appointmentID;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private int patientID;
    private int doctorID;

    @BeforeEach
    void setUp() {
        appointmentID = Helper.generateAppointmentID();
        date = LocalDate.now().plusDays(1);
        time = LocalTime.of(14, 0);
        status = "Scheduled";
        patientID = 101;
        doctorID = 202;
    }

    @Test
    void createAppointment_success() {
        Appointment appointment = AppointmentFactory.createAppointment(appointmentID, date, time, status, patientID, doctorID);

        assertNotNull(appointment);
        assertEquals(appointmentID, appointment.getAppointmentID());
        assertEquals(date, appointment.getDate());
        assertEquals(time, appointment.getTime());
        assertEquals(status, appointment.getStatus());
        assertEquals(doctorID, appointment.getDoctorID());
    }

    @Test
    void createAppointment_invalidDate_shouldThrowException() {
        LocalDate pastDate = LocalDate.of(2023, 1, 1); // Past date

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AppointmentFactory.createAppointment(appointmentID, pastDate, time, status, patientID, doctorID)
        );

        assertEquals("Appointment must be in the future.", exception.getMessage());
    }

    @Test
    void createAppointment_nullStatus_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AppointmentFactory.createAppointment(appointmentID, date, time, null, patientID, doctorID)
        );

        assertEquals("Appointment status cannot be null or empty", exception.getMessage()); // Updated message
    }
}
