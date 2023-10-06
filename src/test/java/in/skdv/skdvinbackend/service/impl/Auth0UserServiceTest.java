package in.skdv.skdvinbackend.service.impl;

import com.auth0.net.client.Auth0HttpClient;
import com.auth0.net.client.DefaultHttpClient;
import com.github.tomakehurst.wiremock.client.WireMock;
import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.model.common.user.UserListResult;
import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.util.ReflectionTestUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"}
)
@Import(Auth0UserServiceTest.Auth0TestConfiguration.class)
class Auth0UserServiceTest extends AbstractSkdvinTest {

    @Autowired
    private Auth0UserService auth0UserService;

    @BeforeEach
    void setupStubs() {
        WireMock.reset();
        stubFor(any(urlEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("token.json")));
    }

    @Test
    void testGetUsers() {
        // Arrange
        initUsersStub();
        initUser1Stub();
        initUser2Stub();

        // Act
        UserListResult userListResult = auth0UserService.getUsers(0, 5);
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


    @Test
    void testGetUsers_ExceptionRetrievingRoles() {
        // Arrange
        initUsersStub();
        stubFor(any(urlPathEqualTo("/api/v2/roles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("roles.json")
                        .withFixedDelay(1000)
                ));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getUsers(0, 5));

        // Assert
        assertEquals("Error retrieving roles from auth0", exception.getMessage());
    }

    @Test
    void testGetUsers_ExceptionRetrievingUsers() {
        // Arrange
        stubFor(any(urlPathEqualTo("/api/v2/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("users.json")
                        .withFixedDelay(1000)
                ));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getUsers(0, 5));

        // Assert
        assertEquals("Error retrieving users from auth0", exception.getMessage());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        initUser1Stub();
        UserDTO newDTO = new UserDTO("1", "foo@example.com",
                new ArrayList<>(Arrays.asList(
                        new RoleDTO("2", "VIDEOFLYER"),
                        new RoleDTO("3", "MANIFEST"))
                )
        );

        // Act
        auth0UserService.updateUser(newDTO);

        // Assert
        verify(postRequestedFor(urlEqualTo("/api/v2/users/1/roles")));
    }

    @Test
    void testUpdateUser_NoChanges() {
        // Arrange
        initUser1Stub();
        UserDTO newDTO = new UserDTO("1", "foo@example.com",
                new ArrayList<>(Arrays.asList(
                        new RoleDTO("3", "ROLE_TANDEMMASTER"),
                        new RoleDTO("4", "ROLE_VIDEOFLYER"))
                )
        );

        // Act
        auth0UserService.updateUser(newDTO);

        // Assert
        verify(0, postRequestedFor(urlEqualTo("/api/v2/users/1/roles")));
    }


    @Test
    void testUpdateUser_ExceptionUpdatingRoles() {
        // Arrange
        initUser1Stub();
        stubFor(post(urlPathEqualTo("/api/v2/users/1/roles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user1.json")
                        .withFixedDelay(1000)
                ));
        UserDTO newDTO = new UserDTO("1", "foo@example.com",
                new ArrayList<>(Arrays.asList(
                        new RoleDTO("2", "VIDEOFLYER"),
                        new RoleDTO("3", "MANIFEST"))
                )
        );

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
    void testGetRoles() {
        // Arrange
        initRolesStub();

        // Act
        List<RoleDTO> roles = auth0UserService.getRoles();

        // Assert
        assertNotNull(roles);
        assertEquals(4, roles.size());
        assertEquals("1", roles.get(0).getId());
        assertEquals("ROLE_ADMIN", roles.get(0).getName());
        assertEquals("2", roles.get(1).getId());
        assertEquals("ROLE_MODERATOR", roles.get(1).getName());
    }


    @Test
    void testGetRoles_ExceptionRetrievingRoles() {
        // Arrange
        // Arrange
        stubFor(get(urlPathEqualTo("/api/v2/roles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("roles.json")
                        .withFixedDelay(1000)
                ));

        // Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> auth0UserService.getRoles());

        // Assert
        assertEquals("Error retrieving roles from auth0", exception.getMessage());
    }

    private static void initUser2Stub() {
        stubFor(any(urlPathEqualTo("/api/v2/users/2/roles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user2.json")));
    }

    private static void initUser1Stub() {
        stubFor(any(urlPathEqualTo("/api/v2/users/1/roles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("user1.json")));
    }

    private static void initRolesStub() {
        stubFor(any(urlPathEqualTo("/api/v2/roles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("roles.json")));
    }

    private static void initUsersStub() {
        stubFor(any(urlPathEqualTo("/api/v2/users"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("users.json")));
    }

    @TestConfiguration
    static class Auth0TestConfiguration {

        @Bean
        @Primary
        public Auth0HttpClient auth0HttpClient() throws NoSuchAlgorithmException, KeyManagementException {
            DefaultHttpClient auth0HttpClient = DefaultHttpClient.newBuilder().build();

            // Disable SSL validation (not recommended for production)
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .connectTimeout(Duration.ofMillis(500))
                    .readTimeout(Duration.ofMillis(500))
                    .build();
            ReflectionTestUtils.setField(auth0HttpClient, "client", okHttpClient);
            return auth0HttpClient;
        }
    }
}
