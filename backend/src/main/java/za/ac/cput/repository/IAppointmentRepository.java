/* IAppointmentRepository.java
Appointment model
Author : Nathan Antha(219474893)
Date: March 2025*/
package za.ac.cput.repository;

import za.ac.cput.domain.Appointment;

import java.util.Set;

public interface IAppointmentRepository extends IRepository<Appointment,Integer > {

    Set<Appointment> getAll();

    void delete(Integer appointmentID);



}
