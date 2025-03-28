package za.ac.cput.factory;

import za.ac.cput.domain.Appointment;
import za.ac.cput.util.Helper;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentFactory {


 public static Appointment createAppointment(int appointmentID, LocalDate date, LocalTime time, String status, int patientID, int doctorID) {


  // Validate inputs
  Helper.validateNotNull(date, "Appointment date cannot be null");
  Helper.validateNotNull(time, "Appointment time cannot be null");
  Helper.validateNotNullOrEmpty(status, "Appointment status cannot be null or empty");
  Helper.validatePositiveNumber(patientID, "Patient ID must be a positive number");
  Helper.validatePositiveNumber(doctorID, "Doctor ID must be a positive number");

  // Ensure the appointment date is in the future
  if (date.isBefore(LocalDate.now())) {
   throw new IllegalArgumentException("Appointment must be in the future.");
  }

  return new Appointment.AppointmentBuilder()
         .setAppointmentID( appointmentID)
         .setDate(date)
         .setTime(time)
         .setStatus(status)
         .setPatientID(patientID)
         .setDoctorID(doctorID)
         .build();
  }
}
