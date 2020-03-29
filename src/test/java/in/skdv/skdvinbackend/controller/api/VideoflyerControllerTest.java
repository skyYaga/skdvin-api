package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
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
import java.util.Map;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
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
public class VideoflyerControllerTest extends AbstractSkdvinTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private VideoflyerConverter converter = new VideoflyerConverter();

    private MockMvc mockMvc;

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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        videoflyerRepository.deleteAll();
        jumpdayRepository.deleteAll();
    }

    @Test
    public void testCreateVideoflyer() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyer());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/videoflyer/")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.firstName", is("Max")))
                .andExpect(jsonPath("$.payload.lastName", is("Mustermann")))
                .andDo(document("videoflyer/create-videoflyer", requestFields(
                        fieldWithPath("firstName").description("Videoflyers first name"),
                        fieldWithPath("lastName").description("Videoflyers last name"),
                        fieldWithPath("email").description("Videoflyers email"),
                        fieldWithPath("tel").description("Videoflyers phone number"),
                        fieldWithPath("picAndVid").description("true if the Videoflyer makes pictures and videos")
                ), responseFields(
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("payload.id").description("Videoflyers id"),
                        fieldWithPath("payload.firstName").description("Videoflyers first name"),
                        fieldWithPath("payload.lastName").description("Videoflyers last name"),
                        fieldWithPath("payload.email").description("Videoflyers email"),
                        fieldWithPath("payload.tel").description("Videoflyers phone number"),
                        fieldWithPath("payload.picAndVid").description("true if the Videoflyer makes pictures and videos"),
                        fieldWithPath("exception").ignored()
                )));
    }

    @Test
    public void testCreateVideoflyer_Unauthorized() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyer());

        mockMvc.perform(post("/api/videoflyer/")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAllVideoflyers() throws Exception {
        videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyerRepository.save(ModelMockHelper.createVideoflyer("john", "doe"));

        mockMvc.perform(get("/api/videoflyer")
                .header("Authorization", MockJwtDecoder.addHeader(READ_VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("videoflyer/get-videoflyers",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload[].id").description("Videoflyers id"),
                                fieldWithPath("payload[].firstName").description("Videoflyers first name"),
                                fieldWithPath("payload[].lastName").description("Videoflyers last name"),
                                fieldWithPath("payload[].email").description("Videoflyers email"),
                                fieldWithPath("payload[].tel").description("Videoflyers phone number"),
                                fieldWithPath("payload[].picAndVid").description("true if the Videoflyer makes pictures and videos"),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetAllVideoflyers_Unauthorized() throws Exception {
        videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyerRepository.save(ModelMockHelper.createVideoflyer("john", "doe"));

        mockMvc.perform(get("/api/videoflyer")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        videoflyer.setEmail("foo@example.com");
        videoflyer.setPicAndVid(true);

        String videoflyerJson = json(converter.convertToDto(videoflyer));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/videoflyer/{id}", videoflyer.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.email", is("foo@example.com")))
                .andExpect(jsonPath("$.payload.picAndVid", is(true)))
                .andDo(document("videoflyer/update-videoflyer",
                        pathParameters(
                                parameterWithName("id").description("Videoflyers id")
                        ),
                        requestFields(
                                fieldWithPath("id").description("Videoflyers id"),
                                fieldWithPath("firstName").description("Videoflyers first name"),
                                fieldWithPath("lastName").description("Videoflyers last name"),
                                fieldWithPath("email").description("Videoflyers email"),
                                fieldWithPath("tel").description("Videoflyers phone number"),
                                fieldWithPath("picAndVid").description("true if the Videoflyer makes pictures and videos")
                        ), responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload.id").description("Videoflyers id"),
                                fieldWithPath("payload.firstName").description("Videoflyers first name"),
                                fieldWithPath("payload.lastName").description("Videoflyers last name"),
                                fieldWithPath("payload.email").description("Videoflyers email"),
                                fieldWithPath("payload.tel").description("Videoflyers phone number"),
                                fieldWithPath("payload.picAndVid").description("true if the Videoflyer makes pictures and videos"),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testUpdateVideoflyer_Unauthorized() throws Exception {
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
    public void testUpdateVideoflyer_NotFound() throws Exception {
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
    public void testDeleteVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/videoflyer/{id}", videoflyer.getId())
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("videoflyer/delete-videoflyer",
                        pathParameters(
                                parameterWithName("id").description("Videoflyers id")
                        ), responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("exception").ignored(),
                                fieldWithPath("payload").ignored()
                        )));
    }

    @Test
    public void testDeleteVideoflyer_Unauthorized() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());

        mockMvc.perform(delete("/api/videoflyer/{id}", videoflyer.getId())
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteVideoflyer_NotFound() throws Exception {

        mockMvc.perform(delete("/api/videoflyer/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Videoflyer not found")));
    }


    @Test
    public void testGetVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        videoflyerService.assignVideoflyerToJumpday(jumpday.getDate(), videoflyer.getId(), new SimpleAssignment(true));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/videoflyer/{id}", videoflyer.getId())
                .header("Authorization", MockJwtDecoder.addHeader(READ_VIDEOFLYER))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.assignments." + LocalDate.now() + ".assigned", is(true)))
                .andDo(document("videoflyer/get-videoflyer",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload.id").description("Videoflyers id"),
                                fieldWithPath("payload.firstName").description("Videoflyers first name"),
                                fieldWithPath("payload.lastName").description("Videoflyers last name"),
                                fieldWithPath("payload.email").description("Videoflyers email"),
                                fieldWithPath("payload.tel").description("Videoflyers phone number"),
                                fieldWithPath("payload.picAndVid").description("true if the Videoflyer makes pictures and videos"),
                                fieldWithPath("payload.assignments").description("key value pairs of date and the videoflyers assignment state as boolean"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".assigned").description("true if the flyer is assigned"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".allday").description("true if the flyer is assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".from").description("from time if the flyer is not assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".to").description("to time if the flyer is not assigned all day"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.firstName").description("videoflyer's first name"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.lastName").description("videoflyer's last name"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.email").description("videoflyer's email"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.tel").description("videoflyer's phone number"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.picAndVid").description("videoflyer's pic and video setting"),
                                fieldWithPath("payload.assignments." + LocalDate.now() + ".flyer.id").description("videoflyer's id"),
                                fieldWithPath("payload.assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetVideoflyer_NotFound() throws Exception {
        mockMvc.perform(get("/api/videoflyer/{id}", "999999999")
                .header("Authorization", MockJwtDecoder.addHeader(READ_VIDEOFLYER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Videoflyer not found")));
    }

    @Test
    public void testGetVideoflyer_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/videoflyer/{id}", "999999999")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAssignVideoflyer() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/videoflyer/{id}/assign", videoflyerDetailsDTO.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andDo(document("videoflyer/assign-videoflyer",
                        pathParameters(
                                parameterWithName("id").description("Videoflyers id")
                        ),
                        requestFields(
                                fieldWithPath("id").description("Videoflyers id"),
                                fieldWithPath("assignments").description("details on the videoflyers assignment state"),
                                fieldWithPath("assignments." + LocalDate.now() + ".assigned").description("true if the flyer is assigned"),
                                fieldWithPath("assignments." + LocalDate.now() + ".allday").description("true if the flyer is assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now() + ".from").description("from time if the flyer is not assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now() + ".to").description("to time if the flyer is not assigned all day"),
                                fieldWithPath("assignments." + LocalDate.now()).ignored(),
                                fieldWithPath("firstName").ignored(),
                                fieldWithPath("lastName").ignored(),
                                fieldWithPath("email").ignored(),
                                fieldWithPath("tel").ignored(),
                                fieldWithPath("picAndVid").ignored()
                        ),
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("exception").ignored(),
                                fieldWithPath("payload").ignored()
                        )));
    }

    @Test
    public void testAssignVideoflyer_Unauthorized() throws Exception {
        String videoflyerJson = json(ModelMockHelper.createVideoflyer());

        mockMvc.perform(patch("/api/videoflyer/{id}/assign", "99999999")
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAssignVideoflyer_BadRequest() throws Exception {
        Videoflyer videoflyer = videoflyerRepository.save(ModelMockHelper.createVideoflyer());
        Jumpday jumpday = ModelMockHelper.createJumpday();
        jumpdayService.saveJumpday(jumpday);
        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer, Map.of(LocalDate.now(), new SimpleAssignment(true)));

        String videoflyerJson = json(videoflyerDetailsDTO);

        mockMvc.perform(patch("/api/videoflyer/{id}/assign", "99999999")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_VIDEOFLYER))
                .contentType(contentType)
                .content(videoflyerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testAssignVideoflyer_NotFound() throws Exception {
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
