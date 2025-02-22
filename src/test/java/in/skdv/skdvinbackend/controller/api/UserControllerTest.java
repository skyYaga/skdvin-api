package in.skdv.skdvinbackend.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.MockJwtDecoder;
import in.skdv.skdvinbackend.model.common.user.UserListResult;
import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends AbstractSkdvinTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Test
    void testGetAllUsers() throws Exception {
        UserDTO userDTO1 = new UserDTO("1", "foo@bar.com", Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );
        UserDTO userDTO2 = new UserDTO("2", "baz@bar.com", Collections.singletonList(
                new RoleDTO("3", "MANIFEST"))
        );
        List<UserDTO> userList = Arrays.asList(userDTO1, userDTO2);
        var userListResult = new UserListResult();
        userListResult.setUsers(userList);
        userListResult.setStart(0);
        userListResult.setTotal(2);

        Mockito.when(userService.getUsers(0, 5)).thenReturn(userListResult);

        mockMvc.perform(get("/api/users")
                .header("Authorization", MockJwtDecoder.addHeader(READ_USERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload.users", hasSize(2)));
    }

    @Test
    void testGetAllUsers_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRoles() throws Exception {
        RoleDTO roleDTO1 = new RoleDTO("1", "ROLE_ADMIN");
        RoleDTO roleDTO2 = new RoleDTO("2", "ROLE_TANDEMMASTER");
        List<RoleDTO> roleList = Arrays.asList(roleDTO1, roleDTO2);

        Mockito.when(userService.getRoles()).thenReturn(roleList);

        mockMvc.perform(get("/api/users/roles")
                .header("Authorization", MockJwtDecoder.addHeader(READ_USERS))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.payload", hasSize(2)));
    }

    @Test
    void testGetRoles_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/roles")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDTO userDTO = new UserDTO("1", "foo@bar.com", Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );

        doNothing().when(userService).updateUser(Mockito.any(UserDTO.class));

        String userJson = json(userDTO);

        mockMvc.perform(put("/api/users")
                .header("Authorization", MockJwtDecoder.addHeader(UPDATE_USERS))
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateUser_Unauthorized() throws Exception {
        UserDTO userDTO = new UserDTO("1", "foo@bar.com", Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );

        String userJson = json(userDTO);

        mockMvc.perform(put("/api/users")
                .contentType(contentType)
                .content(userJson))
                .andExpect(status().isUnauthorized());
    }

    private String json(Object o) throws IOException {
        return objectMapper.writeValueAsString(o);
    }

}
