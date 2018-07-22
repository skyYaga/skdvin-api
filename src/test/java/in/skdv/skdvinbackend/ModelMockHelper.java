package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.model.entity.Customer;

import java.time.LocalDateTime;

public class ModelMockHelper {

    public static Appointment createSingleAppointment() {
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setPlz("12345");
        customer.setCity("Foo City");
        customer.setEmail("max@example.com");
        customer.setMobile("0987654321");
        customer.setTel("0987654");

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setState(AppointmentState.NONE);
        appointment.setDate(LocalDateTime.now());
        appointment.setTandem(1);
        appointment.setVideo(1);

        return appointment;
    }

    public static Appointment createSecondAppointment() {
        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setPlz("54321");
        customer.setCity("Bar City");
        customer.setEmail("jane@example.com");
        customer.setMobile("0123456789");
        customer.setTel("01234567");

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setState(AppointmentState.NONE);
        appointment.setDate(LocalDateTime.now());
        appointment.setTandem(2);
        appointment.setVideo(0);

        return appointment;
    }
}
