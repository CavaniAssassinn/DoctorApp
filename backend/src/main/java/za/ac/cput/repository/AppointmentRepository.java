package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /** Check if a doctor already has an appointment at a specific start time (prevents double booking). */
    boolean existsByDoctor_IdAndStartTime(UUID doctorId, LocalDateTime startTime);

    /** All appointments for a patient, ordered soonest first. */
    List<Appointment> findByPatient_IdOrderByStartTimeAsc(UUID patientId);

    /** Appointments for a doctor within a window (useful for day/week views). */
    List<Appointment> findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(
            UUID doctorId, LocalDateTime from, LocalDateTime to
    );
}
