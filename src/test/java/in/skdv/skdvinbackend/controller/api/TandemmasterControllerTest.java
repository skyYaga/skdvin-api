package in.skdv.skdvinbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.model.mapper.TandemmasterMapper;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TandemmasterControllerTest extends AbstractSkdvinTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ISettingsService settingsService;

    @Autowired
    private TandemmasterMapper mapper;

    @Autowired
    private TandemmasterRepository tandemmasterRepository;

    @Autowired
    private ITandemmasterService tandemmasterService;

    @Autowired
    private IJumpdayService jumpdayService;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @BeforeEach
    void setup() {
        tandemmasterRepository.deleteAll();
        jumpdayRepository.deleteAll();

        Settings settings = ModelMockHelper.createSettings(SelfAssignmentMode.WRITE_DELETE);
        when(settingsService.getSettings()).thenReturn(settings);
    }

    @Test
    void testCreateTandemmaster() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmasterDto());

        mockMvc.perform(post("/api/tandemmaster")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.firstName", is("Max")))
                .andExpect(jsonPath("$.payload.lastName", is("Mustermann")))
                .andExpect(jsonPath("$.payload.favorite", is(false)));
    }

    @Test
    void testCreateTandemmaster_Unauthorized() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(post("/api/tandemmaster")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllTandemmasters() throws Exception {
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster("john", "doe"));

        mockMvc.perform(get("/api/tandemmaster")
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)));
    }

    @Test
    void testGetAllTandemmasters_Unauthorized() throws Exception {
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster("john", "doe"));

        mockMvc.perform(get("/api/tandemmaster")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail("foo@example.com");
        tandemmaster.setHandcam(true);

        String tandemmasterJson = json(mapper.toDto(tandemmaster));

        mockMvc.perform(put("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.email", is("foo@example.com")))
                .andExpect(jsonPath("$.payload.handcam", is(true)));
    }

    @Test
    void testUpdateTandemmaster_Unauthorized() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail("foo@example.com");
        tandemmaster.setHandcam(true);

        String tandemmasterJson = json(tandemmaster);

        mockMvc.perform(put("/api/tandemmaster/{id}", tandemmaster.getId())
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateTandemmaster_NotFound() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        String tandemmasterJson = json(tandemmaster);

        mockMvc.perform(put("/api/tandemmaster/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }


    @Test
    void testDeleteTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        mockMvc.perform(delete("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testDeleteTandemmaster_Unauthorized() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        mockMvc.perform(delete("/api/tandemmaster/{id}", tandemmaster.getId())
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteTandemmaster_NotFound() throws Exception {

        mockMvc.perform(delete("/api/tandemmaster/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }

    @Test
    void testGetTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, jumpday.getDate());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        mockMvc.perform(get("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)));
    }

    @Test
    void testGetTandemmaster_NotFound() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/{id}", "999999999")
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }

    @Test
    void testGetTandemmaster_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/{id}", "999999999")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAssignTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        SimpleAssignment simpleAssignment = new SimpleAssignment(true);
        simpleAssignment.setNote("Example note");
        TandemmasterDetailsDTO tandemmasterDetailsDTO = mapper.toDetailsDto(tandemmaster, Map.of(LocalDate.now(), simpleAssignment));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/{id}/assign", tandemmasterDetailsDTO.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testAssignTandemmaster_Unauthorized() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(patch("/api/tandemmaster/{id}/assign", "99999999")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAssignTandemmaster_NotExisting() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        TandemmasterDetailsDTO tandemmasterDetailsDTO = mapper.toDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/{id}/assign", "99999999")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void testAssignTandemmaster_NotFound() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterDetailsDTO tandemmasterDetailsDTO = mapper.toDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/{id}/assign", tandemmasterDetailsDTO.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Jumpday not found")));
    }

    @Test
    void testSelfAssignTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        TandemmasterDetailsDTO tandemmasterDetailsDTO = mapper.toDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void testSelfAssignTandemmaster_READONLY() throws Exception {
        Settings settings = ModelMockHelper.createSettings(SelfAssignmentMode.READONLY);
        when(settingsService.getSettings()).thenReturn(settings);

        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        TandemmasterDetailsDTO tandemmasterDetailsDTO = mapper.toDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Eigenzuordnung ist im read-only Modus")));
    }

    @Test
    void testSelfAssignTandemmaster_Unauthorized() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(patch("/api/tandemmaster/me/assign")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testSelfAssignTandemmaster_NoEmailSet() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        TandemmasterDetailsDTO tandemmasterDetailsDTO = mapper.toDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void testGetMeTandemmaster() throws Exception {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        tandemmaster1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Tandemmaster tandemmaster = tandemmasterRepository.save(tandemmaster1);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        TandemmasterDetails tandemmasterDetails = ModelMockHelper.addTandemmasterAssignment(tandemmaster, jumpday.getDate());
        tandemmasterService.assignTandemmaster(tandemmasterDetails, false);

        mockMvc.perform(get("/api/tandemmaster/me")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)));
    }

    @Test
    void testGetMeTandemmaster_NotFound() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/me")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }

    @Test
    void testGetMeTandemmaster_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/me")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

}
