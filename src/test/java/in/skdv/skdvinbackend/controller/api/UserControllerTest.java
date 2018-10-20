package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.PasswordDto;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.repository.UserRepository;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

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

        userRepository.deleteAll();

        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);
        doReturn(new JavaMailSenderImpl().createMimeMessage()).when(mailSender).createMimeMessage();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testCreateNewUser() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("max")))
                .andDo(document("user/create-user", requestFields(
                        fieldWithPath("username").description("The users username"),
                        fieldWithPath("password").description("The password for the new user"),
                        fieldWithPath("email").description("The new user's email address"),
                        fieldWithPath("roles").ignored(),
                        fieldWithPath("enabled").ignored(),
                        fieldWithPath("verificationToken").ignored(),
                        fieldWithPath("passwordResetToken").ignored()
                ), responseFields(
                        fieldWithPath("username").description("The confirmed user's username"),
                        fieldWithPath("email").description("The confirmed user's email address"),
                        fieldWithPath("roles").description("The confirmed user's roles")
                )));
    }

    @Test
    @WithMockUser
    public void testCreateNewUser_NoAdmin() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testCreateNewUser_UserExists() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    public void testCreateNewUser_InvalidEmail() throws Exception {
        String userJson = json(new UserDTO("foo", "baz", Collections.singletonList(Role.ROLE_USER)));

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateNewUserUnauthorized() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testInitialUserSetup() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/setup")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("max")));
    }

    @Test
    public void testInitialUserSetup_UsersExist() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/setup")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/user/setup")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void testConfirmation() throws Exception {
        User user = ModelMockHelper.createUser();
        userService.registerNewUser(user);
        User savedUser = userRepository.findByUsername(user.getUsername());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/user/confirm/{token}", savedUser.getVerificationToken().getToken()))
                .andExpect(status().isOk())
                .andDo(document("user/confirm", pathParameters(
                        parameterWithName("token").description("The token created during user registration")
                ), responseFields(
                        fieldWithPath("username").description("The confirmed user's username"),
                        fieldWithPath("email").description("The confirmed user's email address"),
                        fieldWithPath("roles").description("The confirmed user's roles")
                )));
    }

    @Test
    @WithMockUser
    public void testConfirmation_TokenNotFound() throws Exception {
        mockMvc.perform(get("/api/user/confirm/foo"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testInternationalization_Default() throws Exception {
        // By default the Locale should be GERMANY

        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("max")));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertTrue("Mail contains German 'Hallo'", argument.getValue().getContent().toString().contains("Hallo"));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testInternationalization_UrlParamDE() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/user?lang=de")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("max")))
                .andDo(document("user/internationalization", requestParameters(
                        parameterWithName("lang").description("The locale for this request").optional()
                )));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertTrue("Mail contains German 'Hallo'", argument.getValue().getContent().toString().contains("Hallo"));
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testInternationalization_UrlParamEN() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user?lang=en")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("max")));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertTrue("Mail contains English 'Hello'", argument.getValue().getContent().toString().contains("Hello"));
    }

    @Test
    public void testResetPassword_DE() throws Exception {
        User user = ModelMockHelper.createUser();
        User savedUser = userRepository.save(user);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/user/resetpassword?lang=de&email=" + savedUser.getEmail())
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Wir haben dir eine E-Mail mit einem Link zur Passwortwiederherstellung gesendet.")))
                .andDo(document("user/reset-password", requestParameters(
                        parameterWithName("email").description("The email address of the user. The password reset token will be sent there"),
                        parameterWithName("lang").description("The locale for this request").optional()
                ), responseFields(
                        fieldWithPath("success").description("Was the request successful?"),
                        fieldWithPath("message").description("The message of the response"),
                        fieldWithPath("exception").ignored(),
                        fieldWithPath("payload").ignored()
                )));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals("Information zum Passwort Reset", argument.getValue().getSubject());
        assertTrue("Mail contains German 'Hallo'", argument.getValue().getContent().toString().contains("Hallo"));
    }

    @Test
    public void testResetPassword_EN() throws Exception {
        User user = ModelMockHelper.createUser();
        User savedUser = userRepository.save(user);

        mockMvc.perform(post("/api/user/resetpassword?lang=en&email=" + savedUser.getEmail())
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("We've sent you an email containing a link for password recovery.")));

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals("Password reset information", argument.getValue().getSubject());
        assertTrue("Mail contains English 'Hello'", argument.getValue().getContent().toString().contains("Hello"));
    }

    @Test
    public void testResetPassword_EmailNotFound() throws Exception {
        mockMvc.perform(post("/api/user/resetpassword?lang=en&email=notexisting@example.com")
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testChangePassword() throws Exception {
        User user = ModelMockHelper.createUser();
        User savedUser = userRepository.save(user);
        GenericResult<User> userWithToken = userService.sendPasswordResetToken(savedUser);

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword("foo253$)");
        String passwordJson = json(passwordDto);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/user/changepassword/{token}?lang=en", userWithToken.getPayload().getPasswordResetToken().getToken())
                .content(passwordJson)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Password changed successfully.")))
                .andDo(document("user/change-password",
                        pathParameters(
                                parameterWithName("token").description("The token created when invoking password reset")
                        ),
                        requestFields(
                                fieldWithPath("newPassword").description("The new password")
                                        .attributes(key("constraints").value("Must not be null or empty and valid according to password rules"))
                        ), responseFields(
                                fieldWithPath("success").description("Was the request successful?"),
                                fieldWithPath("message").description("The message of the response"),
                                fieldWithPath("exception").ignored(),
                                fieldWithPath("payload").ignored()
                        )));
    }

    @Test
    public void testChangePassword_InvalidToken() throws Exception {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword("foo253$)");
        String passwordJson = json(passwordDto);

        mockMvc.perform(post("/api/user/changepassword/footoken")
                .content(passwordJson)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testChangePassword_NoPasswordSet1() throws Exception {
        User user = ModelMockHelper.createUser();
        User savedUser = userRepository.save(user);
        GenericResult<User> userWithToken = userService.sendPasswordResetToken(savedUser);

        mockMvc.perform(post("/api/user/changepassword/" + userWithToken.getPayload().getPasswordResetToken().getToken())
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChangePassword_NoPasswordSet2() throws Exception {
        User user = ModelMockHelper.createUser();
        User savedUser = userRepository.save(user);
        GenericResult<User> userWithToken = userService.sendPasswordResetToken(savedUser);

        PasswordDto passwordDto = new PasswordDto();
        String passwordJson = json(passwordDto);

        mockMvc.perform(post("/api/user/changepassword/" + userWithToken.getPayload().getPasswordResetToken().getToken())
                .content(passwordJson)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
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
