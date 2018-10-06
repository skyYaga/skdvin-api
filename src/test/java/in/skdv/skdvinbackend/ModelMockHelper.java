package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.model.entity.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

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

    public static User createUser() {
        return new User("max", "s3cr3t", "max@example.com", Collections.singletonList(Role.ROLE_USER));
    }

    public static User createUserWithVerificationToken() {
        User user = createUser();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plus(24, ChronoUnit.HOURS));
        user.setVerificationToken(verificationToken);
        return user;
    }

    public static User createUserWithPasswordResetToken() {
        User user = createUser();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plus(24, ChronoUnit.HOURS));
        user.setPasswordResetToken(verificationToken);
        return user;
    }
}
