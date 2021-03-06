package in.skdv.skdvinbackend;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Abstract Base Class for tests that sets default settings.
 */
@TestPropertySource(properties = {
        "auth0.audience=http://localhost",
        "auth0.management.domain=localhost",
        "auth0.management.client-id=foo",
        "auth0.management.client-secret=foo",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com"
})
public abstract class AbstractSkdvinTest {

    @MockBean
    protected JwtDecoder jwtDecoder;

    @MockBean
    public ManagementAPI managementAPI;

    @MockBean
    public AuthAPI authAPI;

    @BeforeEach
    void setupMocks() throws Auth0Exception {
        TokenRequest authRequest = Mockito.mock(TokenRequest.class);
        TokenHolder tokenHolder = new TokenHolder();
        ReflectionTestUtils.setField(tokenHolder, "accessToken", "foo");
        Mockito.when(authAPI.requestToken(Mockito.anyString())).thenReturn(authRequest);
        Mockito.when(authRequest.execute()).thenReturn(tokenHolder);

        Mockito.when(jwtDecoder.decode(Mockito.anyString()))
                .thenAnswer((Answer<Jwt>) invocation -> MockJwtDecoder.decode(invocation.getArgument(0)));
    }

}
