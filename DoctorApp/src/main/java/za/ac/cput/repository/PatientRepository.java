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

public  class PatientRepository implements IRepository<Patient>{
    private final List<Patient> patients = new ArrayList<>();

    @Override
    public Patient create(Patient patient) {
        patients.add(patient);
        return patient;
    }


    @Override
    public Optional<Patient> readString(String id) { // Changed int to String
        return patients.stream()
                .filter(patient -> patient.getPatientID().equals(id)) // String comparison
                .findFirst();
    }

    @Override
    public Patient update(Patient patient) {
        Optional<Patient> existingPatient = readString(patient.getPatientID());
        if (existingPatient.isPresent()) {
            deleteString(patient.getPatientID()); // Ensure valid deletion
            patients.add(patient);
            return patient;
        }
        return null; // Return null if patient doesn't exist
    }


    // Implement delete(int id) - Not applicable for Patient
    @Override
    public boolean delete(int id) {
        return false;
      //  throw new UnsupportedOperationException("Patient ID is a String, not an int.");
    }


    @Override
    public List<Patient> findAll() {
        return new ArrayList<>(patients);
    }

    // Implementing read(int id) - Not applicable for Patient,
    @Override
    public Optional<Patient> read(int id) {
        return Optional.empty();  // or throw an exception
        //throw new UnsupportedOperationException("Patient ID is a String, not an int.");

    }


    @Override
    public boolean deleteString(String id) {
        return patients.removeIf(patient -> patient.getPatientID().equals(id));
    }
}

