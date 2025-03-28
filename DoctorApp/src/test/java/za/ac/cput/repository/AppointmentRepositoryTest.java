/* AppointmentRepositoryTest.java
Appointment model
Author : Nathan Antha(219474893)
Date: March 2025*/
package za.ac.cput.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Appointment;
import za.ac.cput.util.Helper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentRepositoryTest {

    private AppointmentRepository repository;
    private Appointment appointment;

    @BeforeEach
    void setUp() {

        repository = AppointmentRepository.getInstance();

        appointment = new Appointment.AppointmentBuilder()
                .setAppointmentID(Helper.generateAppointmentID())
                .setDate(LocalDate.of(2025, 3, 26))
                .setTime(LocalTime.of(14, 0))
                .setStatus("Scheduled")
                .setPatientID(101)
                .setDoctorID(202)
                .build();

        repository.getAll().add(appointment);

        repository.create(appointment);

    }

    @Test
    void createAppointment_success() {

        assertNotNull(repository.read(appointment.getAppointmentID()));

    }

    @Test
    void readAppointment_success() {

        Optional<Appointment> readAppointment = repository.read(appointment.getAppointmentID());
        assertTrue(readAppointment.isPresent());
        assertEquals(appointment.getAppointmentID(), readAppointment.get().getAppointmentID());

    }

    @Test
    void updateAppointment_success() {

        Appointment updatedAppointment = new Appointment.AppointmentBuilder()
                .setAppointmentID(appointment.getAppointmentID()) // Keep the same ID
                .setDate(appointment.getDate())
                .setTime(appointment.getTime())
                .setStatus("Completed") // Change status
               // .setPatientID(appointment.getPatientID())
                .setDoctorID(appointment.getDoctorID())
                .build();

        repository.update(updatedAppointment);

        Optional<Appointment> readAppointment = repository.read(appointment.getAppointmentID());
        assertTrue(readAppointment.isPresent());
        assertEquals("Completed", readAppointment.get().getStatus());
    }

    @Test
    void deleteAppointment_success() {

        repository.delete(appointment.getAppointmentID());

        assertFalse(repository.read(appointment.getAppointmentID()).isPresent());
    }

}