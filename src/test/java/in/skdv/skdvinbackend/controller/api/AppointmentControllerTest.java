package in.skdv.skdvinbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.converter.AppointmentConverter;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.dto.AppointmentStateOnlyDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentControllerTest extends AbstractSkdvinTest {

    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private ISettingsService settingsService;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private IAppointmentService appointmentService;

    @Autowired
    private AppointmentConverter appointmentConverter;

    @BeforeEach
    void setup() {
        // Set mock clock
        Clock mockClock = Clock.fixed(Instant.parse(LocalDate.now() + "T00:00:00Z"), ZoneOffset.UTC);
        ReflectionTestUtils.setField(appointmentService, "clock", mockClock);

        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());

        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());

        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);
        doReturn(new JavaMailSenderImpl().createMimeMessage()).when(mailSender).createMimeMessage();
        when(settingsService.getCommonSettingsByLanguage(Mockito.anyString())).
                thenReturn(ModelMockHelper.createCommonSettings());
    }

    @Test
    void testGetOne() throws Exception {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(get("/api/appointment/{id}", appointment.getAppointmentId())
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.appointmentId", is(appointment.getAppointmentId())))
                .andExpect(jsonPath("$.payload.customer.firstName", is(appointment.getCustomer().getFirstName())));
    }

    @Test
    void testGetOne_NotFound() throws Exception {
        mockMvc.perform(get("/api/appointment/{id}", 999999)
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void testGetOneUnauthorized() throws Exception {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(get("/api/appointment/" + appointment.getAppointmentId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAddAppointment() throws Exception {
        String appointmentJson = json(ModelMockHelper.createAppointmentDto());

        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")));
    }

    @Test
    void testAddAppointment_NoSlotLeft() throws Exception {
        String appointmentJson = json(ModelMockHelper.createAppointment(2, 0, 0, 0));

        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "de-DE")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Sprungtag hat nicht genügend freie Slots")));
    }

    @Test
    void testAddAppointment_NoJumpDay() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setDate(appointment.getDate().plus(10, ChronoUnit.DAYS));
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "de-DE")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Sprungtag nicht gefunden")));
    }

    @Test
    void testAddAppointment_MorePicOrVidThanTandemSlots() throws Exception {
        Appointment appointment = ModelMockHelper.createAppointment(1, 2, 0, 0);
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testAddAppointment_MorePicAndVidThanTandemSlots() throws Exception {
        Appointment appointment = ModelMockHelper.createAppointment(1, 0, 2, 0);
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testAddAppointment_MoreHandCamThanTandemSlots() throws Exception {
        Appointment appointment = ModelMockHelper.createAppointment(1, 0, 0, 2);
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testAddAppointment_MorePicVidHandCamThanTandemSlots() throws Exception {
        Appointment appointment = ModelMockHelper.createAppointment(1, 1, 1, 1);
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testAddAdminAppointment() throws Exception {
        AppointmentDTO appointment = ModelMockHelper.createAppointmentDto();
        appointment.getCustomer().setJumpers(Collections.emptyList());
        String appointmentJson = json(appointment);

        mockMvc.perform(post("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")));
    }


    @Test
    void testAddAdminAppointment_Error() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setPicAndVid(5);
        String appointmentJson = json(appointment);

        mockMvc.perform(post("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }


    @Test
    void testUpdateAppointment() throws Exception {
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        int newCount = appointment.getTandem() + 1;

        appointment.setTandem(newCount);
        appointment.getCustomer().setFirstName("Unitjane");
        appointment.getCustomer().setJumpers(ModelMockHelper.createJumpers(2));

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandem", is(newCount)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Unitjane")))
                .andExpect(jsonPath("$.payload.customer.jumpers", hasSize(2)));
    }

    @Test
    void testUpdateAdminAppointment() throws Exception {
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        int newCount = appointment.getTandem() + 1;

        appointment.setTandem(newCount);
        appointment.getCustomer().setJumpers(Collections.emptyList());

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandem", is(newCount)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")))
                .andExpect(jsonPath("$.payload.customer.jumpers", hasSize(0)));
    }

    @Test
    void testUpdateAdminAppointment_Error() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setAppointmentId(99999999);
        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Appointment not found")));
    }

    @Test
    void testUpdateAppointmentUnauthorized() throws Exception {
        Appointment appointment = appointmentService.findAppointmentsByDay(LocalDate.now()).get(0);

        appointment.setTandem(10);
        appointment.getCustomer().setFirstName("Unitjane");

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment/")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateAppointment_NoJumpDay() throws Exception {
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setDate(appointment.getDate().plus(10, ChronoUnit.DAYS));

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "de-DE")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Sprungtag nicht gefunden")));
    }

    @Test
    void testUpdateAppointment_MorePicAndVidThanTandemSlots() throws Exception {
        Appointment result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 0, 1, 0));
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getAppointmentId()));

        appointment.setPicOrVid(appointment.getPicAndVid() + 1);

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testUpdateAppointment_MorePicOrVidThanTandemSlots() throws Exception {
        Appointment result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 1, 0, 0));
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getAppointmentId()));

        appointment.setPicOrVid(appointment.getPicOrVid() + 1);

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testUpdateAppointment_MoreHandcamThanTandemSlots() throws Exception {
        Appointment result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 0, 0, 1));
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getAppointmentId()));

        appointment.setPicOrVid(appointment.getHandcam() + 1);

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testUpdateAppointment_MorePicVidHandcamThanTandemSlots() throws Exception {
        Appointment result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 0, 1, 0));
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getAppointmentId()));

        appointment.setPicOrVid(appointment.getPicOrVid() + 1);
        appointment.setPicOrVid(appointment.getHandcam() + 1);

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testUpdateAppointment_NoSlotLeft() throws Exception {
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setTandem(10);
        appointment.getCustomer().setJumpers(ModelMockHelper.createJumpers(10));

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Jumpday has not enough free slots")));
    }

    @Test
    void testFindFreeSlots() throws Exception {
        SlotQuery query = new SlotQuery(2, 0, 0, 0);

        mockMvc.perform(get("/api/appointment/slots")
                .header("Accept-Language", "en-US")
                .param("tandem", String.valueOf(query.getTandem()))
                .param("picOrVid", String.valueOf(query.getPicOrVid()))
                .param("picAndVid", String.valueOf(query.getPicAndVid()))
                .param("handcam", String.valueOf(query.getHandcam())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload[0].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.payload[0].times[0]", is(LocalTime.of(11, 30).toString())));
    }

    @Test
    void testFindFreeSlots_MoreVideoThanTandem_DE() throws Exception {
        SlotQuery query = new SlotQuery(1, 2, 0, 0);

        mockMvc.perform(get("/api/appointment/slots")
                .header("Accept-Language", "de-DE")
                .param("tandem", String.valueOf(query.getTandem()))
                .param("picOrVid", String.valueOf(query.getPicOrVid()))
                .param("picAndVid", String.valueOf(query.getPicAndVid()))
                .param("handcam", String.valueOf(query.getHandcam())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Der Termin hat mehr gebuchte Video- als Tandem-Slots.")));
    }

    @Test
    void testFindFreeSlots_MoreVideoThanTandem_EN() throws Exception {
        SlotQuery query = new SlotQuery(1, 2, 0, 0);

        mockMvc.perform(get("/api/appointment/slots")
                .header("Accept-Language", "en-US")
                .param("tandem", String.valueOf(query.getTandem()))
                .param("picOrVid", String.valueOf(query.getPicOrVid()))
                .param("picAndVid", String.valueOf(query.getPicAndVid()))
                .param("handcam", String.valueOf(query.getHandcam())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    void testFindFreeSlots_NoFreeSlots() throws Exception {
        SlotQuery query = new SlotQuery(2, 0, 0, 2);

        mockMvc.perform(get("/api/appointment/slots")
                .header("Accept-Language", "en-US")
                .param("tandem", String.valueOf(query.getTandem()))
                .param("picOrVid", String.valueOf(query.getPicOrVid()))
                .param("picAndVid", String.valueOf(query.getPicAndVid()))
                .param("handcam", String.valueOf(query.getHandcam())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("No free appointments found")))
                .andExpect(jsonPath("$.payload", nullValue()));
    }

    @Test
    void testConfirmAppointment() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        mockMvc.perform(get("/api/appointment/{appointmentId}/confirm/{token}",
                appointment.getAppointmentId(), savedAppointment.getVerificationToken().getToken())
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testConfirmAppointment_InvalidToken() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointmentService.saveAppointment(appointment);

        mockMvc.perform(get("/api/appointment/{appointmentId}/confirm/{token}",
                appointment.getAppointmentId(), "foo")
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Confirmation Token invalid")));
    }

    @Test
    void testConfirmAppointment_ExpiredToken() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointment.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(1, ChronoUnit.HOURS));

        Jumpday jumpday = jumpdayRepository.findByDate(LocalDate.now());
        jumpday.addAppointment(appointment);
        jumpdayRepository.save(jumpday);

        mockMvc.perform(get("/api/appointment/{appointmentId}/confirm/{token}",
                appointment.getAppointmentId(), appointment.getVerificationToken().getToken())
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Confirmation Token invalid")));
    }

    @Test
    void testConfirmAppointment_AlreadyConfirmed() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointment.setState(AppointmentState.CONFIRMED);
        appointmentService.saveAppointment(appointment);

        mockMvc.perform(get("/api/appointment/{appointmentId}/confirm/{token}",
                appointment.getAppointmentId(), appointment.getVerificationToken().getToken())
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Appointment already confirmed")));
    }

    @Test
    void testConfirmAppointment_InvalidAppointment() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointment.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(1, ChronoUnit.HOURS));
        appointmentService.saveAppointment(appointment);

        mockMvc.perform(get("/api/appointment/{appointmentId}/confirm/{token}",
                9999999, appointment.getVerificationToken().getToken())
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Appointment not found")));
    }

    @Test
    void testGetAppointmentsByDay() throws Exception {
        mockMvc.perform(get("/api/appointment/date/{date}",
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)));
    }

    @Test
    void testGetAppointmentsByDay_Unauthorized() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        mockMvc.perform(get("/api/appointment/date/{date}",
                appointment.getDate())
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateAppointmentState() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        AppointmentStateOnlyDTO appointmentStateOnly = new AppointmentStateOnlyDTO();
        appointmentStateOnly.setState(AppointmentState.ACTIVE);
        String appointmentStateOnlyJson = json(appointmentStateOnly);

        mockMvc.perform(patch("/api/appointment/{appointmentId}",
                savedAppointment.getAppointmentId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentStateOnlyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        assertEquals(AppointmentState.ACTIVE, appointmentService.findAppointment(savedAppointment.getAppointmentId()).getState());
    }

    @Test
    void testUpdateAppointmentState_AdminBooking() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.getCustomer().setJumpers(Collections.emptyList());
        Appointment savedAppointment = appointmentService.saveAdminAppointment(appointment);

        AppointmentStateOnlyDTO appointmentStateOnly = new AppointmentStateOnlyDTO();
        appointmentStateOnly.setState(AppointmentState.ACTIVE);
        String appointmentStateOnlyJson = json(appointmentStateOnly);

        mockMvc.perform(patch("/api/appointment/{appointmentId}",
                savedAppointment.getAppointmentId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentStateOnlyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        assertEquals(AppointmentState.ACTIVE, appointmentService.findAppointment(savedAppointment.getAppointmentId()).getState());
    }

    @Test
    void testUpdateAppointmentState_Unauthorized() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);

        AppointmentStateOnlyDTO appointmentStateOnly = new AppointmentStateOnlyDTO();
        appointmentStateOnly.setState(AppointmentState.ACTIVE);
        String appointmentStateOnlyJson = json(appointmentStateOnly);

        mockMvc.perform(patch("/api/appointment/{appointmentId}",
                savedAppointment.getAppointmentId())
                .contentType(contentType)
                .content(appointmentStateOnlyJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateAppointmentState_AppointmentNotFound() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointmentService.saveAppointment(appointment);

        AppointmentStateOnlyDTO appointmentStateOnly = new AppointmentStateOnlyDTO();
        appointmentStateOnly.setState(AppointmentState.ACTIVE);
        String appointmentStateOnlyJson = json(appointmentStateOnly);

        mockMvc.perform(patch("/api/appointment/{appointmentId}",
                99999999)
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentStateOnlyJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Appointment not found")));
    }

    @Test
    void testDeleteAppointment() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        Appointment result = appointmentService.saveAppointment(appointment);
        int appointmentId = result.getAppointmentId();

        mockMvc.perform(delete("/api/appointment/{appointmentId}", appointmentId)
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testDeleteAppointment_NotFound() throws Exception {
        mockMvc.perform(delete("/api/appointment/{appointmentId}", 999)
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Appointment not found")));
    }

    @Test
    void testDeleteAppointment_Unauthorized() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        Appointment result = appointmentService.saveAppointment(appointment);
        int appointmentId = result.getAppointmentId();


        mockMvc.perform(delete("/api/appointment/{appointmentId}", appointmentId)
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteAppointment_Invalid() throws Exception {
        mockMvc.perform(delete("/api/appointment/{appointmentId}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }


    @Test
    void testFindGroupSlots() throws Exception {
        SlotQuery query = new SlotQuery(5, 0, 0, 0);

        mockMvc.perform(get("/api/appointment/groupslots")
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS))
                .param("tandem", String.valueOf(query.getTandem())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.payload[0].slots", hasSize(2)));
    }

    @Test
    void testFindGroupSlots_Unauthorized() throws Exception {
        SlotQuery query = new SlotQuery(5, 0, 0, 0);

        mockMvc.perform(get("/api/appointment/groupslots")
                .param("tandem", String.valueOf(query.getTandem())))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }
}
