package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctor_Id(UUID doctorId);

    List<Appointment> findByPatient_IdOrderByStartTimeAsc(UUID patientId);

    boolean existsByDoctor_IdAndStartTime(UUID doctorId, LocalDateTime startTime);
}
