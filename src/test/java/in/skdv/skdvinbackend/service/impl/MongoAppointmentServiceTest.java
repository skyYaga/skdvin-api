package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoAppointmentServiceTest {

    @Autowired
    private IAppointmentService appointmentService;

    @Test
    public void testSaveAppointment() {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        assertNull(appointment.getCreatedOn());
        assertEquals(0, appointment.getId());

        appointmentService.saveAppointment(appointment);

        assertNotNull(appointment.getCreatedOn());
        assertNotEquals(0, appointment.getId());
    }

}
