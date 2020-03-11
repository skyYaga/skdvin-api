package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.FreeSlot;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.model.entity.VerificationToken;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.GenericResult;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoAppointmentServiceTest extends AbstractSkdvinTest {

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private IAppointmentService appointmentService;

    @Before
    public void setup() {
        // Set mock clock
        Clock mockClock = Clock.fixed(Instant.parse(LocalDate.now().toString() + "T00:00:00Z"), ZoneOffset.UTC);
        ReflectionTestUtils.setField(appointmentService, "clock", mockClock);

        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());
    }

    @Test
    public void testSaveAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        assertNull(appointment.getCreatedOn());
        assertEquals(0, appointment.getAppointmentId());

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertTrue(savedAppointment.isSuccess());
        assertNotNull(savedAppointment.getPayload().getCreatedOn());
        assertNotEquals(0, savedAppointment.getPayload().getAppointmentId());
    }

    @Test
    public void testSaveAppointment_NoJumpday() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setDate(LocalDateTime.now().plusDays(1));

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_NoTandemSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(5, 0, 0, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(5, 3, 0, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_PicOrVid_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 3, 0, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_PicOrVid_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 5, 0, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_PicAndVid_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 3, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_PicAndVid_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 5, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_Handcam_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 0, 3);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_Handcam_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 0, 0, 5);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_PicVidHandcam_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 2, 1, 1);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.APPOINTMENT_MORE_VIDEO_THAN_TAMDEM_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testFindAppointment() {
        GenericResult<Appointment> appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        assertTrue(appointment.isSuccess());

        Appointment foundAppointment = appointmentService.findAppointment(appointment.getPayload().getAppointmentId());

        assertEquals(appointment.getPayload().getAppointmentId(), foundAppointment.getAppointmentId());
        assertEquals(appointment.getPayload().getTandem(), foundAppointment.getTandem());
        assertEquals(appointment.getPayload().getCustomer().getFirstName(), foundAppointment.getCustomer().getFirstName());
    }

    @Test
    public void testFindAppointmentsByDay() {
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.now());

        assertEquals(2, appointments.size());
        assertNotEquals(appointments.get(0).getAppointmentId(), appointments.get(1).getAppointmentId());
    }

    @Test
    public void testFindAppointmentsByDay_NoJumpday() {
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.now().plus(1, ChronoUnit.DAYS));

        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testUpdateAppointment() {
        GenericResult<Appointment> appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        assertTrue(appointment.isSuccess());
        int appointmentId = appointment.getPayload().getAppointmentId();
        appointment.getPayload().getCustomer().setFirstName("Unitbob");

        GenericResult<Appointment> updatedAppointment = appointmentService.updateAppointment(appointment.getPayload());

        assertTrue(updatedAppointment.isSuccess());
        assertEquals(appointmentId, updatedAppointment.getPayload().getAppointmentId());
        assertEquals("Unitbob", updatedAppointment.getPayload().getCustomer().getFirstName());
    }

    @Test
    public void testUpdateAppointment_ChangeTime() {
        GenericResult<Appointment> appointmentResult = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        assertTrue(appointmentResult.isSuccess());
        Appointment appointment = appointmentResult.getPayload();
        int appointmentId = appointment.getAppointmentId();
        LocalDateTime newDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 30));
        appointment.setDate(newDate);

        GenericResult<Appointment> updatedAppointment = appointmentService.updateAppointment(appointment);

        assertTrue(updatedAppointment.isSuccess());
        assertEquals(appointmentId, updatedAppointment.getPayload().getAppointmentId());
        assertEquals("Jane", updatedAppointment.getPayload().getCustomer().getFirstName());
        assertEquals(newDate, updatedAppointment.getPayload().getDate());
    }

    @Test
    public void testUpdateAppointment_ChangeDate() {
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(1)));
        GenericResult<Appointment> appointmentResult = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        assertTrue(appointmentResult.isSuccess());
        Appointment appointment = appointmentResult.getPayload();
        int appointmentId = appointment.getAppointmentId();
        LocalDateTime newDate = appointment.getDate().plusDays(1);
        appointment.setDate(newDate);

        GenericResult<Appointment> updatedAppointment = appointmentService.updateAppointment(appointment);

        assertTrue(updatedAppointment.isSuccess());
        assertEquals(appointmentId, updatedAppointment.getPayload().getAppointmentId());
        assertEquals("Jane", updatedAppointment.getPayload().getCustomer().getFirstName());
        assertEquals(newDate, updatedAppointment.getPayload().getDate());
    }

    @Test
    public void testUpdateAppointment_ChangeDateAndTime() {
        jumpdayRepository.save(ModelMockHelper.createJumpday(LocalDate.now().plusDays(1)));
        GenericResult<Appointment> appointmentResult = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        assertTrue(appointmentResult.isSuccess());
        Appointment appointment = appointmentResult.getPayload();
        int appointmentId = appointment.getAppointmentId();
        LocalDateTime newDate = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 30));
        appointment.setDate(newDate);

        GenericResult<Appointment> updatedAppointment = appointmentService.updateAppointment(appointment);

        assertTrue(updatedAppointment.isSuccess());
        assertEquals(appointmentId, updatedAppointment.getPayload().getAppointmentId());
            assertEquals("Jane", updatedAppointment.getPayload().getCustomer().getFirstName());
        assertEquals(newDate, updatedAppointment.getPayload().getDate());
    }

    @Test
    public void testFindFreeSlots() {
        SlotQuery slotQuery = new SlotQuery(2, 1, 0, 0);

        GenericResult<List<FreeSlot>> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertTrue(freeSlots.isSuccess());
        assertNotNull(freeSlots.getPayload());
        assertEquals(1, freeSlots.getPayload().size());
        assertEquals(LocalDate.now(), freeSlots.getPayload().get(0).getDate());
        assertEquals(2, freeSlots.getPayload().get(0).getTimes().size());
        assertEquals(LocalTime.of(10, 0), freeSlots.getPayload().get(0).getTimes().get(0));
        assertEquals(LocalTime.of(11, 30), freeSlots.getPayload().get(0).getTimes().get(1));
    }

    @Test
    public void testFindFreeSlots_TooManyTandems() {
        SlotQuery slotQuery = new SlotQuery(5, 1, 0, 0);

        GenericResult<List<FreeSlot>> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertFalse(freeSlots.isSuccess());
        assertNull(freeSlots.getPayload());
        assertEquals(ErrorMessage.APPOINTMENT_NO_FREE_SLOTS.toString(), freeSlots.getMessage());
    }

    @Test
    public void testFindFreeSlots_TooManyVids() {
        SlotQuery slotQuery = new SlotQuery(4, 4, 0, 0);

        GenericResult<List<FreeSlot>> freeSlots = appointmentService.findFreeSlots(slotQuery);

        assertFalse(freeSlots.isSuccess());
        assertNull(freeSlots.getPayload());
        assertEquals(ErrorMessage.APPOINTMENT_NO_FREE_SLOTS.toString(), freeSlots.getMessage());
    }

    @Test
    public void testUpdateAppointmentState() {
        GenericResult<Appointment> result = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        GenericResult<Void> stateResult = appointmentService.updateAppointmentState(result.getPayload(), AppointmentState.CONFIRMED);
        Appointment appointment = appointmentService.findAppointment(result.getPayload().getAppointmentId());

        assertTrue(stateResult.isSuccess());
        assertEquals(AppointmentState.CONFIRMED, appointment.getState());
    }

    @Test
    public void testUpdateAppointmentState_InvalidAppointment() {
        Appointment invalidAppointment = ModelMockHelper.createSingleAppointment();
        GenericResult<Void> result = appointmentService.updateAppointmentState(invalidAppointment, AppointmentState.CONFIRMED);

        assertFalse(result.isSuccess());
        assertEquals(ErrorMessage.APPOINTMENT_NOT_FOUND.toString(), result.getMessage());
    }

    @Test
    public void testFindUnconfirmedAppointments_ExpiredAndUnconfirmed() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        VerificationToken verificationToken = VerificationTokenUtil.generate();
        verificationToken.setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointment.setVerificationToken(verificationToken);
        appointmentService.saveAppointment(appointment);

        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();

        Assert.assertEquals(1, unconfirmedAppointments.size());
    }

    @Test
    public void testFindUnconfirmedAppointments_ExpiredAndConfirmed() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        VerificationToken verificationToken = VerificationTokenUtil.generate();
        verificationToken.setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointment.setVerificationToken(verificationToken);
        appointment.setState(AppointmentState.CONFIRMED);
        appointmentService.saveAppointment(appointment);

        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();

        Assert.assertEquals(0, unconfirmedAppointments.size());
    }

    @Test
    public void testFindUnconfirmedAppointments_NotExpiredAndUnconfirmed() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointmentService.saveAppointment(appointment);

        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();

        Assert.assertEquals(0, unconfirmedAppointments.size());
    }

    @Test
    public void testDeleteAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointmentService.saveAppointment(appointment);

        appointmentService.deleteAppointment(appointment.getAppointmentId());

        Assert.assertNull(appointmentService.findAppointment(appointment.getAppointmentId()));
    }

}
