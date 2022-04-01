package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class SendAppointmentReminderTaskTest extends AbstractSkdvinTest {
    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    @MockBean
    private ISettingsService settingsService;

    @Autowired
    private SendAppointmentReminderTask task;

    @Autowired
    private JumpdayRepository jumpdayRepository;

    @Autowired
    private IAppointmentService appointmentService;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private IEmailService emailService;

    @BeforeEach
    void setup() {
        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());

        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);
        doReturn(new JavaMailSenderImpl().createMimeMessage()).when(mailSender).createMimeMessage();

        when(settingsService.getCommonSettingsByLanguage(Mockito.anyString())).
                thenReturn(ModelMockHelper.createCommonSettings());
    }

    @Test
    void testNoReminderIfNotConfirmedOrAlreadySent() {
        Appointment appointment1 = ModelMockHelper.createSingleAppointment();
        Appointment appointment2 = ModelMockHelper.createSecondAppointment();
        appointment2.setState(AppointmentState.CONFIRMED);
        appointment2.setReminderSent(true);
        appointmentService.saveAppointment(appointment1);
        appointmentService.saveAppointment(appointment2);

        task.sendReminder();

        verifyNoInteractions(mailSender);
    }

    @Test
    void testEmailIsSend_EN() throws MessagingException {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setState(AppointmentState.CONFIRMED);
        appointmentService.saveAppointment(appointment);

        task.sendReminder();

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals("Appointment reminder (#" + appointment.getAppointmentId() + ")", argument.getValue().getSubject());
    }

    @Test
    void testEmailIsSend_DE() throws MessagingException {
        LocaleContextHolder.setLocale(Locale.GERMAN);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setState(AppointmentState.CONFIRMED);
        appointmentService.saveAppointment(appointment);

        task.sendReminder();

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals("Terminerinnerung (#" + appointment.getAppointmentId() + ")", argument.getValue().getSubject());
    }
}
