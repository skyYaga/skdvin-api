package in.skdv.skdvinbackend.service.impl;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.roles.Role;
import com.auth0.json.mgmt.users.User;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.model.common.user.UserListResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Auth0UserServiceTest extends AbstractSkdvinTest {

    @Autowired
    private ManagementAPI managementAPI;
    @Autowired
    private Auth0UserService auth0UserService;

    @Test
    void testGetUsers() throws Auth0Exception {

        // url: https://skdvin-dev.eu.auth0.com/oauth/token
        // response: {"access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik9UaEJNVEF4UlRjd1FrUkROak01TXpBelFqTTRNRE0wTmtKRFJEYzJRelUxUVRreU5qWXlNZyJ9.eyJpc3MiOiJodHRwczovL3NrZHZpbi1kZXYuZXUuYXV0aDAuY29tLyIsInN1YiI6Ik52UEV6cmxzZjJ1M0xGZ2J5UXpkQkxiNlVDM2ZKanNjQGNsaWVudHMiLCJhdWQiOiJodHRwczovL3NrZHZpbi1kZXYuZXUuYXV0aDAuY29tL2FwaS92Mi8iLCJpYXQiOjE2ODIwODY5OTYsImV4cCI6MTY4MjE3MzM5NiwiYXpwIjoiTnZQRXpybHNmMnUzTEZnYnlRemRCTGI2VUMzZkpqc2MiLCJzY29wZSI6InJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIHJlYWQ6cm9sZXMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.ntng1yfotdNaPvZ_7-qhtWBo7R4iTxZ12f5DrWXMgr6YJYMPnQJSgMXlK5cokdnQO-t8XyubSzR2FFHT5tHqg5XAZWdmiAwl97DtS5rdtlyrNkdbHDW2eMoL4u7nd5NlD858fCfDfhlmHFh-8SgvLcqzsQqJo17U6AV63OGCr-RWTPxnkNqi0U4VhR51rfILCElQyn0a-YS49K8l57AShR1mfJI30hsUOe1BRXdcMjbzV3vS7eu-1FEytyt5HnYnFqvm7LLO7q5uahP8J_MULdS3CqB7F7eXb1JqluU5VlSygW4Arm7BMh9WIwXVSSWAwPLYMbTPhgXGW4Y__Rn-Pg",
        // "scope":"read:users update:users read:roles","expires_in":86400,"token_type":"Bearer"}
        // Arrange
        List<User> userList = createMockUserList();

        /*Request<UsersPage> usersRequest = (Request<UsersPage>) Mockito.mock(Request.class);
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
        when(rolesPage2.getItems()).thenReturn(createRolesForUser(2));*/

        // Act
        UserListResult userListResult = auth0UserService.getUsers(0, 10);
        var users = userListResult.getUsers();

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
/*
    @Test
    void testGetUsers_ExceptionRetrievingRoles() throws Auth0Exception {
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
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getUsers(0, 10));

        // Assert
        assertEquals("Error retrieving roles from auth0", exception.getMessage());
    }

    @Test
    void testGetUsers_ExceptionRetrievingUsers() throws Auth0Exception {
        // Arrange
        List<User> userList = createMockUserList();

        Request<UsersPage> usersRequest = (Request<UsersPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.list(any())).thenReturn(usersRequest);
        when(usersRequest.execute()).thenThrow(new Auth0Exception("Auth0 not available"));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getUsers(0, 10));

        // Assert
        assertEquals("Error retrieving users from auth0", exception.getMessage());
    }

    @Test
    void testUpdateUser() throws Auth0Exception {
        // Arrange
        UserDTO newDTO = new UserDTO("1", "foo@example.com",
                new ArrayList<>(Arrays.asList(
                        new RoleDTO("2", "VIDEOFLYER"),
                        new RoleDTO("3", "MANIFEST"))
                )
        );
        Request<RolesPage> rolesRequest = (Request<RolesPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);
        RolesPage rolesPage = Mockito.mock(RolesPage.class);
        Request addRolesRequest = Mockito.mock(Request.class);
        Request removeRolesRequest = Mockito.mock(Request.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.listRoles(anyString(), any())).thenReturn(rolesRequest);
        when(usersEntity.addRoles(anyString(), any())).thenReturn(addRolesRequest);
        when(usersEntity.removeRoles(anyString(), any())).thenReturn(removeRolesRequest);
        when(rolesRequest.execute()).thenReturn(rolesPage);
        when(rolesPage.getItems()).thenReturn(createRolesForUser(1));
        when(addRolesRequest.execute()).thenReturn(null);
        when(removeRolesRequest.execute()).thenReturn(null);

        // Act
        auth0UserService.updateUser(newDTO);

        // Assert
        verify(usersEntity).addRoles("1", Collections.singletonList("3"));
        verify(usersEntity).removeRoles("1", Collections.singletonList("1"));
        verify(addRolesRequest).execute();
        verify(removeRolesRequest).execute();
    }

    @Test
    void testUpdateUser_NoChanges() throws Auth0Exception {
        // Arrange
        UserDTO newDTO = new UserDTO("1", "foo@example.com",
                new ArrayList<>(Arrays.asList(
                        new RoleDTO("1", "TANDEMMASTER"),
                        new RoleDTO("2", "VIDEOFLYER"))
                )
        );
        Request<RolesPage> rolesRequest = (Request<RolesPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);
        RolesPage rolesPage = Mockito.mock(RolesPage.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.listRoles(anyString(), any())).thenReturn(rolesRequest);
        when(rolesRequest.execute()).thenReturn(rolesPage);
        when(rolesPage.getItems()).thenReturn(createRolesForUser(1));

        // Act
        auth0UserService.updateUser(newDTO);

        // Assert
        verify(usersEntity, never()).addRoles(any(), any());
        verify(usersEntity, never()).removeRoles(any(), any());
    }

    @Test
    void testUpdateUser_ExceptionUpdatingRoles() throws Auth0Exception {
        // Arrange
        UserDTO newDTO = new UserDTO("1", "foo@example.com",
                new ArrayList<>(Arrays.asList(
                        new RoleDTO("2", "VIDEOFLYER"),
                        new RoleDTO("3", "MANIFEST"))
                )
        );

        Request<RolesPage> rolesRequest = (Request<RolesPage>) Mockito.mock(Request.class);
        UsersEntity usersEntity = Mockito.mock(UsersEntity.class);
        RolesPage rolesPage = Mockito.mock(RolesPage.class);
        Request addRolesRequest = Mockito.mock(Request.class);

        when(managementAPI.users()).thenReturn(usersEntity);
        when(usersEntity.listRoles(anyString(), any())).thenReturn(rolesRequest);
        when(usersEntity.addRoles(anyString(), any())).thenReturn(addRolesRequest);
        when(rolesRequest.execute()).thenReturn(rolesPage);
        when(rolesPage.getItems()).thenReturn(createRolesForUser(1));
        when(addRolesRequest.execute()).thenReturn(null);
        when(addRolesRequest.execute()).thenThrow(new Auth0Exception("Auth0 not available"));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.updateUser(newDTO));

        // Assert
        assertEquals("Error updating roles from auth0", exception.getMessage());
    }

    @Test
    void testFindRolesToAdd() {
        ArrayList<RoleDTO> updatedRoles = new ArrayList<>(Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );
        ArrayList<RoleDTO> currentRoles = new ArrayList<>(Collections.singletonList(new RoleDTO("1", "TANDEMMASTER")));

        List<String> roleIdsToAdd = auth0UserService.findRolesToAdd(currentRoles, updatedRoles);

        assertEquals(2, updatedRoles.size());
        assertEquals(1, currentRoles.size());
        assertEquals("TANDEMMASTER", currentRoles.get(0).getName());
        assertEquals(1, roleIdsToAdd.size());
        assertEquals("2", roleIdsToAdd.get(0));
    }

    @Test
    void testFindRolesToRemove() {
        ArrayList<RoleDTO> updatedRoles = new ArrayList<>(Collections.singletonList(new RoleDTO("1", "TANDEMMASTER")));
        ArrayList<RoleDTO> currentRoles = new ArrayList<>(Arrays.asList(
                new RoleDTO("1", "TANDEMMASTER"),
                new RoleDTO("2", "VIDEOFLYER"))
        );

        List<String> roleIdsToRemove = auth0UserService.findRolesToRemove(currentRoles, updatedRoles);

        assertEquals(1, updatedRoles.size());
        assertEquals(2, currentRoles.size());
        assertEquals("TANDEMMASTER", updatedRoles.get(0).getName());
        assertEquals(1, roleIdsToRemove.size());
        assertEquals("2", roleIdsToRemove.get(0));
    }

    @Test
    void testGetRoles() throws Auth0Exception {
        // Arrange
        Request<RolesPage> rolesRequest = (Request<RolesPage>) Mockito.mock(Request.class);
        RolesEntity rolesEntity = Mockito.mock(RolesEntity.class);
        RolesPage rolesPage = Mockito.mock(RolesPage.class);

        when(managementAPI.roles()).thenReturn(rolesEntity);
        when(rolesEntity.list(any())).thenReturn(rolesRequest);
        when(rolesRequest.execute()).thenReturn(rolesPage);
        when(rolesPage.getItems()).thenReturn(createRolesForUser(1));

        // Act
        List<RoleDTO> roles = auth0UserService.getRoles();

        // Assert
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals("1", roles.get(0).getId());
        assertEquals("TANDEMMASTER",roles.get(0).getName());
        assertEquals("2", roles.get(1).getId());
        assertEquals("VIDEOFLYER", roles.get(1).getName());
    }

    @Test
    void testGetRoles_ExceptionRetrievingRoles() throws Auth0Exception {
        // Arrange
        Request<RolesPage> rolesRequest = (Request<RolesPage>) Mockito.mock(Request.class);
        RolesEntity rolesEntity = Mockito.mock(RolesEntity.class);
        RolesPage rolesPage = Mockito.mock(RolesPage.class);

        when(managementAPI.roles()).thenReturn(rolesEntity);
        when(rolesEntity.list(any())).thenReturn(rolesRequest);
        when(rolesRequest.execute()).thenThrow(new Auth0Exception("Auth0 not available"));
        when(rolesPage.getItems()).thenReturn(createRolesForUser(1));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getRoles());

        // Assert
        assertEquals("Error retrieving roles from auth0", exception.getMessage());
    }
*/
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
            ReflectionTestUtils.setField(role1, "id", "1");
            role1.setName("TANDEMMASTER");
            Role role2 = new Role();
            ReflectionTestUtils.setField(role2, "id", "2");
            role2.setName("VIDEOFLYER");
            return Arrays.asList(role1, role2);
        }
        if (i == 2) {
            Role role1 = new Role();
            ReflectionTestUtils.setField(role1, "id", "3");
            role1.setName("MANIFEST");
            return Collections.singletonList(role1);
        }
        return Collections.emptyList();
    }
}
