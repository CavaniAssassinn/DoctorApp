/* AppointmentRepository.java
Appointment model
Author : Nathan Antha(219474893)
Date: March 2025
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Appointment;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AppointmentRepository implements IAppointmentRepository {

    private static AppointmentRepository repository = null;
    private final Set<Appointment> appointmentSet;

    private AppointmentRepository() {
        this.appointmentSet = new HashSet<>();
    }

    public static AppointmentRepository getInstance() {
        if (repository == null) {
            repository = new AppointmentRepository();
        }
        return repository;
    }

    @Override
    public Appointment create(Appointment appointment) {
        this.appointmentSet.add(appointment);
        return appointment;
    }

    @Override
    public Optional<Appointment> read(Integer appointmentID) {
        return appointmentSet.stream()
                .filter(ap -> ap.getAppointmentID() == appointmentID)
                .findFirst();
    }

    @Override
    public Appointment update(Appointment appointment) {
        Optional<Appointment> existingAppointment = read(appointment.getAppointmentID());
        if (existingAppointment.isPresent()) {
            this.appointmentSet.remove(existingAppointment.get());
            this.appointmentSet.add(appointment);
            return appointment;
        }
        return null;
    }

    @Override
    public void delete(Integer appointmentID) {
        this.appointmentSet.removeIf(ap -> ap.getAppointmentID() == appointmentID);
    }

    @Override
    public Set<Appointment> getAll() {
        return appointmentSet;
    }
}
