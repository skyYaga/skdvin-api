package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.converter.AppointmentConverter;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.dto.AppointmentStateOnlyDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.GenericResult;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class AppointmentControllerTest extends AbstractSkdvinTest {

    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

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
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        // Set mock clock
        Clock mockClock = Clock.fixed(Instant.parse(LocalDate.now().toString() + "T00:00:00Z"), ZoneOffset.UTC);
        ReflectionTestUtils.setField(appointmentService, "clock", mockClock);

        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .build();

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
        GenericResult<Appointment> appointmentGenericResult = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        Appointment appointment = appointmentGenericResult.getPayload();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/{id}", appointment.getAppointmentId())
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.appointmentId", is(appointment.getAppointmentId())))
                .andExpect(jsonPath("$.payload.customer.firstName", is(appointment.getCustomer().getFirstName())))
                .andDo(document("appointment/get-appointment",
                    pathParameters(
                        parameterWithName("id").description("The id of the requested appointment")
                    ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload"),
                                fieldWithPath("payload.appointmentId").description("The created appointment's id"),
                                fieldWithPath("payload.customer").description("Customer Details"),
                                fieldWithPath("payload.customer.firstName").description("Customer's first name"),
                                fieldWithPath("payload.customer.lastName").description("Customer's last name"),
                                fieldWithPath("payload.customer.tel").description("Customer's phone number"),
                                fieldWithPath("payload.customer.email").description("Customer's email address"),
                                fieldWithPath("payload.customer.zip").description("Customer's zip code"),
                                fieldWithPath("payload.customer.city").description("Customer's city"),
                                fieldWithPath("payload.customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("payload.customer.jumpers[].firstName").description("Jumper's first name"),
                                fieldWithPath("payload.customer.jumpers[].lastName").description("Jumper's last name"),
                                fieldWithPath("payload.customer.jumpers[].dateOfBirth").description("Jumper's date of birth"),
                                fieldWithPath("payload.customer.jumpers[].voucher").description("true if the jumper has a voucher"),
                                fieldWithPath("payload.date").description("Appointments date/time"),
                                fieldWithPath("payload.tandem").description("Tandem count"),
                                fieldWithPath("payload.picOrVid").description("picture or video count"),
                                fieldWithPath("payload.picAndVid").description("picture and video count"),
                                fieldWithPath("payload.handcam").description("handcam count"),
                                fieldWithPath("payload.note").description("An optional note for the appointment"),
                                fieldWithPath("payload.state").ignored(),
                                fieldWithPath("payload.createdOn").ignored(),
                                fieldWithPath("payload.createdBy").ignored(),
                                fieldWithPath("payload.clientId").ignored()
                        )));
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
        GenericResult<Appointment> appointmentGenericResult = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        Appointment appointment = appointmentGenericResult.getPayload();

        mockMvc.perform(get("/api/appointment/" + appointment.getAppointmentId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAddAppointment() throws Exception {
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/appointment")
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")))
                .andDo(document("appointment/add-appointment",
                        requestFields(
                                fieldWithPath("customer").description("Customer Details"),
                                fieldWithPath("customer.firstName").description("Customer's first name"),
                                fieldWithPath("customer.lastName").description("Customer's last name"),
                                fieldWithPath("customer.tel").description("Customer's phone number"),
                                fieldWithPath("customer.email").description("Customer's email address"),
                                fieldWithPath("customer.zip").description("Customer's zip code"),
                                fieldWithPath("customer.city").description("Customer's city"),
                                fieldWithPath("customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("customer.jumpers[].firstName").description("Jumper's first name"),
                                fieldWithPath("customer.jumpers[].lastName").description("Jumper's last name"),
                                fieldWithPath("customer.jumpers[].dateOfBirth").description("Jumper's date of birth"),
                                fieldWithPath("customer.jumpers[].voucher").description("true if the jumper has a voucher"),
                                fieldWithPath("date").description("Appointments date/time"),
                                fieldWithPath("tandem").description("Tandem count"),
                                fieldWithPath("picOrVid").description("picture or video count"),
                                fieldWithPath("picAndVid").description("picture and video count"),
                                fieldWithPath("handcam").description("handcam count"),
                                fieldWithPath("note").description("An optional note for the appointment"),
                                fieldWithPath("state").ignored(),
                                fieldWithPath("createdOn").ignored(),
                                fieldWithPath("createdBy").ignored(),
                                fieldWithPath("clientId").ignored(),
                                fieldWithPath("lang").ignored(),
                                fieldWithPath("reminderSent").ignored(),
                                fieldWithPath("verificationToken").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload"),
                                fieldWithPath("payload.appointmentId").description("The created appointment's id"),
                                fieldWithPath("payload.customer").description("Customer Details"),
                                fieldWithPath("payload.customer.firstName").description("Customer's first name"),
                                fieldWithPath("payload.customer.lastName").description("Customer's last name"),
                                fieldWithPath("payload.customer.tel").description("Customer's phone number"),
                                fieldWithPath("payload.customer.email").description("Customer's email address"),
                                fieldWithPath("payload.customer.zip").description("Customer's zip code"),
                                fieldWithPath("payload.customer.city").description("Customer's city"),
                                fieldWithPath("payload.customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("payload.customer.jumpers[].firstName").description("Jumper's first name"),
                                fieldWithPath("payload.customer.jumpers[].lastName").description("Jumper's last name"),
                                fieldWithPath("payload.customer.jumpers[].dateOfBirth").description("Jumper's date of birth"),
                                fieldWithPath("payload.customer.jumpers[].voucher").description("true if the jumper has a voucher"),
                                fieldWithPath("payload.date").description("Appointments date/time"),
                                fieldWithPath("payload.tandem").description("Tandem count"),
                                fieldWithPath("payload.picOrVid").description("picture or video count"),
                                fieldWithPath("payload.picAndVid").description("picture and video count"),
                                fieldWithPath("payload.handcam").description("handcam count"),
                                fieldWithPath("payload.note").description("An optional note for the appointment"),
                                fieldWithPath("payload.state").ignored(),
                                fieldWithPath("payload.createdOn").ignored(),
                                fieldWithPath("payload.createdBy").ignored(),
                                fieldWithPath("payload.clientId").ignored()
                        )));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertTrue(argument.getValue().getSubject().startsWith("Confirm your booking"));
    }

    @Test
    void testAddAppointment_NoSlotLeft() throws Exception {
        appointmentService.saveAppointment(ModelMockHelper.createAppointment(3, 0, 0, 0));

        String appointmentJson = json(ModelMockHelper.createAppointment(2, 0, 0, 0));

        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "de-DE")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Sprungtag hat nicht genügend freie Slots")));
    }

    @Test
    void testAddAppointment_NoJumpDay() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setDate(appointment.getDate().plusDays(10));
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment")
                .header("Accept-Language", "de-DE")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
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
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.getCustomer().setJumpers(Collections.emptyList());
        String appointmentJson = json(appointment);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")))
                .andDo(document("appointment/add-admin-appointment",
                        requestFields(
                                fieldWithPath("customer").description("Customer Details"),
                                fieldWithPath("customer.firstName").description("Customer's first name"),
                                fieldWithPath("customer.lastName").description("Customer's last name"),
                                fieldWithPath("customer.tel").description("Customer's phone number"),
                                fieldWithPath("customer.email").description("Customer's email address"),
                                fieldWithPath("customer.zip").description("Customer's zip code"),
                                fieldWithPath("customer.city").description("Customer's city"),
                                fieldWithPath("customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("date").description("Appointments date/time"),
                                fieldWithPath("tandem").description("Tandem count"),
                                fieldWithPath("picOrVid").description("picture or video count"),
                                fieldWithPath("picAndVid").description("picture and video count"),
                                fieldWithPath("handcam").description("handcam count"),
                                fieldWithPath("note").description("An optional note for the appointment"),
                                fieldWithPath("state").ignored(),
                                fieldWithPath("createdOn").ignored(),
                                fieldWithPath("createdBy").ignored(),
                                fieldWithPath("clientId").ignored(),
                                fieldWithPath("lang").ignored(),
                                fieldWithPath("reminderSent").ignored(),
                                fieldWithPath("verificationToken").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload"),
                                fieldWithPath("payload.appointmentId").description("The created appointment's id"),
                                fieldWithPath("payload.customer").description("Customer Details"),
                                fieldWithPath("payload.customer.firstName").description("Customer's first name"),
                                fieldWithPath("payload.customer.lastName").description("Customer's last name"),
                                fieldWithPath("payload.customer.tel").description("Customer's phone number"),
                                fieldWithPath("payload.customer.email").description("Customer's email address"),
                                fieldWithPath("payload.customer.zip").description("Customer's zip code"),
                                fieldWithPath("payload.customer.city").description("Customer's city"),
                                fieldWithPath("payload.customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("payload.date").description("Appointments date/time"),
                                fieldWithPath("payload.tandem").description("Tandem count"),
                                fieldWithPath("payload.picOrVid").description("picture or video count"),
                                fieldWithPath("payload.picAndVid").description("picture and video count"),
                                fieldWithPath("payload.handcam").description("handcam count"),
                                fieldWithPath("payload.note").description("An optional note for the appointment"),
                                fieldWithPath("payload.state").ignored(),
                                fieldWithPath("payload.createdOn").ignored(),
                                fieldWithPath("payload.createdBy").ignored(),
                                fieldWithPath("payload.clientId").ignored()
                        )));
    }


    @Test
    void testAddAdminAppointment_Error() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setPicAndVid(5);
        String appointmentJson = json(appointment);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/appointment/admin")
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
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        int newCount = appointment.getTandem() + 1;

        appointment.setTandem(newCount);
        appointment.getCustomer().setFirstName("Unitjane");
        appointment.getCustomer().setJumpers(ModelMockHelper.createJumpers(2));

        String appointmentJson = json(appointment);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandem", is(newCount)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Unitjane")))
                .andExpect(jsonPath("$.payload.customer.jumpers", hasSize(2)))
                .andDo(document("appointment/update-admin-appointment",
                        requestFields(
                                fieldWithPath("appointmentId").description("The appointment's id"),
                                fieldWithPath("customer").description("Customer Details"),
                                fieldWithPath("customer.firstName").description("Customer's first name"),
                                fieldWithPath("customer.lastName").description("Customer's last name"),
                                fieldWithPath("customer.tel").description("Customer's phone number"),
                                fieldWithPath("customer.email").description("Customer's email address"),
                                fieldWithPath("customer.zip").description("Customer's zip code"),
                                fieldWithPath("customer.city").description("Customer's city"),
                                fieldWithPath("customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("customer.jumpers[].firstName").description("Jumper's first name"),
                                fieldWithPath("customer.jumpers[].lastName").description("Jumper's last name"),
                                fieldWithPath("customer.jumpers[].dateOfBirth").description("Jumper's date of birth"),
                                fieldWithPath("customer.jumpers[].voucher").description("true if the jumper has a voucher"),
                                fieldWithPath("date").description("Appointments date/time"),
                                fieldWithPath("tandem").description("Tandem count"),
                                fieldWithPath("picOrVid").description("picture or video count"),
                                fieldWithPath("picAndVid").description("picture and video count"),
                                fieldWithPath("handcam").description("handcam count"),
                                fieldWithPath("note").description("An optional note for the appointment"),
                                fieldWithPath("state").ignored(),
                                fieldWithPath("createdOn").ignored(),
                                fieldWithPath("createdBy").ignored(),
                                fieldWithPath("clientId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload"),
                                fieldWithPath("payload.appointmentId").description("The appointment's id"),
                                fieldWithPath("payload.customer").description("Customer Details"),
                                fieldWithPath("payload.customer.firstName").description("Customer's first name"),
                                fieldWithPath("payload.customer.lastName").description("Customer's last name"),
                                fieldWithPath("payload.customer.tel").description("Customer's phone number"),
                                fieldWithPath("payload.customer.email").description("Customer's email address"),
                                fieldWithPath("payload.customer.zip").description("Customer's zip code"),
                                fieldWithPath("payload.customer.city").description("Customer's city"),
                                fieldWithPath("payload.customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("payload.customer.jumpers[].firstName").description("Jumper's first name"),
                                fieldWithPath("payload.customer.jumpers[].lastName").description("Jumper's last name"),
                                fieldWithPath("payload.customer.jumpers[].dateOfBirth").description("Jumper's date of birth"),
                                fieldWithPath("payload.customer.jumpers[].voucher").description("true if the jumper has a voucher"),
                                fieldWithPath("payload.date").description("Appointments date/time"),
                                fieldWithPath("payload.tandem").description("Tandem count"),
                                fieldWithPath("payload.picOrVid").description("picture or video count"),
                                fieldWithPath("payload.picAndVid").description("picture and video count"),
                                fieldWithPath("payload.handcam").description("handcam count"),
                                fieldWithPath("payload.note").description("An optional note for the appointment"),
                                fieldWithPath("payload.state").ignored(),
                                fieldWithPath("payload.createdOn").ignored(),
                                fieldWithPath("payload.createdBy").ignored(),
                                fieldWithPath("payload.clientId").ignored()
                        )));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        // Appointment was initially created in german
        assertTrue(argument.getValue().getSubject().startsWith("Deine Buchung wurde aktualisiert (#"));
    }

    @Test
    void testUpdateAdminAppointment() throws Exception {
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        int newCount = appointment.getTandem() + 1;

        appointment.setTandem(newCount);
        appointment.getCustomer().setJumpers(Collections.emptyList());

        String appointmentJson = json(appointment);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandem", is(newCount)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")))
                .andExpect(jsonPath("$.payload.customer.jumpers", hasSize(0)))
                .andDo(document("appointment/update-appointment",
                        requestFields(
                                fieldWithPath("appointmentId").description("The appointment's id"),
                                fieldWithPath("customer").description("Customer Details"),
                                fieldWithPath("customer.firstName").description("Customer's first name"),
                                fieldWithPath("customer.lastName").description("Customer's last name"),
                                fieldWithPath("customer.tel").description("Customer's phone number"),
                                fieldWithPath("customer.email").description("Customer's email address"),
                                fieldWithPath("customer.zip").description("Customer's zip code"),
                                fieldWithPath("customer.city").description("Customer's city"),
                                fieldWithPath("customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("date").description("Appointments date/time"),
                                fieldWithPath("tandem").description("Tandem count"),
                                fieldWithPath("picOrVid").description("picture or video count"),
                                fieldWithPath("picAndVid").description("picture and video count"),
                                fieldWithPath("handcam").description("handcam count"),
                                fieldWithPath("note").description("An optional note for the appointment"),
                                fieldWithPath("state").ignored(),
                                fieldWithPath("createdOn").ignored(),
                                fieldWithPath("createdBy").ignored(),
                                fieldWithPath("clientId").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload"),
                                fieldWithPath("payload.appointmentId").description("The appointment's id"),
                                fieldWithPath("payload.customer").description("Customer Details"),
                                fieldWithPath("payload.customer.firstName").description("Customer's first name"),
                                fieldWithPath("payload.customer.lastName").description("Customer's last name"),
                                fieldWithPath("payload.customer.tel").description("Customer's phone number"),
                                fieldWithPath("payload.customer.email").description("Customer's email address"),
                                fieldWithPath("payload.customer.zip").description("Customer's zip code"),
                                fieldWithPath("payload.customer.city").description("Customer's city"),
                                fieldWithPath("payload.customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("payload.date").description("Appointments date/time"),
                                fieldWithPath("payload.tandem").description("Tandem count"),
                                fieldWithPath("payload.picOrVid").description("picture or video count"),
                                fieldWithPath("payload.picAndVid").description("picture and video count"),
                                fieldWithPath("payload.handcam").description("handcam count"),
                                fieldWithPath("payload.note").description("An optional note for the appointment"),
                                fieldWithPath("payload.state").ignored(),
                                fieldWithPath("payload.createdOn").ignored(),
                                fieldWithPath("payload.createdBy").ignored(),
                                fieldWithPath("payload.clientId").ignored()
                        )));
    }

    @Test
    void testUpdateAdminAppointment_Error() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setAppointmentId(99999999);
        String appointmentJson = json(appointment);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/appointment/admin")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isInternalServerError())
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
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setDate(appointment.getDate().plusDays(10));

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "de-DE")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Sprungtag nicht gefunden")));
    }

    @Test
    void testUpdateAppointment_MorePicAndVidThanTandemSlots() throws Exception {
        GenericResult<Appointment> result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 0, 1, 0));
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getPayload().getAppointmentId()));

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
        GenericResult<Appointment> result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 1, 0, 0));
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getPayload().getAppointmentId()));

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
        GenericResult<Appointment> result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 0, 0, 1));
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getPayload().getAppointmentId()));

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
        GenericResult<Appointment> result = appointmentService.saveAppointment(ModelMockHelper.createAppointment(1, 0, 1, 0));
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointment(result.getPayload().getAppointmentId()));

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
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setTandem(10);
        appointment.getCustomer().setJumpers(ModelMockHelper.createJumpers(10));

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment")
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Jumpday has not enough free slots")));
    }

    @Test
    void testFindFreeSlots() throws Exception {
        SlotQuery query = new SlotQuery(2, 0, 0, 0);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/slots")
                .header("Accept-Language", "en-US")
                .param("tandem", String.valueOf(query.getTandem()))
                .param("picOrVid", String.valueOf(query.getPicOrVid()))
                .param("picAndVid", String.valueOf(query.getPicAndVid()))
                .param("handcam", String.valueOf(query.getHandcam())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload[0].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.payload[0].times[0]", is(LocalTime.of(11, 30).toString())))
                .andDo(document("appointment/find-slots",
                        requestParameters(
                                parameterWithName("tandem").description("number of tandem jumps"),
                                parameterWithName("picOrVid").description("picOrVid count"),
                                parameterWithName("picAndVid").description("picAndVid count"),
                                parameterWithName("handcam").description("handcam count")
                        ),
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("exception").description("exception message"),
                                fieldWithPath("message").description("optional return code message"),
                                fieldWithPath("payload[]").description("an array of date/time Objects"),
                                fieldWithPath("payload[].date").description("date of free slots"),
                                fieldWithPath("payload[].times[]").description("time values of free slots")
                        )
                ));
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
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);
        Appointment savedAppointment = result.getPayload();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/{appointmentId}/confirm/{token}",
                appointment.getAppointmentId(), savedAppointment.getVerificationToken().getToken())
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("appointment/confirm-appointment",
                        pathParameters(
                                parameterWithName("appointmentId").description("The id of the appointment"),
                                parameterWithName("token").description("The verification token for this appointment")
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload")
                        )));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        // Appointment was initially created in german
        assertEquals("Buchungsbestätigung #" + savedAppointment.getAppointmentId(), argument.getValue().getSubject());
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
        appointmentService.saveAppointment(appointment);

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
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);

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
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/date/{date}",
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("appointment/get-appointments-by-day",
                        pathParameters(
                                parameterWithName("date").description("The date for which appointments to get")
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload"),
                                fieldWithPath("payload[].appointmentId").description("The created appointment's id"),
                                fieldWithPath("payload[].customer").description("Customer Details"),
                                fieldWithPath("payload[].customer.firstName").description("Customer's first name"),
                                fieldWithPath("payload[].customer.lastName").description("Customer's last name"),
                                fieldWithPath("payload[].customer.tel").description("Customer's phone number"),
                                fieldWithPath("payload[].customer.email").description("Customer's email address"),
                                fieldWithPath("payload[].customer.zip").description("Customer's zip code"),
                                fieldWithPath("payload[].customer.city").description("Customer's city"),
                                fieldWithPath("payload[].customer.jumpers[]").description("Jumpers for this appointment"),
                                fieldWithPath("payload[].customer.jumpers[].firstName").description("Jumper's first name"),
                                fieldWithPath("payload[].customer.jumpers[].lastName").description("Jumper's last name"),
                                fieldWithPath("payload[].customer.jumpers[].dateOfBirth").description("Jumper's date of birth"),
                                fieldWithPath("payload[].customer.jumpers[].voucher").description("true if the jumper has a voucher"),
                                fieldWithPath("payload[].date").description("Appointments date/time"),
                                fieldWithPath("payload[].tandem").description("Tandem count"),
                                fieldWithPath("payload[].picOrVid").description("picture or video count"),
                                fieldWithPath("payload[].picAndVid").description("picture and video count"),
                                fieldWithPath("payload[].handcam").description("handcam count"),
                                fieldWithPath("payload[].note").description("An optional note for the appointment"),
                                fieldWithPath("payload[].state").ignored(),
                                fieldWithPath("payload[].createdOn").ignored(),
                                fieldWithPath("payload[].createdBy").ignored(),
                                fieldWithPath("payload[].clientId").ignored()
                        )));
    }

    @Test
    void testGetAppointmentsByDay_Unauthorized() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/date/{date}",
                appointment.getDate())
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateAppointmentState() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);
        Appointment savedAppointment = result.getPayload();

        AppointmentStateOnlyDTO appointmentStateOnly = new AppointmentStateOnlyDTO();
        appointmentStateOnly.setState(AppointmentState.ACTIVE);
        String appointmentStateOnlyJson = json(appointmentStateOnly);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/appointment/{appointmentId}",
                savedAppointment.getAppointmentId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentStateOnlyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("appointment/update-appointment-state",
                        pathParameters(
                                parameterWithName("appointmentId").description("The id of the appointment")
                        ),
                        requestFields(
                                fieldWithPath("state").description("The new state for this appointment")
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload")
                        )));

        assertEquals(AppointmentState.ACTIVE, appointmentService.findAppointment(savedAppointment.getAppointmentId()).getState());
    }

    @Test
    void testUpdateAppointmentState_Unauthorized() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);
        Appointment savedAppointment = result.getPayload();

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
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);
        Appointment savedAppointment = result.getPayload();

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
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);
        int appointmentId = result.getPayload().getAppointmentId();


        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/appointment/{appointmentId}", appointmentId)
                .header("Accept-Language", "en-US")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("appointment/delete-appointment",
                        pathParameters(
                                parameterWithName("appointmentId").description("The id of the appointment")
                        ),
                        responseFields(
                                fieldWithPath("success").description("true, if the request was successful"),
                                fieldWithPath("message").description("Message in case of error"),
                                fieldWithPath("exception").description("Exception if any"),
                                fieldWithPath("payload").description("The request's actual payload")
                        )));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        // Appointment was initially created in german
        assertTrue(argument.getValue().getSubject().startsWith("Dein Termin wurde gelöscht (#"));
    }

    @Test
    void testDeleteAppointment_Unauthorized() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        GenericResult<Appointment> result = appointmentService.saveAppointment(appointment);
        int appointmentId = result.getPayload().getAppointmentId();


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

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/groupslots")
                .header("Authorization", MockJwtDecoder.addHeader(READ_APPOINTMENTS))
                .param("tandem", String.valueOf(query.getTandem())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].date", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.payload[0].slots", hasSize(2)))
                .andDo(document("appointment/group-slots",
                        requestParameters(
                                parameterWithName("tandem").description("number of tandem jumps")
                        ),
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("exception").description("exception message"),
                                fieldWithPath("message").description("optional return code message"),
                                fieldWithPath("payload[]").description("an array of group slots"),
                                fieldWithPath("payload[].date").description("date of this group slot"),
                                fieldWithPath("payload[].firstTime").description("first slot time of this group slot"),
                                fieldWithPath("payload[].lastTime").description("last slot time of this group slot"),
                                fieldWithPath("payload[].timeCount").description("number of slots for this group slot"),
                                fieldWithPath("payload[].tandemAvailable").description("available tandems for this slot"),
                                fieldWithPath("payload[].picOrVidAvailable").description("available picOrVid for this slot"),
                                fieldWithPath("payload[].picAndVidAvailable").description("available picAndVid for this slot"),
                                fieldWithPath("payload[].handcamAvailable").description("available handcam for this slot"),
                                fieldWithPath("payload[].slots[]").description("array of slots for this group slot"),
                                fieldWithPath("payload[].slots[].time").description("time of this slot"),
                                fieldWithPath("payload[].slots[].tandemAvailable").description("available tandems for this slot"),
                                fieldWithPath("payload[].slots[].picOrVidAvailable").description("available picOrVid for this slot"),
                                fieldWithPath("payload[].slots[].picAndVidAvailable").description("available picAndVid for this slot"),
                                fieldWithPath("payload[].slots[].handcamAvailable").description("available handcam for this slot")
                        )
                ));
    }

    @Test
    void testFindGroupSlots_Unauthorized() throws Exception {
        SlotQuery query = new SlotQuery(5, 0, 0, 0);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/appointment/groupslots")
                .param("tandem", String.valueOf(query.getTandem())))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @TestConfiguration
    static class CustomizationConfiguration implements RestDocsMockMvcConfigurationCustomizer {
        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint());
        }

        @Bean
        public RestDocumentationResultHandler restDocumentation() {
            return MockMvcRestDocumentation.document("{method-name}");
        }
    }

}
