package in.skdv.skdvinbackend.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
