package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.service.IEmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest {

    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    private IEmailService emailService;
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine emailTemplateEngine;

    @Autowired
    private MessageSource emailMessageSource;

    @Before
    public void setup() {
        mailSender = spy(new JavaMailSenderImpl());
        emailService = new EmailService(mailSender, emailTemplateEngine, emailMessageSource);
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);
    }

    @Test
    public void testRegistrationMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        User user = ModelMockHelper.createUserWithVerificationToken();
        emailService.sendUserRegistrationToken(user);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(user.getEmail(), argument.getValue().getAllRecipients()[0].toString());

        Pattern pattern = Pattern.compile(".*" + user.getUsername() +
                ".*" + BASE_URL + "/api/user/confirm/[A-Za-z0-9-]{36}.*", Pattern.DOTALL);
        assertTrue(pattern.matcher(argument.getValue().getContent().toString()).matches());
    }

    @Test
    public void testPasswordResetMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        User user = ModelMockHelper.createUserWithPasswordResetToken();
        emailService.sendPasswordResetToken(user);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(user.getEmail(), argument.getValue().getAllRecipients()[0].toString());

        Pattern pattern = Pattern.compile(".*" + user.getUsername() +
                ".*" + BASE_URL + "/api/user/resetpassword/[A-Za-z0-9-]{36}.*", Pattern.DOTALL);
        assertTrue(pattern.matcher(argument.getValue().getContent().toString()).matches());
    }
}
