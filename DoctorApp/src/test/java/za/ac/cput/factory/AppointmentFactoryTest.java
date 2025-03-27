package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Appointment;
import za.ac.cput.util.Helper;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentFactoryTest {


    @Test
    void createAppointment_success() {
        // Given
        int appointmentID = Helper.generateAppointmentID();

        LocalDate date = LocalDate.of(2025, 3, 26);
        LocalTime time = LocalTime.of(14, 0);
        String status = "Scheduled";
        int patientID = 101;
        int doctorID = 202;

        // When
        Appointment appointment = AppointmentFactory.createAppointment(appointmentID, date, time, status, patientID, doctorID);

        // Then
        assertNotNull(appointment);
        assertEquals(appointmentID, appointment.getAppointmentID());
        assertEquals(date, appointment.getDate());
        assertEquals(time, appointment.getTime());
        assertEquals(status, appointment.getStatus());
       // assertEquals(patientID, appointment.getPatientID());
        assertEquals(doctorID, appointment.getDoctorID());
    }

    @Test
    void createAppointment_invalidDate_shouldThrowException() {
        // Given
        int appointmentID = Helper.generateAppointmentID();
        LocalDate pastDate = LocalDate.of(2023, 1, 1); // Past date
        LocalTime time = LocalTime.of(14, 0);
        String status = "Scheduled";
        int patientID = 101;
        int doctorID = 202;

        // Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AppointmentFactory.createAppointment(appointmentID, pastDate, time, status, patientID, doctorID)
        );

        assertEquals("Appointment must be in the future.", exception.getMessage());
    }

    @Test
    void createAppointment_nullStatus_shouldThrowException() {
        // Given
        int appointmentID = Helper.generateAppointmentID();
        LocalDate date = LocalDate.of(2025, 3, 26);
        LocalTime time = LocalTime.of(14, 0);
        int patientID = 101;
        int doctorID = 202;

        // Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                AppointmentFactory.createAppointment(appointmentID, date, time, null, patientID, doctorID)
        );

        assertEquals("Status cannot be null or empty", exception.getMessage());
    }
}