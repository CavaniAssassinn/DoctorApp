package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Patient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FactoryPatientTest {

    @Test
    public void createPatient_ValidInput() {
        String patientID = "P001";
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
        String patientID = null;
        String patientName = "John";
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_EmptyPatientID() {
        String patientID = "";
        String patientName = "John";
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_NullPatientName() {
        String patientID = "P002";
        String patientName = null;
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_EmptyPatientName() {
        String patientID = "P002";
        String patientName = "";
        String patientSurname = "Doe";
        LocalDate dateOfBirth = LocalDate.of(1990, 5, 15);

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_NullDateOfBirth() {
        String patientID = "P003";
        String patientName = "Jane";
        String patientSurname = "Smith";
        LocalDate dateOfBirth = null;

        Patient patient = FactoryPatient.createPatient(patientID, patientName, patientSurname, dateOfBirth);
        assertNull(patient);
    }

    @Test
    public void createPatient_ValidDifferentPatient() {
        String patientID = "P004";
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