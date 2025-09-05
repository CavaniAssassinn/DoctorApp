/* AppointmentFactory.java
Appointment model
Author : Nathan Antha(219474893)
Date: March 2025*/
package za.ac.cput.factory;

import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.Doctor;
import za.ac.cput.domain.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class AppointmentFactory {

 /**
  * Build a JPA Appointment from domain objects + date/time.
  * Validates nulls and ensures the slot is not in the past.
  */
 public static Appointment create(Doctor doctor,
                                  Patient patient,
                                  LocalDate date,
                                  LocalTime time,
                                  String statusOpt /* may be null */) {

  // Basic validation (replace with your Helper.* if you prefer)
  Objects.requireNonNull(doctor,  "Doctor cannot be null");
  Objects.requireNonNull(patient, "Patient cannot be null");
  Objects.requireNonNull(date,    "Appointment date cannot be null");
  Objects.requireNonNull(time,    "Appointment time cannot be null");

  LocalDateTime start = LocalDateTime.of(date, time);
  if (start.isBefore(LocalDateTime.now())) {
   throw new IllegalArgumentException("Appointment must be in the future.");
  }

  // Create the entity
  Appointment a = new Appointment(doctor, patient, start);

  // Optional status override
  if (statusOpt != null && !statusOpt.isBlank()) {
   Appointment.Status s = Appointment.Status.valueOf(statusOpt.toUpperCase());
   a.setStatus(s);
  }

  return a;
 }
}
