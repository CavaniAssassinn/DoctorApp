/* PatientFactory.java
Patient model class
Author : Bruneez Apollis(222127600)
Date: March 2025
 */package za.ac.cput.factory;

import za.ac.cput.domain.Patient;
import za.ac.cput.util.Helper;
import java.time.LocalDate;

public class FactoryPatient {
    public static Patient createPatient(int patientID, String patientName, String patientSurname,
                                        LocalDate dateOfBirth) {

        // Validation logic: Ensure patientID is valid (greater than 0), name and surname are not null/empty,
        // and dateOfBirth is not null.
        if (patientID <= 0 || Helper.isNullOrEmpty(patientName) || Helper.isNullOrEmpty(patientSurname) || Helper.isNull(dateOfBirth)) {
            return null;  // Return null if any attribute is invalid
        }

        // Return a valid Patient object using the builder pattern
        return new Patient.Builder()
                .setPatientID(patientID)
                .setPatientName(patientName)
                .setPatientSurname(patientSurname)
                .setDateOfBirth(dateOfBirth)
                .build();
    }
}
