package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.repository.UserRepository;
import in.skdv.skdvinbackend.service.IUserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

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
                        .apply(springSecurity()).build();

        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    public void testCreateNewUser() throws Exception {
        String userJson = json(ModelMockHelper.createUser());

        mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("max")));
    }

    @Test
    @WithMockUser
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
        String userJson = json(new UserDTO("foo", "bar", "baz", Collections.singletonList(Role.ROLE_USER)));

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
    @WithMockUser
    public void testConfirmation() throws Exception {
        User user = ModelMockHelper.createUser();
        String userJson = json(user);

        ResultActions resultActions = mockMvc.perform(post("/api/user/")
                .contentType(contentType)
                .content(userJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        User savedUser = userRepository.findByUsername(user.getUsername());

        mockMvc.perform(get("/api/user/confirm/" + savedUser.getVerificationToken().getToken()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testConfirmation_TokenNotFound() throws Exception {
        mockMvc.perform(get("/api/user/confirm/foo"))
                .andExpect(status().isNotFound());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
