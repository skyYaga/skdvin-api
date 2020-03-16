package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
public class TandemmasterControllerTest extends AbstractSkdvinTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @Autowired
    private TandemmasterRepository tandemmasterRepository;

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
    }

    @Test
    public void testCreateTandemmaster() throws Exception {
        String tandemmasterJson = json(ModelMockHelper.createTandemmaster());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/tandemmaster/")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andDo(MockMvcResultHandlers.print())
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
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetTandemmaster() throws Exception {
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(new Tandemmaster("john", "doe"));

        mockMvc.perform(get("/api/tandemmaster")
                .header("Authorization", MockJwtDecoder.addHeader(READ_TANDEMMASTER))
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("tandemmaster/get-tandemmaster",
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
    public void testGetTandemmaster_Unauthorized() throws Exception {
        tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmasterRepository.save(new Tandemmaster("john", "doe"));

        mockMvc.perform(get("/api/tandemmaster")
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateTandemmaster() throws Exception {
        Tandemmaster tandemmaster = tandemmasterRepository.save(ModelMockHelper.createTandemmaster());
        tandemmaster.setEmail("foo@example.com");
        tandemmaster.setHandcam(true);
        TandemmasterConverter converter = new TandemmasterConverter();

        String tandemmasterJson = json(converter.convertToDto(tandemmaster));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/tandemmaster/{id}", tandemmaster.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_TANDEMMASTER))
                .contentType(contentType)
                .content(tandemmasterJson))
                .andDo(MockMvcResultHandlers.print())
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
                .andDo(MockMvcResultHandlers.print())
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
                .andDo(MockMvcResultHandlers.print())
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
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteTandemmaster_NotFound() throws Exception {

        mockMvc.perform(delete("/api/tandemmaster/{id}", 9999999)
                .header("Authorization", MockJwtDecoder.addHeader(DELETE_TANDEMMASTER))
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Tandemmaster not found")));
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
