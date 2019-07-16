package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.converter.AppointmentConverter;
import in.skdv.skdvinbackend.model.dto.AppointmentDTO;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppointmentControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                        .apply(springSecurity()).build();

        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());

        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
    }

    @Test
    public void testGetAllUnauthorized() throws Exception {
        mockMvc.perform(get("/api/appointment/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testGetOne() throws Exception {
        GenericResult<Appointment> appointmentGenericResult = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        Appointment appointment = appointmentGenericResult.getPayload();

        mockMvc.perform(get("/api/appointment/" + appointment.getAppointmentId()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId", is(appointment.getAppointmentId())))
                .andExpect(jsonPath("$.customer.firstName", is(appointment.getCustomer().getFirstName())));
    }

    @Test
    public void testGetOneUnauthorized() throws Exception {
        GenericResult<Appointment> appointmentGenericResult = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        Appointment appointment = appointmentGenericResult.getPayload();

        mockMvc.perform(get("/api/appointment/" + appointment.getAppointmentId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testAddAppointment() throws Exception {
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(post("/api/appointment/")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Max")));
    }

    @Test
    public void testAddAppointment_Unauthorized() throws Exception {
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(post("/api/appointment/")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testAddAppointment_NoSlotLeft() throws Exception {
        appointmentService.saveAppointment(ModelMockHelper.createAppointment(3, 0));

        String appointmentJson = json(ModelMockHelper.createAppointment(2, 0));

        mockMvc.perform(post("/api/appointment?lang=de")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Sprungtag hat nicht genügend freie Slots")));
    }

    @Test
    @WithMockUser
    public void testAddAppointment_NoJumpDay() throws Exception {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setDate(appointment.getDate().plusDays(10));
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment?lang=de")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Sprungtag nicht gefunden")));
    }

    @Test
    @WithMockUser
    public void testAddAppointment_MoreVideoThanTandemSlots() throws Exception {
        Appointment appointment = ModelMockHelper.createAppointment(1, 2);
        String appointmentJson = json(appointment);


        mockMvc.perform(post("/api/appointment?lang=en")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    @WithMockUser
    public void testUpdateAppointment() throws Exception {
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        int newCount = appointment.getTandem() + 1;

        appointment.setTandem(newCount);
        appointment.getCustomer().setFirstName("Unitjane");

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment/")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandem", is(newCount)))
                .andExpect(jsonPath("$.payload.customer.firstName", is("Unitjane")));
    }

    @Test
    public void testUpdateAppointmentUnauthorized() throws Exception {
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
    @WithMockUser
    public void testUpdateAppointment_NoJumpDay() throws Exception {
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setDate(appointment.getDate().plusDays(10));

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment?lang=de")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Sprungtag nicht gefunden")));
    }

    @Test
    @WithMockUser
    public void testUpdateAppointment_MoreVideoThanTandemSlots() throws Exception {
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setVideo(appointment.getTandem() + 1);

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment?lang=en")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("The appointment has more video than tandem slots")));
    }

    @Test
    @WithMockUser
    public void testUpdateAppointment_NoSlotLeft() throws Exception {
        AppointmentConverter appointmentConverter = new AppointmentConverter();
        AppointmentDTO appointment = appointmentConverter.convertToDto(appointmentService.findAppointmentsByDay(LocalDate.now()).get(0));

        appointment.setTandem(10);

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointment?lang=en")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Jumpday has not enough free slots")));
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
