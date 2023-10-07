package in.skdv.skdvinbackend;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.ZoneId;

/**
 * Abstract Base Class for tests that sets default settings.
 */
@Testcontainers
@TestPropertySource(properties = {
        "auth0.audience=https://localhost:6443",
        "auth0.management.domain=localhost:6443",
        "auth0.management.audience=http://${auth0.management.domain}/api/v2/",
        "auth0.management.client-id=foo",
        "auth0.management.client-secret=foo",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com"
})
@AutoConfigureWireMock(httpsPort = 6443)
public abstract class AbstractSkdvinTest {

    protected final ZoneId zoneId = ZoneId.of("Europe/Berlin");

    @MockBean
    protected JwtDecoder jwtDecoder;

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDb = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @BeforeEach
    void setupMocks() {
        Mockito.when(jwtDecoder.decode(Mockito.anyString()))
                .thenAnswer((Answer<Jwt>) invocation -> MockJwtDecoder.decode(invocation.getArgument(0)));
    }

}
