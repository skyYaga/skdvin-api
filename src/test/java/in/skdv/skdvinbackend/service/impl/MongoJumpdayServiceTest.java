package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class MongoJumpdayServiceTest extends AbstractSkdvinTest {

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    IJumpdayService jumpdayService;

    @Autowired
    TandemmasterRepository tandemmasterRepository;

    @Autowired
    IAppointmentService appointmentService;

    @BeforeEach
    public void setup() {
        jumpdayRepository.deleteAll();
    }

    @Test
    public void testSaveJumpday() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        GenericResult<Jumpday> savedJumpday = jumpdayService.saveJumpday(jumpday);
        assertNotNull(savedJumpday);
        assertTrue(savedJumpday.isSuccess());
        assertNotNull(savedJumpday.getPayload().getObjectId());
        assertEquals(jumpday.getDate(), savedJumpday.getPayload().getDate());
        assertTrue(savedJumpday.getPayload().isJumping());
        assertEquals(2, savedJumpday.getPayload().getSlots().size());
        assertEquals(jumpday.getSlots().get(0).getTime(), savedJumpday.getPayload().getSlots().get(0).getTime());
        assertEquals(4, savedJumpday.getPayload().getSlots().get(0).getTandemTotal());
        assertEquals(2, savedJumpday.getPayload().getSlots().get(0).getPicOrVidTotal());
    }

    @Test
    public void testSaveJumpday_MoreVideoThanTandemSlots() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().get(0).setTandemTotal(1);
        GenericResult<Jumpday> savedJumpday = jumpdayService.saveJumpday(jumpday);
        assertNotNull(savedJumpday);
        assertFalse(savedJumpday.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_INVALID.toString(), savedJumpday.getMessage());
    }

    @Test
    public void testSaveJumpday_MoreAndThanOrVideoSlots() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().get(0).setPicAndVidTotal(1);
        jumpday.getSlots().get(0).setPicOrVidTotal(0);
        GenericResult<Jumpday> savedJumpday = jumpdayService.saveJumpday(jumpday);
        assertNotNull(savedJumpday);
        assertFalse(savedJumpday.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_INVALID.toString(), savedJumpday.getMessage());
    }

    @Test
    public void testFindJumpdayByDate() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        GenericResult<Jumpday> result = jumpdayService.saveJumpday(jumpday);
        assertTrue(result.isSuccess());

        GenericResult<Jumpday> foundJumpday = jumpdayService.findJumpday(jumpday.getDate());
        assertNotNull(foundJumpday);
        assertTrue(foundJumpday.isSuccess());
        assertEquals(jumpday.getDate(), foundJumpday.getPayload().getDate());
    }

    @Test
    public void testFindJumpdayByDate_NotFound() {
        GenericResult<Jumpday> foundJumpday = jumpdayService.findJumpday(LocalDate.now());
        assertNotNull(foundJumpday);
        assertFalse(foundJumpday.isSuccess());
        assertEquals("jumpday.not.found", foundJumpday.getMessage());
    }


    @Test
    public void testFindJumpdays() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday();
        jumpday2.setDate(LocalDate.now().plusDays(1));

        GenericResult<Jumpday> result1 = jumpdayService.saveJumpday(jumpday1);
        assertTrue(result1.isSuccess());
        GenericResult<Jumpday> result2 = jumpdayService.saveJumpday(jumpday2);
        assertTrue(result2.isSuccess());

        GenericResult<List<Jumpday>> jumpdays = jumpdayService.findJumpdays();
        assertNotNull(jumpdays);
        assertTrue(jumpdays.isSuccess());
        assertEquals(2, jumpdays.getPayload().size());
    }

    @Test
    public void testFindJumpdays_empty() {
        GenericResult<List<Jumpday>> jumpdays = jumpdayService.findJumpdays();
        assertNotNull(jumpdays);
        assertTrue(jumpdays.isSuccess());
        assertEquals(0, jumpdays.getPayload().size());
    }

    @Test
    public void testUpdateJumpday() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        Jumpday changedJumpday = initialResult.getPayload();

        changedJumpday.getSlots().get(0).setTandemTotal(2);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getPayload().getSlots().get(0).getTandemTotal());
    }

    @Test
    public void testUpdateJumpday_NotExisting() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().get(0).setTandemTotal(2);
        jumpday.setDate(LocalDate.now().plus(1, ChronoUnit.YEARS));

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(jumpday.getDate(), jumpday);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

    @Test
    public void testUpdateJumpday_DeleteSlot() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        Jumpday changedJumpday = initialResult.getPayload();

        changedJumpday.getSlots().remove(0);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getPayload().getSlots().size());
    }

    @Test
    public void testUpdateJumpday_AddSlot() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        Jumpday changedJumpday = initialResult.getPayload();

        Slot slot = new Slot();
        slot.setTime(LocalTime.of(13, 0));
        slot.setTandemTotal(3);
        slot.setPicOrVidTotal(2);
        slot.setPicAndVidTotal(1);
        slot.setHandcamTotal(0);
        changedJumpday.getSlots().add(slot);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(3, result.getPayload().getSlots().size());
    }

    @Test
    public void testUpdateJumpday_RemoveSlotWithAppointment() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        GenericResult<Appointment> appointmentResult = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        assertTrue(appointmentResult.isSuccess());

        Jumpday changedJumpday = initialResult.getPayload();
        changedJumpday.getSlots().remove(0);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS.toString(), result.getMessage());
    }

    @Test
    public void testUpdateJumpday_ReduceTandemCountWithAppointments() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        GenericResult<Appointment> appointmentResult =
                appointmentService.saveAppointment(ModelMockHelper.createAppointment(3, 0, 0, 0));
        assertTrue(appointmentResult.isSuccess());

        Jumpday changedJumpday = initialResult.getPayload();
        changedJumpday.getSlots().get(0).setTandemTotal(2);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS.toString(), result.getMessage());
    }

    @Test
    public void testUpdateJumpday_MoreVideoThanTandemSlots() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        Jumpday changedJumpday = initialResult.getPayload();

        changedJumpday.getSlots().get(0).setPicAndVidTotal(10);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_INVALID.toString(), result.getMessage());
    }

    @Test
    public void testUpdateJumpday_MoreAndThanOrVideoSlots() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        Jumpday changedJumpday = initialResult.getPayload();

        changedJumpday.getSlots().get(0).setPicAndVidTotal(1);
        changedJumpday.getSlots().get(0).setPicOrVidTotal(0);

        GenericResult<Jumpday> result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_INVALID.toString(), result.getMessage());
    }

    @Test
    public void testDeleteJumpday() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());

        GenericResult<Void> result = jumpdayService.deleteJumpday(initialResult.getPayload().getDate());

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testDeleteJumpday_AppointmentsExist() {
        GenericResult<Jumpday> initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        assertTrue(initialResult.isSuccess());
        GenericResult<Appointment> appointmentResult = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        assertTrue(appointmentResult.isSuccess());

        GenericResult<Void> result = jumpdayService.deleteJumpday(initialResult.getPayload().getDate());

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_HAS_APPOINTMENTS.toString(), result.getMessage());
    }

    @Test
    public void testDeleteJumpday_InvalidJumpday() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.setDate(LocalDate.now().plus(1, ChronoUnit.YEARS));

        GenericResult<Void> result = jumpdayService.deleteJumpday(jumpday.getDate());

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), result.getMessage());
    }

}
