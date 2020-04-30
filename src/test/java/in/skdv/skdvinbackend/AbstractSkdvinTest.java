package in.skdv.skdvinbackend;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;

/**
 * Abstract Base Class for tests that sets default settings.
 */
@TestPropertySource(properties = {
        "auth0.audience=http://localhost",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com"
})
public abstract class AbstractSkdvinTest {

    @MockBean
    protected JwtDecoder jwtDecoder;

    @Before
    public void initJwtMock() {
        Mockito.when(jwtDecoder.decode(Mockito.anyString()))
                .thenAnswer((Answer<Jwt>) invocation -> MockJwtDecoder.decode(invocation.getArgument(0)));
    }

}
