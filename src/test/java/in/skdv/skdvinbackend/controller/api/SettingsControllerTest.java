package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.SettingsRepository;
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
import java.util.Arrays;
import java.util.Locale;

import static in.skdv.skdvinbackend.config.Authorities.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
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
public class SettingsControllerTest extends AbstractSkdvinTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        settingsRepository.deleteAll();
    }


    @Test
    public void testCreateSettings() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/settings/")
                .header("Authorization", MockJwtDecoder.addHeader(CREATE_SETTINGS))
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(5)))
                .andExpect(jsonPath("$.payload.commonSettings.de.dropzone.name", is("Example DZ")))
                .andDo(document("settings/create-settings", requestFields(
                        fieldWithPath("adminSettings").description("Object with admin settings"),
                        fieldWithPath("adminSettings.tandemsFrom").description("Default tandems from setting"),
                        fieldWithPath("adminSettings.tandemsTo").description("Default tandems to setting"),
                        fieldWithPath("adminSettings.interval").description("Default slot interval"),
                        fieldWithPath("adminSettings.tandemCount").description("Default capacity of tandem slots"),
                        fieldWithPath("adminSettings.picOrVidCount").description("Default capacity of picture OR video slots"),
                        fieldWithPath("adminSettings.picAndVidCount").description("Default capacity of picture AND video slots"),
                        fieldWithPath("adminSettings.handcamCount").description("Default capacity of handcam slots"),
                        fieldWithPath("commonSettings").description("Object with common settings"),
                        fieldWithPath("commonSettings.de.dropzone").description("Object with dropzone details"),
                        fieldWithPath("commonSettings.de.dropzone.name").description("Dropzone name"),
                        fieldWithPath("commonSettings.de.dropzone.email").description("Dropzone email"),
                        fieldWithPath("commonSettings.de.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("commonSettings.de.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("commonSettings.de.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("commonSettings.de.dropzone.transportationAgreementUrl")
                                .description("URL to transportation and liability agreement"),
                        fieldWithPath("commonSettings.de.homepageHint").description("Homepage hint"),
                        fieldWithPath("commonSettings.de.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("commonSettings.de.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("commonSettings.de.faq[].question").description("FAQ question"),
                        fieldWithPath("commonSettings.de.faq[].answer").description("FAQ answer")
                ), responseFields(
                        fieldWithPath("payload.adminSettings").description("Object with admin settings"),
                        fieldWithPath("payload.adminSettings.tandemsFrom").description("Default tandems from setting"),
                        fieldWithPath("payload.adminSettings.tandemsTo").description("Default tandems to setting"),
                        fieldWithPath("payload.adminSettings.interval").description("Default slot interval"),
                        fieldWithPath("payload.adminSettings.tandemCount").description("Default capacity of tandem slots"),
                        fieldWithPath("payload.adminSettings.picOrVidCount").description("Default capacity of picture OR video slots"),
                        fieldWithPath("payload.adminSettings.picAndVidCount").description("Default capacity of picture AND video slots"),
                        fieldWithPath("payload.adminSettings.handcamCount").description("Default capacity of handcam slots"),
                        fieldWithPath("payload.commonSettings").description("Object with common settings"),
                        fieldWithPath("payload.commonSettings.de.dropzone").description("Object with dropzone details"),
                        fieldWithPath("payload.commonSettings.de.dropzone.name").description("Dropzone name"),
                        fieldWithPath("payload.commonSettings.de.dropzone.email").description("Dropzone email"),
                        fieldWithPath("payload.commonSettings.de.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("payload.commonSettings.de.dropzone.transportationAgreementUrl")
                                .description("URL to transportation and liability agreement"),
                        fieldWithPath("payload.commonSettings.de.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("payload.commonSettings.de.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("payload.commonSettings.de.homepageHint").description("Homepage hint"),
                        fieldWithPath("payload.commonSettings.de.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("payload.commonSettings.de.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("payload.commonSettings.de.faq[].id").description("FAQ entry id"),
                        fieldWithPath("payload.commonSettings.de.faq[].question").description("FAQ question"),
                        fieldWithPath("payload.commonSettings.de.faq[].answer").description("FAQ answer"),
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("exception").ignored(),
                        fieldWithPath("payload.id").ignored()
                )));
    }

    @Test
    public void testCreateSettings_Unauthorized() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(post("/api/settings/")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateSettings() throws Exception {
        Settings settings = settingsRepository.save(ModelMockHelper.createSettings());
        settings.getAdminSettings().setTandemCount(2);
        settings.getCommonSettings().get(Locale.GERMAN.getLanguage()).getDropzone().setName("Renamed DZ");
        String settingsJson = json(settings);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/settings/{id}", settings.getId())
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_SETTINGS))
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(2)))
                .andExpect(jsonPath("$.payload.commonSettings.de.dropzone.name", is("Renamed DZ")))
                .andDo(document("settings/update-settings", pathParameters(
                        parameterWithName("id").description("The id of the settings object")
                ), requestFields(
                        fieldWithPath("adminSettings").description("Object with admin settings"),
                        fieldWithPath("adminSettings.tandemsFrom").description("Default tandems from setting"),
                        fieldWithPath("adminSettings.tandemsTo").description("Default tandems to setting"),
                        fieldWithPath("adminSettings.interval").description("Default slot interval"),
                        fieldWithPath("adminSettings.tandemCount").description("Default capacity of tandem slots"),
                        fieldWithPath("adminSettings.picOrVidCount").description("Default capacity of picture OR video slots"),
                        fieldWithPath("adminSettings.picAndVidCount").description("Default capacity of picture AND video slots"),
                        fieldWithPath("adminSettings.handcamCount").description("Default capacity of handcam slots"),
                        fieldWithPath("commonSettings").description("Object with common settings"),
                        fieldWithPath("commonSettings.de.dropzone").description("Object with dropzone details"),
                        fieldWithPath("commonSettings.de.dropzone.name").description("Dropzone name"),
                        fieldWithPath("commonSettings.de.dropzone.email").description("Dropzone email"),
                        fieldWithPath("commonSettings.de.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("commonSettings.de.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("commonSettings.de.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("commonSettings.de.dropzone.transportationAgreementUrl")
                                .description("URL to transportation and liability agreement"),
                        fieldWithPath("commonSettings.de.homepageHint").description("Homepage hint"),
                        fieldWithPath("commonSettings.de.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("commonSettings.de.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("commonSettings.de.faq[].question").description("FAQ question"),
                        fieldWithPath("commonSettings.de.faq[].answer").description("FAQ answer")
                ), responseFields(
                        fieldWithPath("payload.id").description("Id of the settings object"),
                        fieldWithPath("payload.adminSettings").description("Object with admin settings"),
                        fieldWithPath("payload.adminSettings.tandemsFrom").description("Default tandems from setting"),
                        fieldWithPath("payload.adminSettings.tandemsTo").description("Default tandems to setting"),
                        fieldWithPath("payload.adminSettings.interval").description("Default slot interval"),
                        fieldWithPath("payload.adminSettings.tandemCount").description("Default capacity of tandem slots"),
                        fieldWithPath("payload.adminSettings.picOrVidCount").description("Default capacity of picture OR video slots"),
                        fieldWithPath("payload.adminSettings.picAndVidCount").description("Default capacity of picture AND video slots"),
                        fieldWithPath("payload.adminSettings.handcamCount").description("Default capacity of handcam slots"),
                        fieldWithPath("payload.commonSettings").description("Object with common settings"),
                        fieldWithPath("payload.commonSettings.de.dropzone").description("Object with dropzone details"),
                        fieldWithPath("payload.commonSettings.de.dropzone.name").description("Dropzone name"),
                        fieldWithPath("payload.commonSettings.de.dropzone.email").description("Dropzone email"),
                        fieldWithPath("payload.commonSettings.de.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("payload.commonSettings.de.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("payload.commonSettings.de.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("payload.commonSettings.de.dropzone.transportationAgreementUrl")
                                .description("URL to transportation and liability agreement"),
                        fieldWithPath("payload.commonSettings.de.homepageHint").description("Homepage hint"),
                        fieldWithPath("payload.commonSettings.de.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("payload.commonSettings.de.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("payload.commonSettings.de.faq[].id").description("FAQ entry id"),
                        fieldWithPath("payload.commonSettings.de.faq[].question").description("FAQ question"),
                        fieldWithPath("payload.commonSettings.de.faq[].answer").description("FAQ answer"),
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("exception").ignored()
                )));
    }

    @Test
    public void testUpdateSettings_Unauthorized() throws Exception {
        Settings settings = settingsRepository.save(ModelMockHelper.createSettings());
        String settingsJson = json(settings);

        mockMvc.perform(put("/api/settings/{id}", settings.getId())
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateSettings_NotFound() throws Exception {
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
    public void testGetSettings() throws Exception {
        settingsRepository.save(ModelMockHelper.createSettings());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/settings/")
                .header("Authorization", MockJwtDecoder.addHeader(READ_SETTINGS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.adminSettings.tandemCount", is(5)))
                .andExpect(jsonPath("$.payload.commonSettings.de.dropzone.name", is("Example DZ")))
                .andDo(document("settings/get-settings", responseFields(
                        fieldWithPath("payload.adminSettings").description("Object with admin settings"),
                        fieldWithPath("payload.adminSettings.tandemsFrom").description("Default tandems from setting"),
                        fieldWithPath("payload.adminSettings.tandemsTo").description("Default tandems to setting"),
                        fieldWithPath("payload.adminSettings.interval").description("Default slot interval"),
                        fieldWithPath("payload.adminSettings.tandemCount").description("Default capacity of tandem slots"),
                        fieldWithPath("payload.adminSettings.picOrVidCount").description("Default capacity of picture OR video slots"),
                        fieldWithPath("payload.adminSettings.picAndVidCount").description("Default capacity of picture AND video slots"),
                        fieldWithPath("payload.adminSettings.handcamCount").description("Default capacity of handcam slots"),
                        fieldWithPath("payload.commonSettings").description("Object with common settings by locale"),
                        fieldWithPath("payload.commonSettings.de.dropzone").description("Object with dropzone details"),
                        fieldWithPath("payload.commonSettings.de.dropzone.name").description("Dropzone name"),
                        fieldWithPath("payload.commonSettings.de.dropzone.email").description("Dropzone email"),
                        fieldWithPath("payload.commonSettings.de.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("payload.commonSettings.de.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("payload.commonSettings.de.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("payload.commonSettings.de.dropzone.transportationAgreementUrl")
                                .description("URL to transportation and liability agreement"),
                        fieldWithPath("payload.commonSettings.de.homepageHint").description("Homepage hint"),
                        fieldWithPath("payload.commonSettings.de.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("payload.commonSettings.de.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("payload.commonSettings.de.faq[].id").description("FAQ entry id"),
                        fieldWithPath("payload.commonSettings.de.faq[].question").description("FAQ question"),
                        fieldWithPath("payload.commonSettings.de.faq[].answer").description("FAQ answer"),
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("exception").ignored(),
                        fieldWithPath("payload.id").ignored()
                )));
    }

    @Test
    public void testGetSettings_Unauthorized() throws Exception {
        String settingsJson = json(ModelMockHelper.createSettings());

        mockMvc.perform(get("/api/settings/")
                .contentType(contentType)
                .content(settingsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetCommonSettings_EN() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        CommonSettings commonSettingsEN = ModelMockHelper.createCommonSettings();
        commonSettingsEN.getDropzone().setName("Example DZ EN");
        settings.getCommonSettings().put(Locale.ENGLISH.getLanguage(), commonSettingsEN);
        settingsRepository.save(settings);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/settings/common/")
                .header("Accept-Language", "en-US")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.dropzone.name", is("Example DZ EN")))
                .andDo(document("settings/get-common-settings", responseFields(
                        fieldWithPath("payload").description("Object with common settings by locale"),
                        fieldWithPath("payload.dropzone").description("Object with dropzone details"),
                        fieldWithPath("payload.dropzone.name").description("Dropzone name"),
                        fieldWithPath("payload.dropzone.email").description("Dropzone email"),
                        fieldWithPath("payload.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("payload.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("payload.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("payload.dropzone.transportationAgreementUrl").description("URL to transportation and liability agreement"),
                        fieldWithPath("payload.homepageHint").description("Homepage hint"),
                        fieldWithPath("payload.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("payload.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("payload.faq[].id").description("FAQ entry id"),
                        fieldWithPath("payload.faq[].question").description("FAQ question"),
                        fieldWithPath("payload.faq[].answer").description("FAQ answer"),
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("exception").ignored()
                )));
    }

    @Test
    public void testGetCommonSettings_DE() throws Exception {
        Settings settings = ModelMockHelper.createSettings();
        CommonSettings commonSettingsEN = ModelMockHelper.createCommonSettings();
        commonSettingsEN.getDropzone().setName("Example DZ EN");
        settings.getCommonSettings().put(Locale.ENGLISH.getLanguage(), commonSettingsEN);
        settingsRepository.save(settings);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/settings/common/")
                .header("Accept-Language", "de")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.dropzone.name", is("Example DZ")))
                .andDo(document("settings/get-common-settings", responseFields(
                        fieldWithPath("payload.dropzone").description("Object with dropzone details"),
                        fieldWithPath("payload.dropzone.name").description("Dropzone name"),
                        fieldWithPath("payload.dropzone.email").description("Dropzone email"),
                        fieldWithPath("payload.dropzone.phone").description("Dropzone phone"),
                        fieldWithPath("payload.dropzone.mobile").description("Dropzone mobile"),
                        fieldWithPath("payload.dropzone.priceListUrl").description("URL to price list"),
                        fieldWithPath("payload.dropzone.transportationAgreementUrl")
                                .description("URL to transportation and liability agreement"),
                        fieldWithPath("payload.homepageHint").description("Homepage hint"),
                        fieldWithPath("payload.homepageHintTitle").description("Homepage hint title"),
                        fieldWithPath("payload.bccMail").description("Mail address for bcc mails"),
                        fieldWithPath("payload.faq[].id").description("FAQ entry id"),
                        fieldWithPath("payload.faq[].question").description("FAQ question"),
                        fieldWithPath("payload.faq[].answer").description("FAQ answer"),
                        fieldWithPath("success").description("true when the request was successful"),
                        fieldWithPath("message").description("message if there was an error"),
                        fieldWithPath("exception").ignored()
                )));
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
