/* PatientRepository.java
Patient model
Author : Bruneez Apollis(222127600)
Date: March 2025
 */
package za.ac.cput.repository;


import za.ac.cput.domain.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public  class PatientRepository implements IPatientRepository{

    private static PatientRepository repository = null; // Singleton instance
    private final List<Patient> patients;

    // Private constructor for Singleton
    private PatientRepository() {
        patients = new ArrayList<>();
    }

    // Singleton getInstance method
    public static PatientRepository getInstance() {
        if (repository == null) {
            repository = new PatientRepository();
        }
        return repository;
    }

    @Override
    public Patient create(Patient patient) {
        patients.add(patient);
        return patient;
    }

    @Override
    public Optional<Patient> read(Integer id) { // Using Integer for Patient ID
        return patients.stream()
                .filter(patient -> patient.getPatientID()==id) // Integer comparison
                .findFirst();
    }

    @Override
    public Patient update(Patient patient) {
        Optional<Patient> existingPatient = read(patient.getPatientID());
        if (existingPatient.isPresent()) {
            delete(patient.getPatientID()); // Remove old patient
            patients.add(patient);
            return patient;
        }
        return null; // Patient not found
    }

    @Override
    public void delete(Integer id) {
        patients.removeIf(patient -> patient.getPatientID()==id);
    }

    @Override
    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }

    @Override
    public boolean existsById(Integer id) {
        return patients.stream().anyMatch(patient -> patient.getPatientID()==id);
    }
}

