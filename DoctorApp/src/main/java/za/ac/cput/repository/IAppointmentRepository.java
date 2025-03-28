package za.ac.cput.repository;

import za.ac.cput.domain.Appointment;

import java.util.List;
import java.util.Set;

public interface IAppointmentRepository extends IRepository<Appointment> {

    Set<Appointment> getAll();

    @Override
    List<Appointment> findAll();

}
