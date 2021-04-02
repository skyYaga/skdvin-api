package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.service.IWaiverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class WaiverControllerTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;
    private WaiverConverter converter = new WaiverConverter();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IWaiverService waiverService;

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
    void setup(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void testGetAllWaivers() throws Exception {
        waiverService.saveWaiver(converter.convertToDto(ModelMockHelper.createWaiver()));
        waiverService.saveWaiver(converter.convertToDto(ModelMockHelper.createWaiver()));

        mockMvc.perform(get("/api/waivers")
                .header("Authorization", MockJwtDecoder.addHeader(READ_WAIVERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("waivers/get-waivers",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload[].id").description("waivers id"),
                                fieldWithPath("payload[].appointmentId").description("Appointment id"),
                                fieldWithPath("payload[].waiverText").description("Waiver Text"),
                                fieldWithPath("payload[].waiverCustomer.firstName").description("Customers first name"),
                                fieldWithPath("payload[].waiverCustomer.lastName").description("Customers last name"),
                                fieldWithPath("payload[].waiverCustomer.tel").description("Customers phone number"),
                                fieldWithPath("payload[].waiverCustomer.zip").description("Customers zip code"),
                                fieldWithPath("payload[].waiverCustomer.city").description("Customers city"),
                                fieldWithPath("payload[].waiverCustomer.street").description("Customers street"),
                                fieldWithPath("payload[].waiverCustomer.dateOfBirth").description("Customers date of birth"),
                                fieldWithPath("payload[].signature").description("customers signature"),
                                fieldWithPath("payload[].parentSignature1").description("parent's signature 1 for minors"),
                                fieldWithPath("payload[].parentSignature2").description("parent's signature 2 for minors"),
                                fieldWithPath("payload[].gdprSocial").description("Allow social sharing?"),
                                fieldWithPath("exception").ignored()
                        )));
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

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/waivers")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isCreated())
                .andDo(document("waivers/save-waiver",
                        requestFields(
                                fieldWithPath("id").description("waivers id"),
                                fieldWithPath("appointmentId").description("Appointment id"),
                                fieldWithPath("waiverText").description("Waiver Text"),
                                fieldWithPath("waiverCustomer.firstName").description("Customers first name"),
                                fieldWithPath("waiverCustomer.lastName").description("Customers last name"),
                                fieldWithPath("waiverCustomer.tel").description("Customers phone number"),
                                fieldWithPath("waiverCustomer.zip").description("Customers zip code"),
                                fieldWithPath("waiverCustomer.city").description("Customers city"),
                                fieldWithPath("waiverCustomer.street").description("Customers street"),
                                fieldWithPath("waiverCustomer.dateOfBirth").description("Customers date of birth"),
                                fieldWithPath("signature").description("customers signature"),
                                fieldWithPath("parentSignature1").description("parent's signature 1 for minors"),
                                fieldWithPath("parentSignature2").description("parent's signature 2 for minors"),
                                fieldWithPath("gdprSocial").description("Allow social sharing?")
                        )));
    }


    @Test
    void testSaveWaiver_MinorMissingSignatures_DE() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());
        waiver1.getWaiverCustomer().setDateOfBirth(LocalDate.now().minusYears(16));

        String userJson = json(waiver1);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/waivers")
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

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/waivers")
                .header("Accept-Language", "en")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Minors need the signatures of both guardians")));
    }

    @Test
    void testUpdateWaiver() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());

        String userJson = json(waiver1);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/waivers")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_WAIVERS))
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isNoContent())
                .andDo(document("waivers/update-waiver",
                        requestFields(
                                fieldWithPath("id").description("waivers id"),
                                fieldWithPath("appointmentId").description("Appointment id"),
                                fieldWithPath("waiverText").description("Waiver Text"),
                                fieldWithPath("waiverCustomer.firstName").description("Customers first name"),
                                fieldWithPath("waiverCustomer.lastName").description("Customers last name"),
                                fieldWithPath("waiverCustomer.tel").description("Customers phone number"),
                                fieldWithPath("waiverCustomer.zip").description("Customers zip code"),
                                fieldWithPath("waiverCustomer.city").description("Customers city"),
                                fieldWithPath("waiverCustomer.street").description("Customers street"),
                                fieldWithPath("waiverCustomer.dateOfBirth").description("Customers date of birth"),
                                fieldWithPath("signature").description("customers signature"),
                                fieldWithPath("parentSignature1").description("parent's signature 1 for minors"),
                                fieldWithPath("parentSignature2").description("parent's signature 2 for minors"),
                                fieldWithPath("gdprSocial").description("Allow social sharing?")
                        )));
    }

    @Test
    void testUpdateWaiver_Unauthorized() throws Exception {
        WaiverDTO waiver1 = converter.convertToDto(ModelMockHelper.createWaiver());

        String userJson = json(waiver1);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/waivers")
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
