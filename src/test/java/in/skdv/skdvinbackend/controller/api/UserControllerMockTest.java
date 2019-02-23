package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.dto.PasswordDto;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.service.impl.MongoUserDetailsService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static in.skdv.skdvinbackend.exception.ErrorMessage.USER_RESET_PASSWORD_FAILED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerMockTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @MockBean
    private MongoUserDetailsService userService;

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
                        .apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testCreateNewUser_MessagingException() throws Exception {
        when(userService.registerNewUser(any(User.class))).thenThrow(new MessagingException());
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());

        verify(userService).registerNewUser(any(User.class));
    }

    @Test
    public void testChangePassword_Unsuccessful() throws Exception {
        GenericResult<User> result = new GenericResult<>(true);
        result.setPayload(new User());
        when(userService.validatePasswordResetToken(any(String.class))).thenReturn(result);
        when(userService.changePassword(any(User.class), any(PasswordDto.class))).thenReturn(new GenericResult<>(false));

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword("foo253$)");
        String passwordJson = json(passwordDto);

        mockMvc.perform(post("/api/user/changepassword/{token}?lang=en", "fooToken")
                .content(passwordJson)
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());

        verify(userService).validatePasswordResetToken(any(String.class));
        verify(userService).changePassword(any(User.class), any(PasswordDto.class));
    }

    @Test
    public void testResetPassword_Unsuccessful() throws Exception {
        when(userService.findUserByEmail(any(String.class))).thenReturn(new User());
        when(userService.sendPasswordResetToken(any(User.class))).thenReturn(new GenericResult<>(false, USER_RESET_PASSWORD_FAILED));

        mockMvc.perform(post("/api/user/resetpassword?lang=en&email=foo@example.com")
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("An error occured while resetting password.")));

        verify(userService).findUserByEmail(any(String.class));
        verify(userService).sendPasswordResetToken(any(User.class));
    }

    @Test
    public void testConfirmRegistrationToken_UserNotFound() throws Exception {
        when(userService.hasVerificationToken(any(String.class))).thenReturn(true);
        when(userService.confirmRegistration(any(String.class))).thenReturn(null);

        mockMvc.perform(get("/api/user/confirm/{token}", "anyToken")
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());

        verify(userService).hasVerificationToken(any(String.class));
        verify(userService).confirmRegistration(any(String.class));
    }

    @Test
    public void testConfirmRegistrationToken_TokenExpired() throws Exception {
        when(userService.hasVerificationToken(any(String.class))).thenReturn(true);
        when(userService.confirmRegistration(any(String.class))).thenThrow(new TokenExpiredException("Token expired"));

        mockMvc.perform(get("/api/user/confirm/{token}", "anyToken")
                .contentType(contentType))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

        verify(userService).hasVerificationToken(any(String.class));
        verify(userService).confirmRegistration(any(String.class));
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
