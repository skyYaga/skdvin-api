package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidDeletionException;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Slot;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IJumpdayService;
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
class JumpdayServiceTest extends AbstractSkdvinTest {

    @Autowired
    JumpdayRepository jumpdayRepository;

    @Autowired
    IJumpdayService jumpdayService;

    @Autowired
    IAppointmentService appointmentService;

    @BeforeEach
    void setup() {
        jumpdayRepository.deleteAll();
    }

    @Test
    void testSaveJumpday() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        
        Jumpday savedJumpday = jumpdayService.saveJumpday(jumpday);
        
        assertNotNull(savedJumpday);
        assertNotNull(savedJumpday.getObjectId());
        assertEquals(jumpday.getDate(), savedJumpday.getDate());
        assertTrue(savedJumpday.isJumping());
        assertEquals(2, savedJumpday.getSlots().size());
        assertEquals(jumpday.getSlots().get(0).getTime(), savedJumpday.getSlots().get(0).getTime());
        assertEquals(4, savedJumpday.getSlots().get(0).getTandemTotal());
        assertEquals(2, savedJumpday.getSlots().get(0).getPicOrVidTotal());
    }

    @Test
    void testSaveJumpday_MoreVideoThanTandemSlots() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().get(0).setTandemTotal(1);

        InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class, () ->
            jumpdayService.saveJumpday(jumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_INVALID, invalidRequestException.getErrorMessage());
    }

    @Test
    void testSaveJumpday_MoreAndThanOrVideoSlots() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().get(0).setPicAndVidTotal(1);
        jumpday.getSlots().get(0).setPicOrVidTotal(0);

        InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class, () ->
            jumpdayService.saveJumpday(jumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_INVALID, invalidRequestException.getErrorMessage());
    }

    @Test
    void testFindJumpdayByDate() {
        Jumpday jumpday = ModelMockHelper.createJumpday();

        jumpdayService.saveJumpday(jumpday);

        Jumpday foundJumpday = jumpdayService.findJumpday(jumpday.getDate());
        assertNotNull(foundJumpday);
    }

    @Test
    void testFindJumpdayByDate_NotFound() {
        LocalDate now = LocalDate.now();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            jumpdayService.findJumpday(now)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }


    @Test
    void testFindJumpdays() {
        Jumpday jumpday1 = ModelMockHelper.createJumpday();
        Jumpday jumpday2 = ModelMockHelper.createJumpday();
        jumpday2.setDate(LocalDate.now().plusDays(1));

        jumpdayService.saveJumpday(jumpday1);
        jumpdayService.saveJumpday(jumpday2);

        List<Jumpday> jumpdays = jumpdayService.findJumpdays();
        assertNotNull(jumpdays);
        assertEquals(2, jumpdays.size());
    }

    @Test
    void testFindJumpdays_empty() {
        List<Jumpday> jumpdays = jumpdayService.findJumpdays();
        assertNotNull(jumpdays);
        assertEquals(0, jumpdays.size());
    }

    @Test
    void testUpdateJumpday() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());

        changedJumpday.getSlots().get(0).setTandemTotal(2);

        Jumpday result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertEquals(2, result.getSlots().get(0).getTandemTotal());
    }

    @Test
    void testUpdateJumpday_NotExisting() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().get(0).setTandemTotal(2);
        jumpday.setDate(LocalDate.now().plus(1, ChronoUnit.YEARS));
        LocalDate date = jumpday.getDate();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            jumpdayService.updateJumpday(date, jumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testUpdateJumpday_DeleteSlot() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());

        changedJumpday.getSlots().remove(0);

        Jumpday result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertEquals(1, result.getSlots().size());
    }

    @Test
    void testUpdateJumpday_AddSlot() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());

        Slot slot = new Slot();
        slot.setTime(LocalTime.of(13, 0));
        slot.setTandemTotal(3);
        slot.setPicOrVidTotal(2);
        slot.setPicAndVidTotal(1);
        slot.setHandcamTotal(0);
        changedJumpday.getSlots().add(slot);

        Jumpday result = jumpdayService.updateJumpday(changedJumpday.getDate(), changedJumpday);

        assertNotNull(result);
        assertEquals(3, result.getSlots().size());
    }

    @Test
    void testUpdateJumpday_RemoveSlotWithAppointment() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        changedJumpday.getSlots().remove(0);
        LocalDate date = changedJumpday.getDate();

        InvalidDeletionException invalidDeletionException = assertThrows(InvalidDeletionException.class, () ->
            jumpdayService.updateJumpday(date, changedJumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS, invalidDeletionException.getErrorMessage());
    }

    @Test
    void testUpdateJumpday_ReduceTandemCountWithAppointments() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        appointmentService.saveAppointment(ModelMockHelper.createAppointment(3, 0, 0, 0));

        changedJumpday.getSlots().get(0).setTandemTotal(2);
        LocalDate date = changedJumpday.getDate();

        InvalidDeletionException invalidDeletionException = assertThrows(InvalidDeletionException.class, () ->
            jumpdayService.updateJumpday(date, changedJumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_SLOT_HAS_APPOINTMENTS, invalidDeletionException.getErrorMessage());
    }

    @Test
    void testUpdateJumpday_MoreVideoThanTandemSlots() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());

        changedJumpday.getSlots().get(0).setPicAndVidTotal(10);
        LocalDate date = changedJumpday.getDate();

        InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class, () ->
            jumpdayService.updateJumpday(date, changedJumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_INVALID, invalidRequestException.getErrorMessage());
    }

    @Test
    void testUpdateJumpday_MoreAndThanOrVideoSlots() {
        Jumpday changedJumpday = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());

        changedJumpday.getSlots().get(0).setPicAndVidTotal(1);
        changedJumpday.getSlots().get(0).setPicOrVidTotal(0);
        LocalDate date = changedJumpday.getDate();

        InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class, () ->
            jumpdayService.updateJumpday(date, changedJumpday)
        );

        assertEquals(ErrorMessage.JUMPDAY_INVALID, invalidRequestException.getErrorMessage());
    }

    @Test
    void testDeleteJumpday() {
        Jumpday initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        LocalDate date = initialResult.getDate();

        jumpdayService.deleteJumpday(date);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> jumpdayService.findJumpday(date));
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, ex.getErrorMessage());
    }

    @Test
    void testDeleteJumpday_AppointmentsExist() {
        Jumpday initialResult = jumpdayService.saveJumpday(ModelMockHelper.createJumpday());
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        LocalDate date = initialResult.getDate();

        InvalidDeletionException invalidDeletionException = assertThrows(InvalidDeletionException.class, () ->
            jumpdayService.deleteJumpday(date)
        );

        assertEquals(ErrorMessage.JUMPDAY_HAS_APPOINTMENTS, invalidDeletionException.getErrorMessage());
    }

    @Test
    void testDeleteJumpday_InvalidJumpday() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.setDate(LocalDate.now().plus(1, ChronoUnit.YEARS));
        LocalDate date = jumpday.getDate();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            jumpdayService.deleteJumpday(date)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

}
