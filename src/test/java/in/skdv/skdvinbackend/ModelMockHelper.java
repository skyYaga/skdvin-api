package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ModelMockHelper {

    public static Appointment createSingleAppointment() {
        return createAppointment(1, 1, 0, 0);
    }

    public static Appointment createAppointment(int tandemCount, int picOrVid, int picAndVid, int handcam) {
        Customer customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setZip("12345");
        customer.setCity("Foo City");
        customer.setEmail("max@example.com");
        customer.setTel("0987654");
        customer.setJumpers(createJumpers(tandemCount));

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setState(AppointmentState.UNCONFIRMED);
        appointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
        appointment.setTandem(tandemCount);
        appointment.setPicOrVid(picOrVid);
        appointment.setPicAndVid(picAndVid);
        appointment.setHandcam(handcam);

        return appointment;
    }

    public static Appointment createSecondAppointment() {
        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setZip("54321");
        customer.setCity("Bar City");
        customer.setEmail("jane@example.com");
        customer.setTel("01234567");
        customer.setJumpers(createJumpers(2));

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setState(AppointmentState.CONFIRMED);
        appointment.setDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
        appointment.setTandem(2);
        appointment.setPicOrVid(0);
        appointment.setPicAndVid(0);
        appointment.setHandcam(0);

        return appointment;
    }

    private static User createUser() {
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
        jumpday.setVideoflyer(new ArrayList<>());
        jumpday.setTandemmaster(new ArrayList<>());

        Slot slot = new Slot();
        slot.setTime(LocalTime.of(10, 0));
        slot.setTandemTotal(4);
        slot.setPicOrVidTotal(2);
        slot.setPicAndVidTotal(1);
        slot.setHandcamTotal(1);
        Slot slot2 = new Slot();
        slot2.setTime(LocalTime.of(11, 30));
        slot2.setTandemTotal(4);
        slot2.setPicOrVidTotal(2);
        slot2.setPicAndVidTotal(1);
        slot2.setHandcamTotal(1);

        jumpday.setSlots(new ArrayList<>(Arrays.asList(slot, slot2)));

        return jumpday;
    }

    public static Jumpday createJumpday() {
        return createJumpday(LocalDate.now());
    }

    public static Tandemmaster createTandemmaster() {
        return createTandemmaster("Max", "Mustermann");
    }

    public static Tandemmaster createTandemmaster(String firstName, String lastName) {
        Tandemmaster tandemmaster = new Tandemmaster();
        tandemmaster.setFirstName(firstName);
        tandemmaster.setLastName(lastName);
        return tandemmaster;
    }

    public static Videoflyer createVideoflyer() {
        return createVideoflyer("Max", "Mustermann");
    }

    public static Videoflyer createVideoflyer(String firstName, String lastName) {
        Videoflyer videoflyer = new Videoflyer();
        videoflyer.setFirstName(firstName);
        videoflyer.setLastName(lastName);
        return videoflyer;
    }

    public static List<Jumper> createJumpers(int tandemCount) {
        List<Jumper> jumpers = new ArrayList<>();
        for (int i = 0; i < tandemCount; i++) {
            jumpers.add(new Jumper(
                    "first" + i,
                    "last" + i,
                    LocalDate.of(1980, 1, 1)
            ));
        }
        return jumpers;
    }

    public static <T extends AbstractFlyer> Assignment<T> createAssignment(T flyer) {
        Assignment<T> assignment = new Assignment<>();
        assignment.setAssigned(true);
        assignment.setFlyer(flyer);
        return assignment;
    }
}
