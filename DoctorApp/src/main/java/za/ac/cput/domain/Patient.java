/* PatientFactory.java
Patient model class
Author : Bruneez Apollis(222127600)
Date: March 2025
 */
package main.java.za.ac.cput.domain;

import java.time.LocalDate;
import java.util.List;

public class Patient {
    private final int patientID;
    private final String patientName;
    private final String patientSurname;
    private final LocalDate dateOfBirth;
    private final String medicalHistory;
    private final List<Integer> appointmentsIDs;

    private Patient(Builder builder) {
        this.patientID = builder.patientID;
        this.patientName = builder.patientName;
        this.patientSurname = builder.patientSurname;
        this.dateOfBirth = builder.dateOfBirth;
        this.medicalHistory = builder.medicalHistory;
        this.appointmentsIDs = builder.appointmentsIDs;
    }

    // Inner Builder class
    public static class Builder {
        private int patientID;
        private String patientName;
        private String patientSurname;
        private LocalDate dateOfBirth;
        private String medicalHistory;
        private List<Integer> appointmentsIDs;

        public Builder setPatientID(int patientID) {
            this.patientID = patientID;
            return this;
        }

        public Builder setPatientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public Builder setPatientSurname(String patientSurname) {
            this.patientSurname = patientSurname;
            return this;
        }

        public Builder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setMedicalHistory(String medicalHistory) {
            this.medicalHistory = medicalHistory;
            return this;
        }

        public Builder setAppointmentsIDs(List<Integer> appointmentsIDs) {
            this.appointmentsIDs = appointmentsIDs;
            return this;
        }

        public Patient build() {
            return new Patient(this);
        }
    }

    @Override
    public String toString() {
        return "Patient{" +
                "PatientID=" + patientID +
                ", name='" + patientName + '\'' +
                ", surname='" + patientSurname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", appointmentsIDs=" + appointmentsIDs +
                '}';
    }
}


