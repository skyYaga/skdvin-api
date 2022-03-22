package in.skdv.skdvinbackend.migration;

import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;

import java.time.ZoneId;
import java.util.List;

/**
 * This class migrates the Jumpdays date field from LocalDate to a date string with a new timezone field.
 * It also migrates the jumpday's slots time from a LocalDateTime to a string.
 * <p>
 * All of this is done by reading the data and saving it, as the new Converters will manage the conversion.
 */
@ChangeUnit(id = "jumpday-date-time", order = "1")
@AllArgsConstructor
public class JumpdayDateTimeChange {

    private final JumpdayRepository jumpdayRepository;
    private final ZoneId zoneId;

    @Execution
    public void changeSet() {
        List<Jumpday> jumpdays = jumpdayRepository.findAll();
        jumpdays.forEach(j -> j.setTimezone(zoneId));
        jumpdayRepository.saveAll(jumpdays);
    }

    @RollbackExecution
    public void rollback() {
        // nothing to do here
    }
}
