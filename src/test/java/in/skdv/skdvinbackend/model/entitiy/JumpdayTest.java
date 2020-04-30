package in.skdv.skdvinbackend.model.entitiy;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JumpdayTest {

    @Test
    public void testAddAppointment_SlotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Assert.assertTrue(jumpday.addAppointment(ModelMockHelper.createSingleAppointment()));
    }

    @Test
    public void testAddAppointment_SlotNotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Appointment singleAppointment = ModelMockHelper.createSingleAppointment();
        singleAppointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
        Assert.assertFalse(jumpday.addAppointment(singleAppointment));
    }

    @Test
    public void testGetSlotForAppointment_SlotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Assert.assertTrue(jumpday.getSlotForAppointment(ModelMockHelper.createSingleAppointment()).isPresent());
    }

    @Test
    public void testGetSlotForAppointment_SlotNotExists() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        Appointment singleAppointment = ModelMockHelper.createSingleAppointment();
        singleAppointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
        Assert.assertFalse(jumpday.getSlotForAppointment(singleAppointment).isPresent());
    }
}
