/* FactoryDoctor.java
Doctor model class
Author : Matthew Michael Engelbrecht(222381086)
Date : March 2025
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Doctor;

public class FactoryDoctor {
    public static Doctor createDoctor(String doctorID, String specialization, boolean availability) {

        if (doctorID == null || doctorID.trim().isEmpty()) {
            throw new IllegalStateException("Doctor ID cannot be null or empty");
        }

        // Validate that specialization is not null or empty
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalStateException("Specialization cannot be null or empty");
        }
        return new Doctor.DoctorBuilder()
                .setDoctorID(doctorID)
                .setSpecialization(specialization)
                .isAvailable(availability)
                .build();
    }
}
