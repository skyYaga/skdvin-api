package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.entity.settings.WaiverSettings;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import in.skdv.skdvinbackend.repository.WaiverRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IWaiverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static in.skdv.skdvinbackend.config.Authorities.READ_WAIVERS;
import static in.skdv.skdvinbackend.config.Authorities.UPDATE_WAIVERS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class WaiverControllerTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;
    private WaiverConverter converter = new WaiverConverter();

    @MockBean
    private ISettingsService settingsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IWaiverService waiverService;

    @Autowired
    private WaiverRepository waiverRepository;

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
                .apply(springSecurity())
                .build();

        WaiverSettings waiverSettings = new WaiverSettings();
        waiverSettings.setTandemwaiver("Tandem Waiver Text");
        when(settingsService.getWaiverSettingsByLanguage(anyString())).
                thenReturn(waiverSettings);

        waiverRepository.deleteAll();
    }

    @Test
    void testGetAllWaivers() throws Exception {
        waiverService.saveWaiver(ModelMockHelper.createWaiver());
        waiverService.saveWaiver(ModelMockHelper.createWaiver());

        mockMvc.perform(get("/api/waivers")
                .header("Authorization", MockJwtDecoder.addHeader(READ_WAIVERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)));
    }

    @Test
    void testGetAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/waivers")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSaveWaiver() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());

        String userJson = json(waiver1);

        mockMvc.perform(post("/api/waivers")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isCreated());
    }


    @Test
    void testSaveWaiver_MinorMissingSignatures_DE() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());
        waiver1.getWaiverCustomer().setDateOfBirth(LocalDate.now().minusYears(16));

        String userJson = json(waiver1);

        mockMvc.perform(post("/api/waivers")
                .header("Accept-Language", "de")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Minderjährige benötigen die Unterschriften beider Erziehungsberechtigten")));
    }

    @Test
    void testSaveWaiver_MinorMissingSignatures_EN() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());
        waiver1.getWaiverCustomer().setDateOfBirth(LocalDate.now().minusYears(16));

        String userJson = json(waiver1);

        mockMvc.perform(post("/api/waivers")
                .header("Accept-Language", "en")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Minors need the signatures of both guardians")));
    }

    @Test
    void testUpdateWaiver() throws Exception {
        Waiver result = waiverService.saveWaiver(ModelMockHelper.createWaiver());

        String userJson = json(result);

        mockMvc.perform(put("/api/waivers/{id}", result.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_WAIVERS))
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateWaiver_NotExisting_DE() throws Exception {
        WaiverDTO waiver = converter.convertToDto(ModelMockHelper.createWaiver());
        waiver.setTandemmaster("12345");
        waiver.setId("12345");

        String userJson = json(waiver);

        mockMvc.perform(put("/api/waivers/{id}", waiver.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_WAIVERS))
                .header("Accept-Language", "de")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Haftungsvereinbarung existiert nicht")));
    }

    @Test
    void testUpdateWaiver_NotExisting_EN() throws Exception {
        WaiverDTO waiver = converter.convertToDto(ModelMockHelper.createWaiver());
        waiver.setTandemmaster("12345");
        waiver.setId("12345");

        String userJson = json(waiver);

        mockMvc.perform(put("/api/waivers/{id}", waiver.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_WAIVERS))
                .header("Accept-Language", "en")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Waiver does not exist")));
    }

    @Test
    void testUpdateWaiver_Unauthorized() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());

        String userJson = json(waiver1);

        mockMvc.perform(put("/api/waivers")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isUnauthorized());
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
