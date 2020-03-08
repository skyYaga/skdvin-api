package in.skdv.skdvinbackend.util;

import in.skdv.skdvinbackend.model.entity.VerificationToken;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class VerificationTokenUtilTest {

    @Test
    public void testGenerateToken() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
    }

    @Test
    public void testTokenIsNotExpired() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertTrue(VerificationTokenUtil.isNotExpired(token));
    }

    @Test
    public void testTokenIsExpired() {
        VerificationToken token = VerificationTokenUtil.generate();
        token.setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        assertFalse(VerificationTokenUtil.isNotExpired(token));
    }

    @Test
    public void testTokenMatches() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertTrue(VerificationTokenUtil.matches(token.getToken(), token));
    }

    @Test
    public void testTokenMatchesNot() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertFalse(VerificationTokenUtil.matches("foo", token));
    }

    @Test
    public void testTokenIsValid() {
        VerificationToken token = VerificationTokenUtil.generate();
        assertTrue(VerificationTokenUtil.isValid(token.getToken(), token));
    }
}
