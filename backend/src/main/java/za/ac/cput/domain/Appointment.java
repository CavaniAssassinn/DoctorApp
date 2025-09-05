package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"doctor_id","startTime"}))
public class Appointment {
    public enum Status { PENDING, CONFIRMED, CANCELLED }

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional=false) private Doctor doctor;
    @ManyToOne(optional=false) private Patient patient;

    @Column(nullable=false) private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false) private Status status = Status.PENDING;

    protected Appointment(){}
    public Appointment(Doctor d, Patient p, LocalDateTime start){ this.doctor=d; this.patient=p; this.startTime=start; }
    public UUID getId(){ return id; }
    public Doctor getDoctor(){ return doctor; }
    public Patient getPatient(){ return patient; }
    public LocalDateTime getStartTime(){ return startTime; }
    public Status getStatus(){ return status; }
    public void setStatus(Status s){ this.status=s; }
}
