package in.skdv.skdvinbackend.util;

import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PasswordConstraintValidatorTest {

    private PasswordConstraintValidator validator = new PasswordConstraintValidator();

    @Test
    public void testValidPasswords() {
        assertTrue(validator.isValid("tet@e3xam", null));
    }

    @Test
    public void testInvalidPasswords() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        doReturn(builder).when(context).buildConstraintViolationWithTemplate(anyString());

        assertFalse(validator.isValid("test", context)); // too short
        assertFalse(validator.isValid("128/%$342", context)); // no alphabetical
        assertFalse(validator.isValid("testasdf3", context)); // no special
        assertFalse(validator.isValid("testasdf$", context)); // no digit
        assertFalse(validator.isValid("tes3 asdf$", context)); // whitespace
    }

}
