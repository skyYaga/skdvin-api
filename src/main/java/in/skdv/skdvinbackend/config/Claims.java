package in.skdv.skdvinbackend.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class Claims {

    private Claims() {
        throw new IllegalStateException("Utility class that should not be instantiated.");
    }

    public static final String EMAIL = "https://skdv.in/email";

    public static String getEmail() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) token.getPrincipal();
        return (String) jwt.getClaims().get(EMAIL);
    }
}
