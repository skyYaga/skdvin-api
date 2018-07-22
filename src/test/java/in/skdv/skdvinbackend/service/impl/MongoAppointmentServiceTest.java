package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoAppointmentServiceTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private IAppointmentService appointmentService;

    @Before
    public void setup() {
        appointmentRepository.deleteAll();
    }

    @Test
    public void testSaveAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        assertNull(appointment.getCreatedOn());
        assertEquals(0, appointment.getAppointmentId());

        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        assertNotNull(savedAppointment.getCreatedOn());
        assertNotEquals(0, savedAppointment.getAppointmentId());
    }

    @Test
    public void testFindAppointment() {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        Appointment foundAppointment = appointmentService.findAppointment(appointment.getAppointmentId());

        assertEquals(appointment.getAppointmentId(), foundAppointment.getAppointmentId());
        assertEquals(appointment.getTandem(), foundAppointment.getTandem());
        assertEquals(appointment.getCustomer().getFirstName(), foundAppointment.getCustomer().getFirstName());
    }

    @Test
    public void testFindAppointments() {
        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        List<Appointment> appointments = appointmentService.findAppointments();

        assertEquals(2, appointments.size());
        assertNotEquals(appointments.get(0).getAppointmentId(), appointments.get(1).getAppointmentId());
    }

    @Test
    public void testUpdateAppointment() {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
        int appointmentId = appointment.getAppointmentId();
        appointment.getCustomer().setFirstName("Unitbob");

        Appointment updatedAppointment = appointmentService.updateAppointment(appointment);

        assertEquals(appointmentId, updatedAppointment.getAppointmentId());
        assertEquals("Unitbob", updatedAppointment.getCustomer().getFirstName());
    }

}
