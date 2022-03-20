package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NoSlotsLeftException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.GroupSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.*;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static in.skdv.skdvinbackend.exception.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AppointmentServiceTest extends AbstractSkdvinTest {

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private IAppointmentService appointmentService;

    @BeforeEach
    void setup() {
        // Set mock clock
        Clock mockClock = Clock.fixed(Instant.parse(LocalDate.now() + "T00:00:00Z"), ZoneOffset.UTC);
        ReflectionTestUtils.setField(appointmentService, "clock", mockClock);

        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());
    }

    @Test
    void testSaveAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        assertNull(appointment.getCreatedOn());
        assertEquals(0, appointment.getAppointmentId());

        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        assertNotNull(savedAppointment.getCreatedOn());
        assertNotEquals(0, savedAppointment.getAppointmentId());
    }


    @Test
    void testSaveAdminAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.getCustomer().setJumpers(Collections.emptyList());

        assertNull(appointment.getCreatedOn());
        assertEquals(0, appointment.getAppointmentId());

        Appointment savedAppointment = appointmentService.saveAdminAppointment(appointment);

        assertEquals(AppointmentState.CONFIRMED, savedAppointment.getState());
    }

    @Test
    void testSaveAppointment_WithNote() {
        String note = "Price is 10% off";
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setNote(note);

        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        assertEquals(note, savedAppointment.getNote());
    }

    @Test
    void testSaveAppointment_NoJumpday() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setDate(ZonedDateTime.now(zoneId).plusDays(1).toInstant());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            appointmentService.saveAppointment(appointment)
        );

        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG, notFoundException.getErrorMessage());
    }

    @Test
    void testSaveAppointment_NoTandemSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(5, 0, 0, 0);

        NoSlotsLeftException ex = assertThrows(NoSlotsLeftException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(JUMPDAY_NO_FREE_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(5, 3, 0, 0);

        NoSlotsLeftException ex = assertThrows(NoSlotsLeftException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(JUMPDAY_NO_FREE_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_PicOrVid_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 3, 0, 0);

        NoSlotsLeftException ex = assertThrows(NoSlotsLeftException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(JUMPDAY_NO_FREE_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_PicOrVid_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 5, 0, 0);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_PicAndVid_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 3, 0);

        NoSlotsLeftException ex = assertThrows(NoSlotsLeftException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(JUMPDAY_NO_FREE_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_PicAndVid_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 5, 0);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_Handcam_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 0, 3);

        NoSlotsLeftException ex = assertThrows(NoSlotsLeftException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(JUMPDAY_NO_FREE_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_Handcam_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 0, 5);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_PicVidHandcam_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 2, 1, 1);

        InvalidRequestException ex = assertThrows(InvalidRequestException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testSaveAppointment_PicAndVid_MoreCombinedVideoSlotsThanAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(4, 2, 1, 0);

        NoSlotsLeftException ex = assertThrows(NoSlotsLeftException.class, () ->
                appointmentService.saveAppointment(appointment));

        assertEquals(JUMPDAY_NO_FREE_SLOTS, ex.getErrorMessage());
    }

    @Test
    void testFindAppointment() {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());

        Appointment foundAppointment = appointmentService.findAppointment(appointment.getAppointmentId());

        assertEquals(appointment.getAppointmentId(), foundAppointment.getAppointmentId());
        assertEquals(appointment.getTandem(), foundAppointment.getTandem());
        assertEquals(appointment.getCustomer().getFirstName(), foundAppointment.getCustomer().getFirstName());
    }

    @Test
    void testFindAppointment_InvalidId() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                appointmentService.findAppointment(9999999));

        assertEquals(APPOINTMENT_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testFindAppointmentsByDay() {
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.now());

        assertEquals(2, appointments.size());
        assertNotEquals(appointments.get(0).getAppointmentId(), appointments.get(1).getAppointmentId());
    }

    @Test
    void testFindAppointmentsByDay_NoJumpday() {
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertTrue(appointments.isEmpty());
    }

    @Test
    void testUpdateAppointment() {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        appointment.getCustomer().setFirstName("Unitbob");

        Appointment updatedAppointment = appointmentService.updateAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
        assertEquals("Unitbob", updatedAppointment.getCustomer().getFirstName());
    }

    @Test
    void testUpdateAppointment_WithNote() {
        String note = "Price is 10% off";

        Appointment savedAppointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        assertTrue(savedAppointment.getNote().isEmpty());
        savedAppointment.setNote(note);

        Appointment updatedAppointment = appointmentService.updateAppointment(savedAppointment);

        assertEquals(note, updatedAppointment.getNote());
    }

    @Test
    void testUpdateAppointment_ChangeTime() {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        ZonedDateTime newDate = ZonedDateTime.of(LocalDate.now(), LocalTime.of(11, 30), zoneId);
        appointment.setDate(newDate.toInstant());

        Appointment updatedAppointment = appointmentService.updateAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
        assertEquals("Jane", updatedAppointment.getCustomer().getFirstName());
        assertEquals(newDate.toInstant(), updatedAppointment.getDate());
    }

    @Test
    void testUpdateAppointment_ChangeDate() {
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(1)));
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        Instant newDate = appointment.getDate().plus(1, ChronoUnit.DAYS);
        appointment.setDate(newDate);

        Appointment updatedAppointment = appointmentService.updateAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
        assertEquals("Jane", updatedAppointment.getCustomer().getFirstName());
        assertEquals(newDate, updatedAppointment.getDate());
    }

    @Test
    void testUpdateAppointment_ChangeDateAndTime() {
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(1)));
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        ZonedDateTime newDate = ZonedDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 30), zoneId);
        appointment.setDate(newDate.toInstant());

        Appointment updatedAppointment = appointmentService.updateAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
            assertEquals("Jane", updatedAppointment.getCustomer().getFirstName());
        assertEquals(newDate.toInstant(), updatedAppointment.getDate());
    }

    @Test
    void testUpdateAdminAppointment() {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        appointment.getCustomer().setJumpers(Collections.emptyList());
        appointment.setTandem(3);

        Appointment updatedAppointment = appointmentService.updateAdminAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
        assertEquals(0, updatedAppointment.getCustomer().getJumpers().size());
        assertEquals(3, updatedAppointment.getTandem());
    }

    @Test
    void testUpdateAdminAppointment_ChangeDate() {
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(1)));
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        appointment.getCustomer().setJumpers(Collections.emptyList());
        Instant newDate = appointment.getDate().plus(1, ChronoUnit.DAYS);
        appointment.setDate(newDate);

        Appointment updatedAppointment = appointmentService.updateAdminAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
        assertEquals(0, updatedAppointment.getCustomer().getJumpers().size());
        assertEquals(newDate, updatedAppointment.getDate());
    }

    @Test
    void testFindFreeSlots() {
        SlotQuery slotQuery = new SlotQuery(2, 1, 0, 0);

        List<FreeSlot> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertNotNull(freeSlots);
        assertEquals(1, freeSlots.size());
        assertEquals(LocalDate.now(), freeSlots.get(0).getDate());
        assertEquals(2, freeSlots.get(0).getTimes().size());
        assertEquals(LocalTime.of(10, 0), freeSlots.get(0).getTimes().get(0));
        assertEquals(LocalTime.of(11, 30), freeSlots.get(0).getTimes().get(1));
    }

    @Test
    void testFindFreeSlots_TooManyTandems() {
        SlotQuery slotQuery = new SlotQuery(5, 1, 0, 0);

        List<FreeSlot> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertEquals(0, freeSlots.size());
    }

    @Test
    void testFindFreeSlots_TooManyVids() {
        SlotQuery slotQuery = new SlotQuery(4, 4, 0, 0);

        List<FreeSlot> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertEquals(0, freeSlots.size());
    }

    @Test
    void testFindFreeSlots_TooManyCombinedPicVids() {
        SlotQuery slotQuery = new SlotQuery(4, 2, 1, 0);

        List<FreeSlot> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertEquals(0, freeSlots.size());
    }

    @Test
    void testUpdateAppointmentState() {
        Appointment result = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.updateAppointmentState(result, AppointmentState.CONFIRMED);
        Appointment appointment = appointmentService.findAppointment(result.getAppointmentId());

        assertEquals(AppointmentState.CONFIRMED, appointment.getState());
    }

    @Test
    void testUpdateAppointmentState_InvalidAppointment() {
        Appointment invalidAppointment = ModelMockHelper.createSingleAppointment();

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                appointmentService.updateAppointmentState(invalidAppointment, AppointmentState.CONFIRMED));

        assertEquals(APPOINTMENT_NOT_FOUND, ex.getErrorMessage());
    }

    @Test
    void testFindUnconfirmedAppointments_ExpiredAndUnconfirmed() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        VerificationToken verificationToken = VerificationTokenUtil.generate();
        verificationToken.setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointment.setVerificationToken(verificationToken);
        appointmentService.saveAppointment(appointment);

        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();

        assertEquals(1, unconfirmedAppointments.size());
    }

    @Test
    void testFindUnconfirmedAppointments_ExpiredAndConfirmed() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        VerificationToken verificationToken = VerificationTokenUtil.generate();
        verificationToken.setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointment.setVerificationToken(verificationToken);
        appointment.setState(AppointmentState.CONFIRMED);
        appointmentService.saveAppointment(appointment);

        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();

        assertEquals(0, unconfirmedAppointments.size());
    }

    @Test
    void testFindUnconfirmedAppointments_NotExpiredAndUnconfirmed() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointmentService.saveAppointment(appointment);

        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();

        assertEquals(0, unconfirmedAppointments.size());
    }

    @Test
    void testDeleteAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointmentService.saveAppointment(appointment);
        int appointmentId = appointment.getAppointmentId();

        appointmentService.deleteAppointment(appointmentId);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
            appointmentService.findAppointment(appointmentId)
        );

        assertEquals(APPOINTMENT_NOT_FOUND, notFoundException.getErrorMessage());
    }

    @Test
    void testFindGroupSlots() {
        SlotQuery slotQuery = new SlotQuery(6, 0, 0, 0);

        List<GroupSlot> groupSlots = appointmentService.findGroupSlots(slotQuery);

        assertNotNull(groupSlots);
        assertEquals(1, groupSlots.size());
        assertEquals(LocalDate.now(), groupSlots.get(0).getDate());
        assertEquals(LocalTime.of(10, 0), groupSlots.get(0).getFirstTime());
        assertEquals(LocalTime.of(11, 30), groupSlots.get(0).getLastTime());
        assertEquals(2, groupSlots.get(0).getTimeCount());
        assertEquals(8, groupSlots.get(0).getTandemAvailable());
        assertEquals(4, groupSlots.get(0).getPicOrVidAvailable());
        assertEquals(2, groupSlots.get(0).getPicAndVidAvailable());
        assertEquals(2, groupSlots.get(0).getHandcamAvailable());
        assertEquals(2, groupSlots.get(0).getSlots().size());
        assertEquals(LocalTime.of(10, 0), groupSlots.get(0).getSlots().get(0).getTime());
        assertEquals(LocalTime.of(11, 30), groupSlots.get(0).getSlots().get(1).getTime());
        assertEquals(4, groupSlots.get(0).getSlots().get(0).getTandemAvailable());
        assertEquals(2, groupSlots.get(0).getSlots().get(0).getPicOrVidAvailable());
        assertEquals(1, groupSlots.get(0).getSlots().get(0).getPicAndVidAvailable());
        assertEquals(1, groupSlots.get(0).getSlots().get(0).getHandcamAvailable());
    }

    @Test
    void testFindGroupSlots_MultipleDays() {
        Jumpday jumpday = ModelMockHelper.createJumpday(LocalDate.now().plus(1, ChronoUnit.DAYS));
        Slot slot = new Slot();
        slot.setTime(LocalTime.of(13, 0));
        slot.setTandemTotal(4);
        slot.setPicOrVidTotal(2);
        slot.setPicAndVidTotal(1);
        slot.setHandcamTotal(1);
        jumpday.getSlots().add(slot);
        jumpdayRepository.save(jumpday);

        SlotQuery slotQuery = new SlotQuery(6, 0, 0, 0);

        List<GroupSlot> groupSlots = appointmentService.findGroupSlots(slotQuery);

        assertNotNull(groupSlots);
        assertEquals(3, groupSlots.size());
    }

    @Test
    void testFindGroupSlots_NoFreeSlots() {
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpday.getSlots().forEach(s -> s.setTandemTotal(0));
        jumpdayRepository.deleteAll();
        jumpdayRepository.save(jumpday);

        SlotQuery slotQuery = new SlotQuery(6, 0, 0, 0);

        List<GroupSlot> groupSlots = appointmentService.findGroupSlots(slotQuery);

        assertNotNull(groupSlots);
        assertEquals(0, groupSlots.size());
    }
    
    @Test
    void testFindAppointmentsWithinNextWeek() {
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().minusDays(1)));
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(5)));
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(10)));

        Appointment pastAppointment = ModelMockHelper.createSingleAppointment();
        Appointment todayAppointment = ModelMockHelper.createSingleAppointment();
        Appointment thisWeekAppointment = ModelMockHelper.createSingleAppointment();
        Appointment futureAppointment = ModelMockHelper.createSingleAppointment();

        pastAppointment.setDate(ZonedDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(10, 0), zoneId).toInstant());
        thisWeekAppointment.setDate(ZonedDateTime.of(LocalDate.now().plusDays(5), LocalTime.of(10, 0), zoneId).toInstant());
        futureAppointment.setDate(ZonedDateTime.of(LocalDate.now().plusDays(10), LocalTime.of(10, 0), zoneId).toInstant());

        appointmentService.saveAppointment(pastAppointment);
        appointmentService.saveAppointment(todayAppointment);
        appointmentService.saveAppointment(thisWeekAppointment);
        appointmentService.saveAppointment(futureAppointment);

        List<Appointment> appointmentsWithinNextWeek = appointmentService.findAppointmentsWithinNextWeek();

        assertEquals(2, appointmentsWithinNextWeek.size());
        assertEquals(ZonedDateTime.of(LocalDate.now(), LocalTime.of(10, 0), zoneId).toInstant(), appointmentsWithinNextWeek.get(0).getDate());
        assertEquals(ZonedDateTime.of(LocalDate.now().plusDays(5), LocalTime.of(10, 0), zoneId).toInstant(), appointmentsWithinNextWeek.get(1).getDate());
    }

    @Test
    void testReminderSent() {
        Appointment savedAppointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());

        appointmentService.reminderSent(savedAppointment);

        Appointment appointment = appointmentService.findAppointment(savedAppointment.getAppointmentId());

        assertTrue(appointment.isReminderSent());
    }

    @Test
    void testReminderSent_AdminAppointment() {
        Appointment singleAppointment = ModelMockHelper.createSingleAppointment();
        singleAppointment.getCustomer().setJumpers(Collections.emptyList());
        Appointment savedAppointment = appointmentService.saveAdminAppointment(singleAppointment);

        appointmentService.reminderSent(savedAppointment);

        Appointment appointment = appointmentService.findAppointment(savedAppointment.getAppointmentId());

        assertTrue(appointment.isReminderSent());
    }
}
