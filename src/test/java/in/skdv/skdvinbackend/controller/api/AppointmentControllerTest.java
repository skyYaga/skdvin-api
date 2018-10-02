package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.AppointmentRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
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
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
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
    private AppointmentRepository appointmentRepository;

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

        appointmentRepository.deleteAll();

        appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
        appointmentService.saveAppointment(ModelMockHelper.createSecondAppointment());
    }

    @Test
    @WithMockUser
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/api/appointments/"))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customer.firstName", is("Max")))
                .andExpect(jsonPath("$[1].customer.firstName", is("Jane")));
    }

    @Test
    public void testGetAllUnauthorized() throws Exception {
        mockMvc.perform(get("/api/appointments/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testGetOne() throws Exception {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(get("/api/appointments/" + appointment.getAppointmentId()))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId", is(appointment.getAppointmentId())))
                .andExpect(jsonPath("$.customer.firstName", is(appointment.getCustomer().getFirstName())));
    }

    @Test
    public void testGetOneUnauthorized() throws Exception {
        Appointment appointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(get("/api/appointments/" + appointment.getAppointmentId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testCreateNewAppointment() throws Exception {
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(post("/api/appointments/")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.firstName", is("Max")));
    }

    @Test
    public void testCreateNewAppointmentUnauthorized() throws Exception {
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(post("/api/appointments/")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testUpdateAppointment() throws Exception {
        Appointment appointment = appointmentService.findAppointments().get(0);

        appointment.setTandem(10);
        appointment.getCustomer().setFirstName("Unitjane");

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointments/")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tandem", is(10)))
                .andExpect(jsonPath("$.customer.firstName", is("Unitjane")));
    }

    @Test
    public void testUpdateAppointmentUnauthorized() throws Exception {
        Appointment appointment = appointmentService.findAppointments().get(0);

        appointment.setTandem(10);
        appointment.getCustomer().setFirstName("Unitjane");

        String appointmentJson = json(appointment);

        mockMvc.perform(put("/api/appointments/")
                .contentType(contentType)
                .content(appointmentJson))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
