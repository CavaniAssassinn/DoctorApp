/* FactoryPatientTest.java
Patient model
Author : Bruneez Apollis(222127600)
Date: March 2025
 */package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Patient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FactoryPatientTest {

    @Test
    public void createPatient_ValidInput() {
        int patientID = 101;
        String patientName = "John";
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);

        assertNotNull(patient);
        assertEquals(patientID, patient.getPatientID());
        assertEquals(patientName, patient.getPatientName());
        assertEquals(patientSurname, patient.getPatientSurname());
        assertEquals(dateOfBirth, patient.getDateOfBirth());
    }

    @Test
    public void createPatient_NullPatientID() {
        int patientID = 0;
        String patientName = "John";
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }


    @Test
    public void createPatient_NullPatientName() {
        int patientID = 102;
        String patientName = null;
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_EmptyPatientName() {
        int patientID = 103;
        String patientName = "";
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_NullDateOfBirth() {
        int patientID = 104;
        String patientName = "Jane";
        String patientSurname = "Smith";
        LocalDate dateOfBirth = null;

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_ValidDifferentPatient() {
        int patientID = 105;
        String patientName = "Alice";
        String patientSurname = "Brown";
        LocalDate dateOfBirth = LocalDate.of(1985, 10, 20);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);

        assertNotNull(patient);
        assertEquals(patientID, patient.getPatientID());
        assertEquals(patientName, patient.getPatientName());
        assertEquals(patientSurname, patient.getPatientSurname());
        assertEquals(dateOfBirth, patient.getDateOfBirth());
    }
}