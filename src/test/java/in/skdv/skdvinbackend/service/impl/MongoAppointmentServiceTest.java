package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.Appointment;
import in.skdv.skdvinbackend.model.AppointmentState;
import in.skdv.skdvinbackend.model.Customer;
import in.skdv.skdvinbackend.service.IAppointmentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoAppointmentServiceTest {

    @Autowired
    private
    IAppointmentService appointmentService;

    @Test
    public void testSaveAppointment() {
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setPlz("12345");
        customer.setCity("Foo City");
        customer.setEmail("example@example.com");
        customer.setMobile("1234567890");
        customer.setTel("0123456789");


        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setState(AppointmentState.NONE);
        appointment.setDate(LocalDateTime.now());
        appointment.setTandem(1);
        appointment.setVideo(1);

        assertNull(appointment.getCreatedOn());

        appointmentService.saveAppointment(appointment);

        assertNotNull(appointment.getCreatedOn());
    }

}
