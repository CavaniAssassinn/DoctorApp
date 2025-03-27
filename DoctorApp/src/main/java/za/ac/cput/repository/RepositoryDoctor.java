package za.ac.cput.repository;
import za.ac.cput.domain.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositoryDoctor {
    private List<Doctor> doctors;

    public RepositoryDoctor(){
        this.doctors = new ArrayList<>();
    }

    public void addDoctor(Doctor doctor){
        doctors.add(doctor);
    }

    public boolean removeDoctor(String doctorID){
        Optional<Doctor> doctorToRemove = doctors.stream()
                .filter(doctor -> doctor.getDoctorID().equals(doctorID))
                .findFirst();

        if (doctorToRemove.isPresent()) {
            doctors.remove(doctorToRemove.get());
            return true;
        }
        return false;
    }

    public Optional<Doctor> findDoctorById(String doctorID) {
        return doctors.stream()
                .filter(doctor -> doctor.getDoctorID().equals(doctorID))
                .findFirst();
    }

    public List<Doctor> getAllDoctors(){
        return new ArrayList<>(doctors);
    }

    public int getDoctorCount() {
        return doctors.size();
    }
}
