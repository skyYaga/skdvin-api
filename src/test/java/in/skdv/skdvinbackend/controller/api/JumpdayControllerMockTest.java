package in.skdv.skdvinbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.JumpdayInternalException;
import in.skdv.skdvinbackend.service.IJumpdayService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static in.skdv.skdvinbackend.config.Authorities.READ_JUMPDAYS;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JumpdayControllerMockTest extends AbstractSkdvinTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IJumpdayService jumpdayService;

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
        return objectMapper.writeValueAsString(o);
    }

}
