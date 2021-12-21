package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.converter.SettingsConverter;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.model.entity.settings.WaiverSettings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class SettingsControllerTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    private SettingsConverter settingsConverter = new SettingsConverter();

    @Autowired
    private SettingsRepository settingsRepository;

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
                .apply(springSecurity())
                .build();

        settingsRepository.deleteAll();
    }


    @Test
    void testCreateSettings() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettingsDto());

        mockMvc.perform(post("/api/settings/")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_SETTINGS))
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(5)))
                .andExpect(jsonPath("$.payload.commonSettings.de.dropzone.name", is("Example DZ")));
    }

    @Test
    void testCreateSettings_Unauthorized() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(post("/api/settings/")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateSettings() throws Exception {
        Settings settings = settingsRepository.save(ModelMockHelper.createSettings());
        SettingsDTO settingsDTO = settingsConverter.convertToDto(settings);
        settings.getAdminSettings().setTandemCount(2);
        settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().setName("Renamed DZ");
        String settingsJson = json(settings);

        mockMvc.perform(put("/api/settings/{id}", settings.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_SETTINGS))
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(2)))
                .andExpect(jsonPath("$.payload.commonSettings.de.dropzone.name", is("Renamed DZ")));
    }

    @Test
    void testUpdateSettings_Unauthorized() throws Exception {
        Settings settings = settingsRepository.save(ModelMockHelper.createSettings());
        String settingsJson = json(settings);

        mockMvc.perform(put("/api/settings/{id}", settings.getId())
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateSettings_NotFound() throws Exception {
        Settings settings = settingsRepository.save(ModelMockHelper.createSettings());
        String settingsJson = json(settings);

        mockMvc.perform(put("/api/settings/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_SETTINGS))
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Settings not found")));
    }

    @Test
    void testGetSettings() throws Exception {
        settingsRepository.save(ModelMockHelper.createSettings());

        mockMvc.perform(get("/api/settings/")
                .header("Authorization", MockJwtDecoder.addHeader(READ_SETTINGS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(5)))
                .andExpect(jsonPath("$.payload.commonSettings.de.dropzone.name", is("Example DZ")));
    }

    @Test
    void testGetSettings_Unauthorized() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(get("/api/settings/")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCommonSettings_EN() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        CommonSettings commonSettingsEN = ModelMockHelper.createCommonSettings();
        commonSettingsEN.getDropzone().setName("Example DZ EN");
        settings.getCommonSettings().put(Locale.ENGLISH.getLanguage(), commonSettingsEN);
        settingsRepository.save(settings);

        mockMvc.perform(get("/api/settings/common/")
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.dropzone.name", is("Example DZ EN")));
    }

    @Test
    void testGetCommonSettings_DE() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        settingsRepository.save(settings);

        mockMvc.perform(get("/api/settings/common/")
                .header("Accept-Language", "de")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.dropzone.name", is("Example DZ")));
    }

    @Test
    void testGetWaiverSettings_EN() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        WaiverSettings waiverSettingsEN = ModelMockHelper.createWaiverSettings();
        waiverSettingsEN.setTandemwaiver("Please accept the terms and conditions.");
        settings.getWaiverSettings().put(Locale.ENGLISH.getLanguage(), waiverSettingsEN);
        settingsRepository.save(settings);

        mockMvc.perform(get("/api/settings/waiver")
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandemwaiver", is("Please accept the terms and conditions.")));
    }

    @Test
    void testGetWaiverSettings_DE() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        settingsRepository.save(settings);

        mockMvc.perform(get("/api/settings/waiver/")
                .header("Accept-Language", "de")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.tandemwaiver", is("Bitte akzeptieren Sie die Bedingungen.")));
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
