package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
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
import java.util.Locale;
import java.util.Map;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class VideoflyerControllerTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private VideoflyerConverter converter = new VideoflyerConverter();

    private MockMvc mockMvc;

    @MockBean
    ISettingsService settingsService;

    @Autowired
    private VideoflyerRepository videoflyerRepository;

    @Autowired
    private IVideoflyerService videoflyerService;

    @Autowired
    private IJumpdayService jumpdayService;

    @Autowired
    private JumpdayRepository jumpdayRepository;

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

        videoflyerRepository.deleteAll();
        jumpdayRepository.deleteAll();

        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(new CommonSettings());
    }

    @Test
    void testCreateVideoflyer() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyerDto());

        mockMvc.perform(post("/api/videoflyer/")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.firstName", is("Max")))
                .andExpect(jsonPath("$.payload.lastName", is("Mustermann")));
    }

    @Test
    void testCreateVideoflyer_Unauthorized() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyer());

        mockMvc.perform(post("/api/videoflyer/")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllVideoflyers() throws Exception {
        videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyerRepository.save(ModelMockHelper.createVideoflyer("john", "doe"));

        mockMvc.perform(get("/api/videoflyer")
                .header("Authorization", MockJwtDecoder.addHeader(READ_VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)));
    }

    @Test
    void testGetAllVideoflyers_Unauthorized() throws Exception {
        videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyerRepository.save(ModelMockHelper.createVideoflyer("john", "doe"));

        mockMvc.perform(get("/api/videoflyer")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyer.setEmail("foo@example.com");
        videoflyer.setPicAndVid(true);

        String videoflyerJson = json(converter.convertToDto(videoflyer));

        mockMvc.perform(put("/api/videoflyer/{id}", videoflyer.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.email", is("foo@example.com")))
                .andExpect(jsonPath("$.payload.picAndVid", is(true)));
    }

    @Test
    void testUpdateVideoflyer_Unauthorized() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyer.setEmail("foo@example.com");
        videoflyer.setPicAndVid(true);

        String videoflyerJson = json(videoflyer);

        mockMvc.perform(put("/api/videoflyer/{id}", videoflyer.getId())
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateVideoflyer_NotFound() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        String videoflyerJson = json(videoflyer);

        mockMvc.perform(put("/api/videoflyer/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Videoflyer not found")));
    }


    @Test
    void testDeleteVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        mockMvc.perform(delete("/api/videoflyer/{id}", videoflyer.getId())
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testDeleteVideoflyer_Unauthorized() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        mockMvc.perform(delete("/api/videoflyer/{id}", videoflyer.getId())
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteVideoflyer_NotFound() throws Exception {

        mockMvc.perform(delete("/api/videoflyer/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Videoflyer not found")));
    }


    @Test
    void testGetVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyer.getId(), new SimpleAssignment(true));

        mockMvc.perform(get("/api/videoflyer/{id}", videoflyer.getId())
                .header("Authorization", MockJwtDecoder.addHeader(READ_VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)));
    }

    @Test
    void testGetVideoflyer_NotFound() throws Exception {
        mockMvc.perform(get("/api/videoflyer/{id}", "999999999")
                .header("Authorization", MockJwtDecoder.addHeader(READ_VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Videoflyer not found")));
    }

    @Test
    void testGetVideoflyer_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/videoflyer/{id}", "999999999")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAssignVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/{id}/assign", videoflyerDetailsDTO.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testAssignVideoflyer_Unauthorized() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyer());

        mockMvc.perform(patch("/api/videoflyer/{id}/assign", "99999999")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAssignVideoflyer_NotExisting() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/{id}/assign", "99999999")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void testAssignVideoflyer_NotFound() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/{id}/assign", videoflyerDetailsDTO.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Jumpday not found")));
    }

    @Test
    void testSelfAssignVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyer.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testSelfAssignVideoflyer_READONLY() throws Exception {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.READONLY);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyer.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/me/assign")
                .header("Accept-Language", "en-EN")
                .header("Authorization", MockJwtDecoder.addHeader(VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Selfassignment is in read-only mode")));
    }

    @Test
    void testSelfAssignVideoflyer_Unauthorized() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyer());

        mockMvc.perform(patch("/api/videoflyer/me/assign")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSelfAssignVideoflyer_NoEmailSet() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }


    @Test
    void testGetMeVideoflyer() throws Exception {
        Videoflyer videoflyer1 = ModelMockHelper.createVideoflyer();
        videoflyer1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Videoflyer videoflyer = videoflyerRepository.save(videoflyer1);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyer.getId(), new SimpleAssignment(true));

        mockMvc.perform(get("/api/videoflyer/me")
                .header("Authorization", MockJwtDecoder.addHeader(VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)));
    }

    @Test
    void testGetMeVideoflyer_NotFound() throws Exception {
        mockMvc.perform(get("/api/videoflyer/me")
                .header("Authorization", MockJwtDecoder.addHeader(VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Videoflyer not found")));
    }

    @Test
    void testGetMeVideoflyer_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/videoflyer/me")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
