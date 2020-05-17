package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.entity.*;
import in.skdv.skdvinbackend.model.entity.settings.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public static Settings createSettings() {
        return createSettings(Locale.GERMAN);
    }

    public static Settings createSettings(Locale locale) {
        AdminSettings adminSettings = new AdminSettings();
        adminSettings.setTandemsFrom(LocalTime.of(10, 0));
        adminSettings.setTandemsTo(LocalTime.of(18, 0));
        adminSettings.setInterval("1:30");
        adminSettings.setTandemCount(5);
        adminSettings.setPicOrVidCount(2);
        adminSettings.setPicAndVidCount(0);
        adminSettings.setHandcamCount(0);

        Map<String, CommonSettings> commonSettingsMap = new HashMap<>(Map.of(locale.getLanguage(), createCommonSettings()));

        Settings settings = new Settings();
        settings.setAdminSettings(adminSettings);
        settings.setCommonSettings(commonSettingsMap);

        return settings;
    }

    public static CommonSettings createCommonSettings() {
        Dropzone dropzone = new Dropzone();
        dropzone.setName("Example DZ");
        dropzone.setPriceListUrl("https://example.com");
        dropzone.setMobile("015112345678");
        dropzone.setPhone("0987654321");
        dropzone.setEmail("dz@example.com");

        Faq faq1 = new Faq();
        faq1.setId(1);
        faq1.setQuestion("Foo?");
        faq1.setAnswer("Bar");
        Faq faq2 = new Faq();
        faq2.setId(2);
        faq2.setQuestion("Question?");
        faq2.setAnswer("Answer!");

        List<Faq> faqList = Arrays.asList(faq1, faq2);

        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setDropzone(dropzone);
        commonSettings.setFaq(faqList);

        return commonSettings;
    }

}
