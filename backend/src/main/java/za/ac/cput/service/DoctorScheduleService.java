package za.ac.cput.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Doctor;
import za.ac.cput.domain.DoctorAvailability;
import za.ac.cput.domain.DoctorTimeOff;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.repository.DoctorAvailabilityRepository;
import za.ac.cput.repository.DoctorRepository;
import za.ac.cput.repository.DoctorTimeOffRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorScheduleService {

    private final DoctorRepository doctors;
    private final DoctorAvailabilityRepository availRepo;
    private final DoctorTimeOffRepository timeOffRepo;
    private final AppointmentRepository apptRepo;

    public DoctorScheduleService(DoctorRepository doctors,
                                 DoctorAvailabilityRepository availRepo,
                                 DoctorTimeOffRepository timeOffRepo,
                                 AppointmentRepository apptRepo) {
        this.doctors = doctors;
        this.availRepo = availRepo;
        this.timeOffRepo = timeOffRepo;
        this.apptRepo = apptRepo;
    }

    public record AvailabilityDTO(int dayOfWeek, String start, String end, int slotMinutes) {}

    @Transactional
    public void setWeeklyAvailability(UUID doctorId, List<AvailabilityDTO> rules){
        Doctor doc = doctors.findById(doctorId).orElseThrow(() -> notFound("Doctor", doctorId));
        // replace all rules for this doctor
        availRepo.deleteByDoctor_Id(doctorId);
        for (AvailabilityDTO r : rules) {
            LocalTime s = LocalTime.parse(r.start());
            LocalTime e = LocalTime.parse(r.end());
            if (r.dayOfWeek() < 1 || r.dayOfWeek() > 7) throw bad("dayOfWeek must be 1..7");
            if (!s.isBefore(e)) throw bad("start must be before end");
            if (r.slotMinutes() <= 0) throw bad("slotMinutes must be > 0");
            availRepo.save(new DoctorAvailability(doc, r.dayOfWeek(), s, e, r.slotMinutes()));
        }
    }

    @Transactional
    public void addTimeOff(UUID doctorId, LocalDateTime start, LocalDateTime end, String reason){
        Doctor doc = doctors.findById(doctorId).orElseThrow(() -> notFound("Doctor", doctorId));
        if (!start.isBefore(end)) throw bad("time off start must be before end");
        timeOffRepo.save(new DoctorTimeOff(doc, start, end, reason));
    }

    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableSlots(UUID doctorId, LocalDate date){
        int dow = date.getDayOfWeek().getValue(); // 1..7
        List<DoctorAvailability> rules = availRepo.findByDoctor_IdAndDayOfWeek(doctorId, dow);
        if (rules.isEmpty()) return List.of();

        // The day range to check time-off overlap against
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.plusDays(1).atStartOfDay();
        List<DoctorTimeOff> offs = timeOffRepo.findOverlapping(doctorId, dayStart, dayEnd);

        List<LocalDateTime> out = new ArrayList<>();
        for (DoctorAvailability r : rules) {
            LocalDateTime cur = date.atTime(r.getStartTime());
            final LocalDateTime end = date.atTime(r.getEndTime());
            final int minutes = r.getSlotMinutes();

            while (cur.plusMinutes(minutes).compareTo(end) <= 0) {
                // take final snapshots for this iteration
                final java.time.LocalDateTime slotStart = cur;
                final java.time.LocalDateTime slotEnd   = slotStart.plusMinutes(minutes);

                // OK: lambda uses final variables (slotStart/slotEnd), not 'cur'
                boolean blockedByTimeOff = offs.stream().anyMatch(off ->
                        slotStart.isBefore(off.getEndTime()) && slotEnd.isAfter(off.getStartTime())
                );

                boolean alreadyBooked = apptRepo.existsByDoctor_IdAndStartTime(doctorId, slotStart);

                if (!blockedByTimeOff && !alreadyBooked) {
                    // (optional) skip past times
                    if (!slotStart.isBefore(java.time.LocalDateTime.now())) {
                        out.add(slotStart);
                    }
                }

                // advance to next slot (safe to reassign 'cur' here; it's not used in the lambda)
                cur = slotEnd;
            }

        }
        out.sort(Comparator.naturalOrder());
        return out;
    }

    private static ResponseStatusException bad(String msg){
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
    private static ResponseStatusException notFound(String what, Object id){
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " not found: " + id);
    }
}
