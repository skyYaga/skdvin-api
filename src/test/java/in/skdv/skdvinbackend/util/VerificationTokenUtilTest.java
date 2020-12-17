package in.skdv.skdvinbackend.util;

import in.skdv.skdvinbackend.model.entity.VerificationToken;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;


class VerificationTokenUtilTest {

    @Test
    void testGenerateToken() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
    }

    @Test
    void testTokenIsNotExpired() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertTrue(VerificationTokenUtil.isNotExpired(token));
    }

    @Test
    void testTokenIsExpired() {
        VerificationToken token = VerificationTokenUtil.generate();
        token.setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        assertFalse(VerificationTokenUtil.isNotExpired(token));
    }

    @Test
    void testTokenMatches() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertTrue(VerificationTokenUtil.matches(token.getToken(), token));
    }

    @Test
    void testTokenMatchesNot() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertFalse(VerificationTokenUtil.matches("foo", token));
    }

    @Test
    void testTokenIsValid() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertTrue(VerificationTokenUtil.isValid(token.getToken(), token));
    }
}
