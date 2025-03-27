package za.ac.cput.repository;

import main.java.za.ac.cput.domain.Patient;
//import main.java.za.ac.cput.util.Helper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepository implements IRepository<Patient> {
    private List<Patient> patients = new ArrayList<>();

    @Override
    public Patient create(Patient patient) {
        patients.add(patient);
        return patient;
    }

    @Override
    public Optional<Patient> read(int id) {
        return patients.stream()
                .filter(patient -> patient.getPatientID() == id)
                .findFirst();
    }

    @Override
    public Patient update(Patient patient) {
        delete(patient.getPatientID());
        patients.add(patient);
        return patient;
    }

    @Override
    public boolean delete(int id) {
        return patients.removeIf(patient -> patient.getPatientID() == id);
    }

    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(patients);
    }
}

