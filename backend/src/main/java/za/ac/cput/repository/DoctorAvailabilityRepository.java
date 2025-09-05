package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.DoctorAvailability;

import java.util.List;
import java.util.UUID;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, UUID> {
    List<DoctorAvailability> findByDoctor_IdAndDayOfWeek(UUID doctorId, int dayOfWeek);
    List<DoctorAvailability> findByDoctor_Id(UUID doctorId);
    void deleteByDoctor_Id(UUID doctorId);
}
