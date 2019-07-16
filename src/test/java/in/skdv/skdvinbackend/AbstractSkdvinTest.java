package in.skdv.skdvinbackend;

import org.springframework.test.context.TestPropertySource;

/**
 * Abstract Base Class for tests that sets default settings.
 */
@TestPropertySource(properties = {
        "auth0.apiAudience=http://localhost",
        "auth0.issuer=https://example.com"
})
public abstract class AbstractSkdvinTest {
}
