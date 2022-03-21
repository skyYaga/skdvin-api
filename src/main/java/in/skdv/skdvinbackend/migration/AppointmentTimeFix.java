package in.skdv.skdvinbackend.migration;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * This migration fixes invalid appointment timestamps.
 * They are stored as UTC timestamp in the database, but with the time value of the Zoned Time.
 */
@Slf4j
@ChangeUnit(id = "appointment-time", order = "2")
@AllArgsConstructor
public class AppointmentTimeFix {

    private final JumpdayRepository jumpdayRepository;
    private final ZoneId zoneId;

    @Execution
    public void changeSet() {
        if (systemIsInUTC()) {
            log.info("System is in UTC, performing appointment time migration");
            List<Jumpday> jumpdays = jumpdayRepository.findAll();
            jumpdays.forEach(this::updateSlot);
            jumpdayRepository.saveAll(jumpdays);
        }
    }

    private boolean systemIsInUTC() {
        return TimeZone.getDefault().getRawOffset() == 0;
    }

    private void updateSlot(Jumpday j) {
        j.getSlots().forEach(slot -> slot.getAppointments().forEach(this::updateAppointment));
    }

    private void updateAppointment(Appointment a) {
        Instant instant = a.getDate();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.ofInstant(instant, zoneId), zoneId);
        int offsetSeconds = zonedDateTime.getOffset().getTotalSeconds();
        Instant newInstant = instant.minusSeconds(offsetSeconds);
        a.setDate(newInstant);
        log.info("Appointment {}: Changing instant from {} to {}", a.getAppointmentId(), instant, newInstant);
    }

    @RollbackExecution
    public void rollback() {
        // nothing to do here
    }
}
