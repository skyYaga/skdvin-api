package in.skdv.skdvinbackend.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.util.ReflectionTestUtils;

class SecurityConfigTest {

    @Test
    void test() {
        SecurityConfig config = new SecurityConfig() {
            @Override
            JwtDecoder loadRemoteJwtDecoder() {
                return Mockito.mock(NimbusJwtDecoder.class);
            }
        };
        ReflectionTestUtils.setField(config, "issuer", "https://localhost");
        Assertions.assertNotNull(config.jwtDecoder());
    }
}
