package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringRunner;
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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TandemmasterControllerTest extends AbstractSkdvinTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private TandemmasterConverter converter = new TandemmasterConverter();

    private MockMvc mockMvc;

    @MockBean
    ISettingsService settingsService;

    @Autowired
    private TandemmasterRepository tandemmasterRepository;

    @Autowired
    private ITandemmasterService tandemmasterService;

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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        tandemmasterRepository.deleteAll();
        jumpdayRepository.deleteAll();

        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(new CommonSettings());
    }

    @Test
    public void testCreateTandemmaster() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/tandemmaster/")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.firstName", is("Max")))
                .andExpect(jsonPath("$.payload.lastName", is("Mustermann")))
                .andDo(document("tandemmaster/create-tandemmaster", requestFields(
                        fieldWithPath("firstName").description("Tandemmasters first name"),
                        fieldWithPath("lastName").description("Tandemmasters last name"),
                        fieldWithPath("email").description("Tandemmasters email"),
                        fieldWithPath("tel").description("Tandemmasters phone number"),
                        fieldWithPath("handcam").description("true if the Tandemmaster makes handcam videos")
                ), responseFields(
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("payload.id").description("Tandemmasters id"),
                        fieldWithPath("payload.firstName").description("Tandemmasters first name"),
                        fieldWithPath("payload.lastName").description("Tandemmasters last name"),
                        fieldWithPath("payload.email").description("Tandemmasters email"),
                        fieldWithPath("payload.tel").description("Tandemmasters phone number"),
                        fieldWithPath("payload.handcam").description("true if the Tandemmaster makes handcam videos"),
                        fieldWithPath("exception").ignored()
                )));
    }

    @Test
    public void testCreateTandemmaster_Unauthorized() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(post("/api/tandemmaster/")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllTandemmasters() throws Exception {
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster("john", "doe"));

        mockMvc.perform(get("/api/tandemmaster")
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("tandemmaster/get-tandemmasters",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload[].id").description("Tandemmasters id"),
                                fieldWithPath("payload[].firstName").description("Tandemmasters first name"),
                                fieldWithPath("payload[].lastName").description("Tandemmasters last name"),
                                fieldWithPath("payload[].email").description("Tandemmasters email"),
                                fieldWithPath("payload[].tel").description("Tandemmasters phone number"),
                                fieldWithPath("payload[].handcam").description("true if the Tandemmaster makes handcam videos"),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetAllTandemmasters_Unauthorized() throws Exception {
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster("john", "doe"));

        mockMvc.perform(get("/api/tandemmaster")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail("foo@example.com");
        tandemmaster.setHandcam(true);

        String tandemmasterJson = json(converter.convertToDto(tandemmaster));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.email", is("foo@example.com")))
                .andExpect(jsonPath("$.payload.handcam", is(true)))
                .andDo(document("tandemmaster/update-tandemmaster",
                        pathParameters(
                                parameterWithName("id").description("Tandemmasters id")
                        ),
                        requestFields(
                                fieldWithPath("id").description("Tandemmasters id"),
                                fieldWithPath("firstName").description("Tandemmasters first name"),
                                fieldWithPath("lastName").description("Tandemmasters last name"),
                                fieldWithPath("email").description("Tandemmasters email"),
                                fieldWithPath("tel").description("Tandemmasters phone number"),
                                fieldWithPath("handcam").description("true if the Tandemmaster makes handcam videos")
                        ), responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload.id").description("Tandemmasters id"),
                                fieldWithPath("payload.firstName").description("Tandemmasters first name"),
                                fieldWithPath("payload.lastName").description("Tandemmasters last name"),
                                fieldWithPath("payload.email").description("Tandemmasters email"),
                                fieldWithPath("payload.tel").description("Tandemmasters phone number"),
                                fieldWithPath("payload.handcam").description("true if the Tandemmaster makes handcam videos"),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testUpdateTandemmaster_Unauthorized() throws Exception {
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
    public void testUpdateTandemmaster_NotFound() throws Exception {
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
    public void testDeleteTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("tandemmaster/delete-tandemmaster",
                        pathParameters(
                                parameterWithName("id").description("Tandemmasters id")
                        ), responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("exception").ignored(),
                                fieldWithPath("payload").ignored()
                        )));
    }

    @Test
    public void testDeleteTandemmaster_Unauthorized() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());

        mockMvc.perform(delete("/api/tandemmaster/{id}", tandemmaster.getId())
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteTandemmaster_NotFound() throws Exception {

        mockMvc.perform(delete("/api/tandemmaster/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }

    @Test
    public void testGetTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        tandemmasterService.assignTandemmasterToJumpday(jumpday.getDate(), tandemmaster.getId(), new SimpleAssignment(true));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)))
                .andDo(document("tandemmaster/get-tandemmaster",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload.id").description("Tandemmasters id"),
                                fieldWithPath("payload.firstName").description("Tandemmasters first name"),
                                fieldWithPath("payload.lastName").description("Tandemmasters last name"),
                                fieldWithPath("payload.email").description("Tandemmasters email"),
                                fieldWithPath("payload.tel").description("Tandemmasters phone number"),
                                fieldWithPath("payload.handcam").description("true if the Tandemmaster makes handcam videos"),
                                fieldWithPath("payload.assignments").description("key value pairs of date and the tandemmasters assignment state as boolean"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".assigned").description("true if the flyer is assigned"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".allday").description("true if the flyer is assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".from").description("from time if the flyer is not assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".to").description("to time if the flyer is not assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.firstName").description("Tandemmaster's first name"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.lastName").description("Tandemmaster's last name"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.email").description("Tandemmaster's email"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.tel").description("Tandemmaster's phone number"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.handcam").description("Tandemmaster's handcam"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.id").description("Tandemmaster's id"),
                                fieldWithPath("payload.assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetTandemmaster_NotFound() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/{id}", "999999999")
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }

    @Test
    public void testGetTandemmaster_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/{id}", "999999999")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAssignTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/tandemmaster/{id}/assign", tandemmasterDetailsDTO.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("tandemmaster/assign-tandemmaster",
                        pathParameters(
                                parameterWithName("id").description("Tandemmasters id")
                        ),
                        requestFields(
                                fieldWithPath("id").description("Tandemmasters id"),
                                fieldWithPath("assignments").description("key value pairs of date and the tandemmasters assignment state as boolean"),
                                fieldWithPath("assignments." + LocalDate.now() + ".assigned").description("true if the flyer is assigned"),
                                fieldWithPath("assignments." + LocalDate.now() + ".allday").description("true if the flyer is assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now() + ".from").description("from time if the flyer is not assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now() + ".to").description("to time if the flyer is not assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("firstName").ignored(),
                                fieldWithPath("lastName").ignored(),
                                fieldWithPath("email").ignored(),
                                fieldWithPath("tel").ignored(),
                                fieldWithPath("handcam").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("exception").ignored(),
                                fieldWithPath("payload").ignored()
                        )));
    }

    @Test
    public void testAssignTandemmaster_Unauthorized() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(patch("/api/tandemmaster/{id}/assign", "99999999")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAssignTandemmaster_BadRequest() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/{id}/assign", "99999999")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testAssignTandemmaster_NotFound() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

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
    public void testSelfAssignTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/tandemmaster/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("tandemmaster/self-assign-tandemmaster",
                        requestFields(
                                fieldWithPath("id").description("Tandemmasters id"),
                                fieldWithPath("email").description("Tandemmasters email"),
                                fieldWithPath("assignments").description("key value pairs of date and the tandemmasters assignment state as boolean"),
                                fieldWithPath("assignments." + LocalDate.now() + ".assigned").description("true if the flyer is assigned"),
                                fieldWithPath("assignments." + LocalDate.now() + ".allday").description("true if the flyer is assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now() + ".from").description("from time if the flyer is not assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now() + ".to").description("to time if the flyer is not assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("firstName").ignored(),
                                fieldWithPath("lastName").ignored(),
                                fieldWithPath("tel").ignored(),
                                fieldWithPath("handcam").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("exception").ignored(),
                                fieldWithPath("payload").ignored()
                        )));
    }

    @Test
    public void testSelfAssignTandemmaster_READONLY() throws Exception {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setSelfAssignmentMode(SelfAssignmentMode.READONLY);
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).thenReturn(commonSettings);

        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);

        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/tandemmaster/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Eigenzuordnung ist im read-only Modus")));
    }

    @Test
    public void testSelfAssignTandemmaster_Unauthorized() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(patch("/api/tandemmaster/me/assign")
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testSelfAssignTandemmaster_NoEmailSet() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String tandemmasterJson = json(tandemmasterDetailsDTO);

        mockMvc.perform(patch("/api/tandemmaster/me/assign")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testGetMeTandemmaster() throws Exception {
        Tandemmaster tandemmaster1 = ModelMockHelper.createTandemmaster();
        tandemmaster1.setEmail(MockJwtDecoder.EXAMPLE_EMAIL);
        Tandemmaster tandemmaster = tandemmasterRepository.save(tandemmaster1);
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        tandemmasterService.assignTandemmasterToJumpday(jumpday.getDate(), tandemmaster.getId(), new SimpleAssignment(true));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/tandemmaster/me")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)))
                .andDo(document("tandemmaster/get-me-tandemmaster",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload.id").description("Tandemmasters id"),
                                fieldWithPath("payload.firstName").description("Tandemmasters first name"),
                                fieldWithPath("payload.lastName").description("Tandemmasters last name"),
                                fieldWithPath("payload.email").description("Tandemmasters email"),
                                fieldWithPath("payload.tel").description("Tandemmasters phone number"),
                                fieldWithPath("payload.handcam").description("true if the Tandemmaster makes handcam videos"),
                                fieldWithPath("payload.assignments").description("key value pairs of date and the tandemmasters assignment state as boolean"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".assigned").description("true if the flyer is assigned"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".allday").description("true if the flyer is assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".from").description("from time if the flyer is not assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".to").description("to time if the flyer is not assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.firstName").description("Tandemmaster's first name"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.lastName").description("Tandemmaster's last name"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.email").description("Tandemmaster's email"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.tel").description("Tandemmaster's phone number"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.handcam").description("Tandemmaster's handcam"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.id").description("Tandemmaster's id"),
                                fieldWithPath("payload.assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetMeTandemmaster_NotFound() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/me")
                .header("Authorization", MockJwtDecoder.addHeader(TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
    }

    @Test
    public void testGetMeTandemmaster_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/tandemmaster/me")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @TestConfiguration
    static class CustomizationConfiguration implements RestDocsMockMvcConfigurationCustomizer {
        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint());
        }

        @Bean
        public RestDocumentationResultHandler restDocumentation() {
            return MockMvcRestDocumentation.document("{method-name}");
        }
    }
}
