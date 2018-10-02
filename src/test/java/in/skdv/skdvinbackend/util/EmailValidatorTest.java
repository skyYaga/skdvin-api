package in.skdv.skdvinbackend.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmailValidatorTest {

    private EmailValidator validator = new EmailValidator();

    @Test
    public void testValidEmail() {
        assertTrue(validator.isValid("test@example.com", null));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(validator.isValid("test", null));
    }

}
