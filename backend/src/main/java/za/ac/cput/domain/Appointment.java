/* Appointment.java
Appointment model
Author : Nathan Antha(219474893)
Date: March 2025*/
package za.ac.cput.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    int appointmentID;
    LocalDate date;
    LocalTime time;
    String status;
    int patientID;
    int doctorID;

    public Appointment(AppointmentBuilder builder) {
        this.appointmentID = builder.appointmentID;
        this.date = builder.date;
        this.time = builder.time;
        this.status = builder.status;
        this.patientID = builder.patientID;
        this.doctorID = builder.doctorID;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    /*public int getPatientID() {
        return patientID;
    }*/

    public int getDoctorID() {
        return doctorID;
    }

    public static class AppointmentBuilder {
        int appointmentID;
        LocalDate date;
        LocalTime time;
        String status;
        int patientID;
        int doctorID;

        public AppointmentBuilder setAppointmentID(int appointmentID) {
            this.appointmentID = appointmentID;
            return this;

        }

        public AppointmentBuilder setDate(LocalDate date) {
            this.date = date;
            return this;

        }

        public AppointmentBuilder setTime(LocalTime time) {
            this.time = time;
            return this;

        }

        public AppointmentBuilder setPatientID(int patientID) {
            this.patientID = patientID;
            return this;

        }

        public AppointmentBuilder setStatus(String status) {
            this.status = status;
            return this;

        }

        public AppointmentBuilder setDoctorID(int doctorID) {
            this.doctorID = doctorID;
            return this;

        }
        public Appointment build() {
            return new Appointment(this);
        }
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentID=" + appointmentID +
                ", date=" + date +
                ", time=" + time +
                ", status='" + status + '\'' +
                ", patientID=" + patientID +
                ", doctorID=" + doctorID +
                '}';
    }
}

