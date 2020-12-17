package in.skdv.skdvinbackend.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AudienceValidatorTest {

    private Jwt jwt = createJwt();

    @Test
    public void testValidator_Valid() {
        AudienceValidator audienceValidator = new AudienceValidator("foo");
        assertFalse(audienceValidator.validate(jwt).hasErrors());
    }

    @Test
    public void testValidator_Invalid() {
        AudienceValidator audienceValidator = new AudienceValidator("bar");
        assertTrue(audienceValidator.validate(jwt).hasErrors());
    }

    private Jwt createJwt() {
        return Jwt.withTokenValue("token")
                .audience(Collections.singletonList("foo"))
                .header("header", "test")
                .build();
    }
}
