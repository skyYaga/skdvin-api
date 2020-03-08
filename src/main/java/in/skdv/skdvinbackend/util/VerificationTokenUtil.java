package in.skdv.skdvinbackend.util;

import in.skdv.skdvinbackend.model.entity.VerificationToken;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class VerificationTokenUtil {

    private static final int EXPIRATION_HOURS = 24;

    public static VerificationToken generate() {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS));
        return verificationToken;
    }

    public static boolean isValid(String token1, VerificationToken token2) {
        return matches(token1, token2) && isNotExpired(token2);
    }

    public static boolean matches(String token1, VerificationToken token2) {
        return token1.equals(token2.getToken());
    }

    public static boolean isNotExpired(VerificationToken token) {
        return token.getExpiryDate().isAfter(LocalDateTime.now());
    }

}
