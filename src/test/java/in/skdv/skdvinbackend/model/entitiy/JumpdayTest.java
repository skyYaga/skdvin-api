package in.skdv.skdvinbackend.model.entitiy;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpdayTest {

    @Test
    void testAddAppointment_SlotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        assertTrue(jumpday.addAppointment(ModelMockHelper.createSingleAppointment()));
    }

    @Test
    void testAddAppointment_SlotNotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Appointment singleAppointment = ModelMockHelper.createSingleAppointment();
        singleAppointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
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
        singleAppointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
        assertFalse(jumpday.getSlotForAppointment(singleAppointment).isPresent());
    }
}
