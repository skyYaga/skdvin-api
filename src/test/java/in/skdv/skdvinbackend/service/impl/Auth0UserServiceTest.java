package in.skdv.skdvinbackend.service.impl;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.Role;
import com.auth0.json.mgmt.RolesPage;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class Auth0UserServiceTest {

    private ManagementAPI managementAPI;
    private Auth0UserService auth0UserService;

    @Before
    public void setUp() {
        managementAPI = Mockito.mock(ManagementAPI.class);
        auth0UserService = new Auth0UserService(managementAPI);
    }

    @Test
    public void testGetUsers() throws Auth0Exception {
        // Arrange
        List<User> userList = createMockUserList();

        Request<UsersPage> usersRequest = (Request<UsersPage>) Mockito.mock(Request.class);
        Request<RolesPage> rolesRequest1 = (Request<RolesPage>) Mockito.mock(Request.class);
        Request<RolesPage> rolesRequest2 = (Request<RolesPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);
        UsersPage usersPage = Mockito.mock(UsersPage.class);
        RolesPage rolesPage1 = Mockito.mock(RolesPage.class);
        RolesPage rolesPage2 = Mockito.mock(RolesPage.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.list(any())).thenReturn(usersRequest);
        when(usersEntity.listRoles(anyString(), any())).thenReturn(rolesRequest1).thenReturn(rolesRequest2);
        when(usersRequest.execute()).thenReturn(usersPage);
        when(rolesRequest1.execute()).thenReturn(rolesPage1);
        when(rolesRequest2.execute()).thenReturn(rolesPage2);
        when(usersPage.getItems()).thenReturn(userList);
        when(rolesPage1.getItems()).thenReturn(createRolesForUser(1));
        when(rolesPage2.getItems()).thenReturn(createRolesForUser(2));

        // Act
        List<UserDTO> users = auth0UserService.getUsers();

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("1", users.get(0).getUserId());
        assertEquals("foo@bar.com", users.get(0).getEmail());
        assertEquals(2, users.get(0).getRoles().size());
        assertEquals("2", users.get(1).getUserId());
        assertEquals("baz@bar.com", users.get(1).getEmail());
        assertEquals(1, users.get(1).getRoles().size());
    }

    @Test
    public void testGetUsers_ExceptionRetrievingRoles() throws Auth0Exception {
        // Arrange
        List<User> userList = createMockUserList();

        Request<UsersPage> usersRequest = (Request<UsersPage>) Mockito.mock(Request.class);
        Request<RolesPage> rolesRequest1 = (Request<RolesPage>) Mockito.mock(Request.class);
        Request<RolesPage> rolesRequest2 = (Request<RolesPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);
        UsersPage usersPage = Mockito.mock(UsersPage.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.list(any())).thenReturn(usersRequest);
        when(usersEntity.listRoles(anyString(), any())).thenReturn(rolesRequest1).thenReturn(rolesRequest2);
        when(usersRequest.execute()).thenReturn(usersPage);
        when(rolesRequest1.execute()).thenThrow(new Auth0Exception("Auth0 not available"));
        when(usersPage.getItems()).thenReturn(userList);

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getUsers());

        // Assert
        assertEquals("com.auth0.exception.Auth0Exception: Auth0 not available", exception.getMessage());
    }

    @Test
    public void testGetUsers_ExceptionRetrievingUsers() throws Auth0Exception {
        // Arrange
        List<User> userList = createMockUserList();

        Request<UsersPage> usersRequest = (Request<UsersPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.list(any())).thenReturn(usersRequest);
        when(usersRequest.execute()).thenThrow(new Auth0Exception("Auth0 not available"));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getUsers());

        // Assert
        assertEquals("com.auth0.exception.Auth0Exception: Auth0 not available", exception.getMessage());
    }


    private List<User> createMockUserList() {
        User user1 = new User();
        user1.setId("1");
        user1.setEmail("foo@bar.com");
        User user2 = new User();
        user2.setId("2");user2.setEmail("baz@bar.com");
        return new ArrayList<>(Arrays.asList(user1, user2));
    }

    private List<Role> createRolesForUser(int i) {
        if (i == 1) {
            Role role1 = new Role();
            role1.setName("tandemmaster");
            Role role2 = new Role();
            role2.setName("videoflyer");
            return Arrays.asList(role1, role1);
        }
        if (i == 2) {
            Role role1 = new Role();
            role1.setName("manifest");
            return Collections.singletonList(role1);
        }
        return Collections.emptyList();
    }
}
