package in.skdv.skdvinbackend.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;

public class AudienceValidatorTest {

    private Jwt jwt = createJwt();

    @Test
    public void testValidator_Valid() {
        AudienceValidator audienceValidator = new AudienceValidator("foo");
        Assert.assertFalse(audienceValidator.validate(jwt).hasErrors());
    }

    @Test
    public void testValidator_Invalid() {
        AudienceValidator audienceValidator = new AudienceValidator("bar");
        Assert.assertTrue(audienceValidator.validate(jwt).hasErrors());
    }

    private Jwt createJwt() {
        return Jwt.withTokenValue("token")
                .audience(Collections.singletonList("foo"))
                .header("header", "test")
                .build();
    }
}
