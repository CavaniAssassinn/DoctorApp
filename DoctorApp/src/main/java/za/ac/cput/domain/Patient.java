/* PatientFactory.java
Patient model class
Author : Bruneez Apollis(222127600)
Date: March 2025
 */
package za.ac.cput.domain;

import java.time.LocalDate;


public class Patient {
    private final String patientID;
    private final String patientName;
    private final String patientSurname;
    private final LocalDate dateOfBirth;


    public String getPatientID() {
        return patientID;
    }
    public String getPatientName(){return patientName;}
    public String getPatientSurname(){return patientSurname;}
    public LocalDate getDateOfBirth(){return dateOfBirth;}


    private Patient(Builder builder) {
        this.patientID = builder.patientID;
        this.patientName = builder.patientName;
        this.patientSurname = builder.patientSurname;
        this.dateOfBirth = builder.dateOfBirth;

    }

    // Inner Builder class
    public static class Builder {
        private String patientID;
        private String patientName;
        private String patientSurname;
        private LocalDate dateOfBirth;


        public Builder setPatientID(String patientID) {
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
                '}';
    }


}


