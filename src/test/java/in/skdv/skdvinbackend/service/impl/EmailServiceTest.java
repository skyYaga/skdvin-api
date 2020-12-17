package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.Dropzone;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class EmailServiceTest extends AbstractSkdvinTest {

    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BCC_EMAIL = "dz@example.com";
    private static final String REPLYTO_EMAIL = "reply@example.com";
    private static final String BASE_URL = "https://example.com";

    private IEmailService emailService;
    private JavaMailSender mailSender;

    @Mock
    private ISettingsService settingsService;

    @Autowired
    private TemplateEngine emailTemplateEngine;

    @Autowired
    private MessageSource emailMessageSource;

    @BeforeEach
    public void setup() {
        Mockito.reset(settingsService);

        mailSender = spy(new JavaMailSenderImpl());
        emailService = new EmailService(settingsService, mailSender, emailTemplateEngine, emailMessageSource);
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);

        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).
                thenReturn(ModelMockHelper.createCommonSettings());
    }

    @Test
    public void testAppointmentVerificationMail() throws MessagingException, IOException {
        LocaleContextHolder.setLocale(Locale.GERMAN);
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());

        emailService.sendAppointmentVerification(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        Pattern pattern = Pattern.compile(".*" + BASE_URL + "/de/appointment/verify\\?id=[0-9]+&token=[A-Za-z0-9-]{36}.*", Pattern.DOTALL);
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

    @Test
    public void testAppointmentUnconfirmedCancellationMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();

        emailService.sendAppointmentUnconfirmedCancellation(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        assertFalse(argument.getValue().getContent().toString().isEmpty());
    }

    @Test
    public void testAppointmentUpdatedMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();

        emailService.sendAppointmentUpdated(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        assertFalse(argument.getValue().getContent().toString().isEmpty());
    }

    @Test
    public void testAppointmentDeletedMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();

        emailService.sendAppointmentDeleted(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        assertFalse(argument.getValue().getContent().toString().isEmpty());
    }

    @Test
    public void testAppointmentReminderMail() throws MessagingException, IOException {
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();

        emailService.sendAppointmentReminder(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        assertEquals(FROM_EMAIL, argument.getValue().getFrom()[0].toString());
        assertEquals(appointment.getCustomer().getEmail(), argument.getValue().getAllRecipients()[0].toString());

        assertFalse(argument.getValue().getContent().toString().isEmpty());
    }

    @Test
    public void testBccAndReplyToMail() throws MessagingException {
        CommonSettings commonSettings = new CommonSettings();
        commonSettings.setBccMail(BCC_EMAIL);
        Dropzone dropzone = new Dropzone();
        dropzone.setEmail(REPLYTO_EMAIL);
        commonSettings.setDropzone(dropzone);
        LocaleContextHolder.setLocale(Locale.GERMAN);
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).
                thenReturn(commonSettings);

        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());

        emailService.sendAppointmentVerification(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        MimeMessage mimeMessage = argument.getValue();
        assertEquals(REPLYTO_EMAIL, mimeMessage.getReplyTo()[0].toString());
        assertEquals(BCC_EMAIL, mimeMessage.getHeader("Bcc")[0]);
    }

    @Test
    public void testNoBccAndReplyToMail() throws MessagingException {
        CommonSettings settings = ModelMockHelper.createCommonSettings();
        settings.getDropzone().setEmail("");
        settings.setBccMail("");
        when(settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage())).
                thenReturn(settings);

        LocaleContextHolder.setLocale(Locale.GERMAN);
        doNothing().when(mailSender).send(Mockito.any(MimeMessage.class));

        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());

        emailService.sendAppointmentVerification(appointment);

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());

        MimeMessage mimeMessage = argument.getValue();
        assertEquals(FROM_EMAIL, mimeMessage.getReplyTo()[0].toString());
        assertNull(mimeMessage.getHeader("Bcc"));
    }
}
