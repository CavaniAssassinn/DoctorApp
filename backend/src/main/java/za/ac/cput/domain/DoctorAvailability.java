package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Doctor doctor;

    /**
     * ISO-8601 day-of-week, 1=Monday ... 7=Sunday
     */
    @Column(nullable = false)
    private int dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;   // e.g. 09:00

    @Column(nullable = false)
    private LocalTime endTime;     // e.g. 13:00

    @Column(nullable = false)
    private int slotMinutes;       // e.g. 20

    protected DoctorAvailability() {}

    public DoctorAvailability(Doctor doctor, int dayOfWeek, LocalTime startTime, LocalTime endTime, int slotMinutes) {
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotMinutes = slotMinutes;
    }

    public UUID getId() { return id; }
    public Doctor getDoctor() { return doctor; }
    public int getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getSlotMinutes() { return slotMinutes; }
}
