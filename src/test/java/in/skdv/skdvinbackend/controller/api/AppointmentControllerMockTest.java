package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SlotQuery;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static in.skdv.skdvinbackend.config.Authorities.UPDATE_APPOINTMENTS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppointmentControllerMockTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @MockBean
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
    }

    @Test
    public void testAddAppointment_InternalError() throws Exception {
        Mockito.when(appointmentService.saveAppointment(Mockito.any(Appointment.class)))
                .thenReturn(new GenericResult<>(false, ErrorMessage.APPOINTMENT_SERVICE_ERROR_MSG));
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(post("/api/appointment?lang=en")
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Internal appointment error")));
    }

    @Test
    public void testUpdateAppointment_InternalError() throws Exception {
        Mockito.when(appointmentService.updateAppointment(Mockito.any(Appointment.class)))
                .thenReturn(new GenericResult<>(false, ErrorMessage.APPOINTMENT_SERVICE_ERROR_MSG));
        String appointmentJson = json(ModelMockHelper.createSingleAppointment());

        mockMvc.perform(put("/api/appointment?lang=de")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_APPOINTMENTS))
                .contentType(contentType)
                .content(appointmentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Interner Termin Fehler")));
    }

    @Test
    public void testFindFreeSlots_InternalError() throws Exception {
        Mockito.when(appointmentService.findFreeSlots(Mockito.any(SlotQuery.class)))
                .thenReturn(new GenericResult<>(false, ErrorMessage.APPOINTMENT_SERVICE_ERROR_MSG));

        SlotQuery query = new SlotQuery(2, 0, 0, 2);
        String queryJson = json(query);

        mockMvc.perform(get("/api/appointment/slots?lang=en")
                .contentType(contentType)
                .content(queryJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Internal appointment error")));
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
