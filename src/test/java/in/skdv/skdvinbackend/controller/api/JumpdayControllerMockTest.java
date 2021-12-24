package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.JumpdayInternalException;
import in.skdv.skdvinbackend.service.IJumpdayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

import static in.skdv.skdvinbackend.config.Authorities.READ_JUMPDAYS;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class JumpdayControllerMockTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @MockBean
    private IJumpdayService jumpdayService;

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
    void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                        .apply(springSecurity()).build();
    }

    @Test
    void testFindJumpday_InternalError() throws Exception {
        Mockito.when(jumpdayService.findJumpday(Mockito.any(LocalDate.class)))
                .thenThrow(new JumpdayInternalException(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG));

        mockMvc.perform(get("/api/jumpday/{date}?lang=de", LocalDate.now().toString())
                .header("Authorization", MockJwtDecoder.addHeader(READ_JUMPDAYS)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Interner Sprungtag Fehler")));
    }

    @Test
    void testFindJumpdays_InternalError() throws Exception {
        Mockito.when(jumpdayService.findJumpdays())
                .thenThrow(new JumpdayInternalException(ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG));

        mockMvc.perform(get("/api/jumpday")
                .header("Authorization", MockJwtDecoder.addHeader(READ_JUMPDAYS))
                .header("Accept-Language", "en-US"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Internal Jumpday error")));
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
