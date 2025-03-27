package za.ac.cput.factory;

import za.ac.cput.domain.Doctor;

public class FactoryDoctor {
    public static Doctor createDoctor(String doctorID, String specialization, boolean availability) {
        return new Doctor.DoctorBuilder()
                .setDoctorID(doctorID)
                .setSpecialization(specialization)
                .isAvailable(availability)
                .build();
    }
}
