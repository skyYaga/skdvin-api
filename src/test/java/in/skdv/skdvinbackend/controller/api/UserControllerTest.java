package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static in.skdv.skdvinbackend.config.Authorities.READ_USERS;
import static in.skdv.skdvinbackend.config.Authorities.UPDATE_USERS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
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
public class UserControllerTest extends AbstractSkdvinTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private IUserService userService;

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
    public void setup(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserDTO userDTO1 = new UserDTO("1", "foo@bar.com", Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );
        UserDTO userDTO2 = new UserDTO("2", "baz@bar.com", Collections.singletonList(
                new RoleDTO("3", "MANIFEST"))
        );
        List<UserDTO> userList = Arrays.asList(userDTO1, userDTO2);

        Mockito.when(userService.getUsers()).thenReturn(userList);

        mockMvc.perform(get("/api/users")
                .header("Authorization", MockJwtDecoder.addHeader(READ_USERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("users/get-users",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload[].userId").description("Users id"),
                                fieldWithPath("payload[].email").description("Users email"),
                                fieldWithPath("payload[].roles[]").description("Users roles"),
                                fieldWithPath("payload[].roles[].id").description("Role ID"),
                                fieldWithPath("payload[].roles[].name").description("Role name"),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetRoles() throws Exception {
        RoleDTO roleDTO1 = new RoleDTO("1", "ROLE_ADMIN");
        RoleDTO roleDTO2 = new RoleDTO("2", "ROLE_TANDEMMASTER");
        List<RoleDTO> roleList = Arrays.asList(roleDTO1, roleDTO2);

        Mockito.when(userService.getRoles()).thenReturn(roleList);

        mockMvc.perform(get("/api/users/roles")
                .header("Authorization", MockJwtDecoder.addHeader(READ_USERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)))
                .andDo(document("users/get-roles",
                        responseFields(
                                fieldWithPath("success").description("true when the request was successful"),
                                fieldWithPath("message").description("message if there was an error"),
                                fieldWithPath("payload[].id").description("Role ID"),
                                fieldWithPath("payload[].name").description("Role name"),
                                fieldWithPath("exception").ignored()
                        )));
    }

    @Test
    public void testGetRoles_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/roles")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO("1", "foo@bar.com", Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );

        doNothing().when(userService).updateUser(Mockito.any(UserDTO.class));

        String userJson = json(userDTO);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/users")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_USERS))
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isNoContent())
                .andDo(document("users/update-user",
                        requestFields(
                                fieldWithPath("userId").description("Users id"),
                                fieldWithPath("email").description("Users email"),
                                fieldWithPath("roles[]").description("Users roles"),
                                fieldWithPath("roles[].id").description("Role ID"),
                                fieldWithPath("roles[].name").description("Role name")
                        )));
    }

    @Test
    public void testUpdateUser_Unauthorized() throws Exception {
        UserDTO userDTO = new UserDTO("1", "foo@bar.com", Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );

        String userJson = json(userDTO);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/users")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isUnauthorized());
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
