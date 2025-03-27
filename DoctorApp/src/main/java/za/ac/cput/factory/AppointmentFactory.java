package za.ac.cput.factory;

import za.ac.cput.domain.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentFactory {
 public static Appointment createAppointment(int appointmentID, LocalDate date, LocalTime time, String status, int patientID, int doctorID) {
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
