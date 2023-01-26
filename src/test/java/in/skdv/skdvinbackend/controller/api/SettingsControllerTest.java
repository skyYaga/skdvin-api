package in.skdv.skdvinbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.SettingsDTO;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.model.mapper.SettingsMapper;
import in.skdv.skdvinbackend.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest extends AbstractSkdvinTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SettingsMapper mapper;

    @Autowired
    private SettingsRepository settingsRepository;

    @BeforeEach
    void setup() {
        settingsRepository.deleteAll();
    }

    @Test
    void testCreateSettings() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettingsDto());

        mockMvc.perform(post("/api/settings")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_SETTINGS))
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(5)))
                .andExpect(jsonPath("$.payload.languageSettings.de.dropzone.name", is("Example DZ")))
                .andExpect(jsonPath("$.payload.commonSettings.picAndVidEnabled", is(true)));
    }

    @Test
    void testCreateSettings_Unauthorized() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(post("/api/settings")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateSettings() throws Exception {
        Settings settings = settingsRepository.save(ModelMockHelper.createSettings());
        SettingsDTO settingsDTO = mapper.toDto(settings);
        settings.getAdminSettings().setTandemCount(2);
        settings.getLanguageSettings().get(Locale.GERMAN.getLanguage()).getDropzone().setName("Renamed DZ");
        String settingsJson = json(settings);

        mockMvc.perform(put("/api/settings/{id}", settings.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_SETTINGS))
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(2)))
                .andExpect(jsonPath("$.payload.languageSettings.de.dropzone.name", is("Renamed DZ")));
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

        mockMvc.perform(get("/api/settings")
                .header("Authorization", MockJwtDecoder.addHeader(READ_SETTINGS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(5)))
                .andExpect(jsonPath("$.payload.languageSettings.de.dropzone.name", is("Example DZ")));
    }

    @Test
    void testGetSettings_Unauthorized() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(get("/api/settings")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetLanguageSettings_EN() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        LanguageSettings languageSettingsEN = ModelMockHelper.createLanguageSettings();
        languageSettingsEN.getDropzone().setName("Example DZ EN");
        settings.getLanguageSettings().put(Locale.ENGLISH.getLanguage(), languageSettingsEN);
        settingsRepository.save(settings);

        mockMvc.perform(get("/api/settings/common")
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.dropzone.name", is("Example DZ EN")))
                .andExpect(jsonPath("$.payload.picAndVidEnabled", is(true)));
    }

    @Test
    void testGetLanguageSettings_DE() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        settingsRepository.save(settings);

        mockMvc.perform(get("/api/settings/common")
                .header("Accept-Language", "de")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.dropzone.name", is("Example DZ")))
                .andExpect(jsonPath("$.payload.picAndVidEnabled", is(true)));
    }

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }
}
