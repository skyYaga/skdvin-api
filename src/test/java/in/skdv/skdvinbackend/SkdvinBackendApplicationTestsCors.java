package in.skdv.skdvinbackend;

import in.skdv.skdvinbackend.config.ApplicationConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkdvinBackendApplicationTestsCors {

    @Test
    public void testCorsMapping() {
        String origin = "http://localhost:8080";
        ApplicationConfig config = new ApplicationConfig();
		ReflectionTestUtils.setField(config, "corsEnabled", true);
		ReflectionTestUtils.setField(config, "corsUrl", origin);
        CorsRegistry corsRegistry = new CorsRegistry();

        config.addCorsMappings(corsRegistry);

        List<CorsRegistration> registrations = (List<CorsRegistration>) ReflectionTestUtils.getField(corsRegistry, "registrations");

        Assert.assertEquals("/**", ReflectionTestUtils.getField(registrations.get(0), "pathPattern"));
        Assert.assertEquals(Collections.singletonList(origin),
                ((CorsConfiguration) ReflectionTestUtils.getField(registrations.get(0), "config")).getAllowedOrigins());
        Assert.assertEquals(Arrays.asList(HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name()),
                ((CorsConfiguration) ReflectionTestUtils.getField(registrations.get(0), "config")).getAllowedMethods());
    }

}
