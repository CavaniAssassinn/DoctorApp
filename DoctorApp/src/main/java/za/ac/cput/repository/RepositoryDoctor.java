
package za.ac.cput.repository;

import za.ac.cput.domain.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositoryDoctor implements IRepository<Doctor> {
    private static RepositoryDoctor instance;
    private final List<Doctor> doctors;

    private RepositoryDoctor() {
        this.doctors = new ArrayList<>();
    }

    public static RepositoryDoctor getInstance() {
        if (instance == null) {
            synchronized (RepositoryDoctor.class) {
                if (instance == null) {
                    instance = new RepositoryDoctor();
                }
            }
        }
        return instance;
    }

    @Override
    public Doctor create(Doctor doctor) {
        if (readString(doctor.getDoctorID()).isPresent()) {
            throw new IllegalArgumentException("Doctor with ID " + doctor.getDoctorID() + " already exists.");
        }
        doctors.add(doctor);
        return doctor;
    }

    @Override
    public Optional<Doctor> read(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Doctor> readString(String id) {
        return doctors.stream()
                .filter(doctor -> doctor.getDoctorID().equals(id))
                .findFirst();
    }

    @Override
    public Doctor update(Doctor doctor) {
        Optional<Doctor> existingDoctor = readString(doctor.getDoctorID());
        if (existingDoctor.isPresent()) {
            deleteString(doctor.getDoctorID());
            create(doctor);
            return doctor;
        } else {
            throw new IllegalArgumentException("Doctor with ID " + doctor.getDoctorID() + " does not exist.");
        }
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean deleteString(String id) {
        Optional<Doctor> doctorToRemove = readString(id);
        if (doctorToRemove.isPresent()) {
            doctors.remove(doctorToRemove.get());
            return true;
        }
        return false;
    }

    @Override
    public List<Doctor> findAll() {
        return new ArrayList<>(doctors);
    }
}

