package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.cput.domain.Doctor;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    @Query("""
      select d from Doctor d
      where (:q is null or lower(d.fullName) like lower(concat('%', :q, '%')))
        and (:city is null or lower(d.clinicCity) = lower(:city))
        and (:spec is null or lower(d.speciality) = lower(:spec))
    """)
    List<Doctor> search(@Param("q") String q,
                        @Param("city") String city,
                        @Param("spec") String spec);
}
