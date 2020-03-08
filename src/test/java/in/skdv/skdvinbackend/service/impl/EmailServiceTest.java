package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest extends AbstractSkdvinTest {

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
    public void testAppointmentVerificationMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());

        emailService.sendAppointmentVerification(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        Pattern pattern = Pattern.compile(".*" + BASE_URL + "/api/appointment/confirm/[A-Za-z0-9-]{36}.*", Pattern.DOTALL);
        assertTrue(pattern.matcher(argument.getValue().getContent().toString()).matches());
    }

    @Test
    public void testAppointmentConfirmationMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());

        emailService.sendAppointmentConfirmation(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        assertFalse(argument.getValue().getContent().toString().isEmpty());
    }
}
