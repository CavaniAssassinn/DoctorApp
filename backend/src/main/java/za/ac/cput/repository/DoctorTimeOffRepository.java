package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.ac.cput.domain.DoctorTimeOff;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DoctorTimeOffRepository extends JpaRepository<DoctorTimeOff, UUID> {

    // Overlaps a given range [from, to)
    @Query("""
    select t from DoctorTimeOff t
    where t.doctor.id = :doctorId
      and t.startTime < :to
      and t.endTime   > :from
    """)
    List<DoctorTimeOff> findOverlapping(UUID doctorId, LocalDateTime from, LocalDateTime to);
}
