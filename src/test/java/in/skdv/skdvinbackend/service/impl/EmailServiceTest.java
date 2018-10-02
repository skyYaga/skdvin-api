package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.service.IEmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("junit")
public class EmailServiceTest {

    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    private IEmailService emailService;
    private JavaMailSender mailSender;

    @Before
    public void setup() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);
    }

    @Test
    public void test() {
        User user = ModelMockHelper.createUserWithVerificationToken();
        emailService.sendUserRegistrationToken(user);

        ArgumentCaptor<SimpleMailMessage> argument = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom());
        assertEquals(user.getEmail(), Objects.requireNonNull(argument.getValue().getTo())[0]);
        assertTrue(Pattern.matches("Hi max!\n" +
                "Go to " + BASE_URL + "/api/user/confirm/.{36} to confirm registration.",
                argument.getValue().getText()));
    }
}
