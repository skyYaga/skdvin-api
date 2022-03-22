package in.skdv.skdvinbackend.model.entitiy;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpdayTest {

    private final ZoneId zoneId = ZoneId.of("Europe/Berlin");

    @Test
    void testAddAppointment_SlotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        assertTrue(jumpday.addAppointment(ModelMockHelper.createSingleAppointment()));
    }

    @Test
    void testAddAppointment_SlotNotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Appointment singleAppointment = ModelMockHelper.createSingleAppointment();
        singleAppointment.setDate(ZonedDateTime.of(LocalDate.now(), LocalTime.of(11, 0), zoneId).toInstant());
        assertFalse(jumpday.addAppointment(singleAppointment));
    }

    @Test
    void testGetSlotForAppointment_SlotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        assertTrue(jumpday.getSlotForAppointment(ModelMockHelper.createSingleAppointment()).isPresent());
    }

    @Test
    void testGetSlotForAppointment_SlotNotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Appointment singleAppointment = ModelMockHelper.createSingleAppointment();
        singleAppointment.setDate(ZonedDateTime.of(LocalDate.now(), LocalTime.of(11, 0), zoneId).toInstant());
        assertFalse(jumpday.getSlotForAppointment(singleAppointment).isPresent());
    }
}
