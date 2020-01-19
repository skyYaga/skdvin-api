package in.skdv.skdvinbackend.config;

import org.junit.Test;
import org.mockito.Mockito;
import org.modelmapper.internal.util.Assert;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

public class SecurityConfigTest {

    @Test
    public void test() {
        SecurityConfig config = new SecurityConfig() {
            @Override
            JwtDecoder loadRemoteJwtDecoder() {
                return Mockito.mock(NimbusJwtDecoder.class);
            }
        };
        ReflectionTestUtils.setField(config, "issuer", "https://localhost");
        Assert.notNull(config.jwtDecoder());
    }
}
