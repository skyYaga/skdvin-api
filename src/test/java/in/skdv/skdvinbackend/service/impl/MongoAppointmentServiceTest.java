package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoAppointmentServiceTest {

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private IAppointmentService appointmentService;

    @Before
    public void setup() {
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
        Appointment appointment = ModelMockHelper.createAppointment(5, 0);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_NoVideoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 3);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_NoSlotsAvailable() {
        Appointment appointment = ModelMockHelper.createAppointment(5, 3);

        GenericResult<Appointment> savedAppointment = appointmentService.saveAppointment(appointment);

        assertFalse(savedAppointment.isSuccess());
        assertEquals(ErrorMessage.JUMPDAY_NO_FREE_SLOTS.toString(), savedAppointment.getMessage());
    }

    @Test
    public void testSaveAppointment_MoreVideoThanTandemSlots() {
        Appointment appointment = ModelMockHelper.createAppointment(3, 5);

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
    public void testFindAppointments() {
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        List<Appointment> appointments = appointmentService.findAppointmentsByDay(LocalDate.now());

        assertEquals(2, appointments.size());
        assertNotEquals(appointments.get(0).getAppointmentId(), appointments.get(1).getAppointmentId());
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

}
