package main.java.za.ac.cput.factory;

import main.java.za.ac.cput.domain.Doctor;

public class FactoryDoctor {
    public static Doctor createDoctor(String doctorID, String specialization, boolean availability) {
        return new Doctor.DoctorBuilder()
                .setDoctorID(doctorID)
                .setSpecialization(specialization)
                .isAvailable(availability)
                .build();
    }
}
