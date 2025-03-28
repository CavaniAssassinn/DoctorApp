/* PatientFactory.java
Patient model class
Author : Bruneez Apollis(222127600)
Date: March 2025
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Patient;
import za.ac.cput.util.Helper;
import java.time.LocalDate;
import java.util.List;


public class FactoryPatient {
    public static Patient createPatient(int patientID, String patientName, String patientSurname,
                                        LocalDate dateOfBirth) {

        if (Helper.isValidID(patientID) ||
                Helper.isNullOrEmpty(patientName) ||
                Helper.isNullOrEmpty(patientSurname) ||
                Helper.isNull(dateOfBirth)) {
            return null; // If any attribute is invalid, return null
        }

        // Return a valid Patient object
        return new Patient.Builder()
                .setPatientID(patientID)
                .setPatientName(patientName)
                .setPatientSurname(patientSurname)
                .setDateOfBirth(dateOfBirth)
                .build();
    }
}

