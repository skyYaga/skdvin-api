package in.skdv.skdvinbackend;

import com.auth0.exception.Auth0Exception;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.ZoneId;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Abstract Base Class for tests that sets default settings.
 */
@Testcontainers
@TestPropertySource(properties = {
        "auth0.audience=http://localhost:${wiremock.server.port}",
        "auth0.management.domain=localhost:${wiremock.server.port}",
        "auth0.management.audience=http://${auth0.management.domain}/api/v2/",
        "auth0.management.client-id=foo",
        "auth0.management.client-secret=foo",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com"
})
@AutoConfigureWireMock(port = 0)
// Needed to use MongoDB Testcontainer with DynamicPropertySource
@DirtiesContext
public abstract class AbstractSkdvinTest {

    protected final ZoneId zoneId = ZoneId.of("Europe/Berlin");

    @MockBean
    protected JwtDecoder jwtDecoder;

    @Container
    public static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:5.0"));

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }

    @BeforeEach
    void setupMocks() throws Auth0Exception {
        //stubFor(any(urlEqualTo("/oauth/token"))
        stubFor(any(anyUrl())
                .willReturn(aResponse().withBody(
                        "{\"access_token\": \"foo\",\"scope\":\"read:users update:users read:roles\",\"expires_in\":86400,\"token_type\":\"Bearer\"}"
                ))
        );

        /*TokenRequest authRequest = Mockito.mock(TokenRequest.class);
        TokenHolder tokenHolder = new TokenHolder();
        ReflectionTestUtils.setField(tokenHolder, "accessToken", "foo");
        Mockito.when(authAPI.requestToken(Mockito.anyString())).thenReturn(authRequest);
        Mockito.when(authRequest.execute()).thenReturn(tokenHolder);*/

        Mockito.when(jwtDecoder.decode(Mockito.anyString()))
                .thenAnswer((Answer<Jwt>) invocation -> MockJwtDecoder.decode(invocation.getArgument(0)));
    }

}
