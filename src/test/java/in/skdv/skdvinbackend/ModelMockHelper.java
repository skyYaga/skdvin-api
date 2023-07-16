package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.model.common.AbstractFlyer;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.domain.PublicSettings;
import in.skdv.skdvinbackend.model.dto.*;
import in.skdv.skdvinbackend.model.entity.*;
import in.skdv.skdvinbackend.model.entity.settings.*;
import in.skdv.skdvinbackend.model.entity.voucher.legacy.LegacyVoucherDocument;
import in.skdv.skdvinbackend.model.mapper.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class ModelMockHelper {

    private static final ZoneId zoneId = ZoneId.of("Europe/Berlin");
    private static final VideoflyerMapper VIDEOFLYER_MAPPER = new VideoflyerMapperImpl();
    private static final TandemmasterMapper TANDEMMASTER_MAPPER = new TandemmasterMapperImpl();
    private static final JumpdayMapper JUMPDAY_MAPPER = new JumpdayMapperImpl(new AssignmentMapperImpl(VIDEOFLYER_MAPPER, TANDEMMASTER_MAPPER));
    private static final SettingsMapper SETTINGS_MAPPER = new SettingsMapperImpl();
    private static final AppointmentMapper APPOINTMENT_MAPPER = new AppointmentMapperImpl();

    public static final String DZ_EMAIL = "dz@example.com";

    public static AppointmentDTO createAppointmentDto() {
        return APPOINTMENT_MAPPER.toDto(createSingleAppointment());
    }

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
        appointment.setDate(ZonedDateTime.of(LocalDate.now(), LocalTime.of(10, 0), zoneId).toInstant());
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
        appointment.setDate(ZonedDateTime.of(LocalDate.now(), LocalTime.of(10, 0), zoneId).toInstant());
        appointment.setTandem(2);
        appointment.setPicOrVid(0);
        appointment.setPicAndVid(0);
        appointment.setHandcam(0);

        return appointment;
    }

    public static Jumpday createJumpday(LocalDate date) {
        Jumpday jumpday = new Jumpday();
        jumpday.setDate(date);
        jumpday.setTimezone(zoneId);
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

    public static JumpdayDTO createJumpdayDto() {
        return JUMPDAY_MAPPER.toDto(createJumpday());
    }

    public static Jumpday createJumpday() {
        return createJumpday(LocalDate.now());
    }

    public static TandemmasterDTO createTandemmasterDto() {
        return TANDEMMASTER_MAPPER.toDto(createTandemmaster());
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

    public static VideoflyerDTO createVideoflyerDto() {
        return VIDEOFLYER_MAPPER.toDto(createVideoflyer());
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
        assignment.setNote("Example Note");
        return assignment;
    }

    public static SettingsDTO createSettingsDto() {
        return SETTINGS_MAPPER.toDto(createSettings(Locale.GERMAN));
    }

    public static Settings createSettings() {
        return createSettings(Locale.GERMAN, SelfAssignmentMode.READONLY);
    }

    public static Settings createSettings(Locale locale) {
        return createSettings(locale, SelfAssignmentMode.READONLY);
    }

    public static Settings createSettings(SelfAssignmentMode mode) {
        return createSettings(Locale.GERMAN, mode);
    }

    private static Settings createSettings(Locale locale, SelfAssignmentMode mode) {
        AdminSettings adminSettings = new AdminSettings();
        adminSettings.setTandemsFrom(LocalTime.of(10, 0));
        adminSettings.setTandemsTo(LocalTime.of(18, 0));
        adminSettings.setInterval("1:30");
        adminSettings.setTandemCount(5);
        adminSettings.setPicOrVidCount(2);
        adminSettings.setPicAndVidCount(0);
        adminSettings.setHandcamCount(0);

        CommonSettings commonSettings = CommonSettings.builder()
                .picAndVidEnabled(true)
                .selfAssignmentMode(mode)
                .build();

        Map<String, LanguageSettings> languageSettingsMap = new HashMap<>(Map.of(locale.getLanguage(), createLanguageSettings()));

        Settings settings = new Settings();
        settings.setAdminSettings(adminSettings);
        settings.setLanguageSettings(languageSettingsMap);
        settings.setCommonSettings(commonSettings);

        return settings;
    }

    public static PublicSettings createPublicSettings() {
        Settings settings = createSettings();
        return new PublicSettings(settings.getCommonSettings(), createLanguageSettings());
    }

    public static PublicSettings createPublicSettings(SelfAssignmentMode selfAssignmentMode) {
        Settings settings = createSettings();
        settings.getCommonSettings().setSelfAssignmentMode(selfAssignmentMode);
        return new PublicSettings(settings.getCommonSettings(), createLanguageSettings());
    }

    public static LanguageSettings createLanguageSettings() {
        Dropzone dropzone = new Dropzone();
        dropzone.setName("Example DZ");
        dropzone.setPriceListUrl("https://example.com");
        dropzone.setMobile("015112345678");
        dropzone.setPhone("0987654321");
        dropzone.setEmail(DZ_EMAIL);

        Faq faq1 = new Faq();
        faq1.setId(1);
        faq1.setQuestion("Foo?");
        faq1.setAnswer("Bar");
        Faq faq2 = new Faq();
        faq2.setId(2);
        faq2.setQuestion("Question?");
        faq2.setAnswer("Answer!");

        List<Faq> faqList = Arrays.asList(faq1, faq2);

        return LanguageSettings.builder()
                .dropzone(dropzone)
                .faq(faqList)
                .additionalReminderHint("<p>This is a additional hint</p><ul><li>Hint 1</li><li>Hint 2</li></ul>")
                .build();
    }

    public static TandemmasterDetails addTandemmasterAssignment(Tandemmaster tandemmaster, LocalDate date) {
        return TANDEMMASTER_MAPPER.toDetails(tandemmaster, Map.of(date, new SimpleAssignment(true)));
    }

    public static TandemmasterDetails removeTandemmasterAssignment(Tandemmaster tandemmaster, LocalDate date) {
        return TANDEMMASTER_MAPPER.toDetails(tandemmaster, Map.of(date, new SimpleAssignment(false)));
    }

    public static void addTandemmasterAssignment(TandemmasterDetails tandemmasterDetails, LocalDate date) {
        tandemmasterDetails.getAssignments().put(date, new SimpleAssignment(true));
    }

    public static VideoflyerDetails addVideoflyerAssignment(Videoflyer videoflyer, LocalDate date) {
        return VIDEOFLYER_MAPPER.toDetails(videoflyer, Map.of(date, new SimpleAssignment(true)));
    }

    public static VideoflyerDetails removeVideoflyerAssignment(Videoflyer videoflyer, LocalDate date) {
        return VIDEOFLYER_MAPPER.toDetails(videoflyer, Map.of(date, new SimpleAssignment(false)));
    }

    public static void addVideoflyerAssignment(VideoflyerDetails videoflyerDetails, LocalDate date) {
        videoflyerDetails.getAssignments().put(date, new SimpleAssignment(true));
    }

    public static LegacyVoucherDocument createVoucherDocument() {
        LegacyVoucherDocument legacyVoucherDocument = new LegacyVoucherDocument();
        legacyVoucherDocument.setId("10000");
        legacyVoucherDocument.setFirstName("John");
        legacyVoucherDocument.setLastName("Doe");
        return legacyVoucherDocument;
    }
}
