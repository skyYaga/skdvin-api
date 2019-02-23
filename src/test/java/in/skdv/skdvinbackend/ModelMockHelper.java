package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.model.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class ModelMockHelper {

    public static Appointment createSingleAppointment() {
        return createAppointment(1, 1);
    }

    public static Appointment createAppointment(int tandemCount, int videoCount) {
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
        appointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
        appointment.setTandem(tandemCount);
        appointment.setVideo(videoCount);

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
        appointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
        appointment.setTandem(2);
        appointment.setVideo(0);

        return appointment;
    }

    public static User createUser() {
        return new User("max", "s3cr3t$!", "max@example.com", Collections.singletonList(Role.ROLE_USER));
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

    public static Jumpday createJumpday(LocalDate date) {
        Jumpday jumpday = new Jumpday();
        jumpday.setDate(date);
        jumpday.setJumping(true);
        jumpday.setTandemmaster(Collections.singletonList("Tandem Master"));
        jumpday.setVideoflyer(Collections.singletonList("Video Flyer"));

        Slot slot = new Slot();
        slot.setTime(LocalTime.of(10, 0));
        slot.setTandemTotal(4);
        slot.setVideoTotal(2);
        Slot slot2 = new Slot();
        slot2.setTime(LocalTime.of(11, 30));
        slot2.setTandemTotal(4);
        slot2.setVideoTotal(2);

        jumpday.setSlots(Arrays.asList(slot, slot2));

        return jumpday;
    }

    public static Jumpday createJumpday() {
        return createJumpday(LocalDate.now());
    }
}
