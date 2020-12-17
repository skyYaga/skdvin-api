package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class CancelUnconfirmedAppointmentsTaskTest extends AbstractSkdvinTest {
    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

    @MockBean
    private ISettingsService settingsService;

    @Autowired
    private CancelUnconfirmedAppointmentsTask task;

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
    void testAppointmentIsCanceled() {
        Appointment appointment1 = ModelMockHelper.createSingleAppointment();
        Appointment appointment2 = ModelMockHelper.createSecondAppointment();
        appointment1.setVerificationToken(VerificationTokenUtil.generate());
        appointment1.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointmentService.saveAppointment(appointment1);
        appointmentService.saveAppointment(appointment2);

        task.cancelAppointments();

        List<Appointment> result = appointmentService.findAppointmentsByDay(LocalDate.now());
        assertEquals(1, result.size());
    }

    @Test
    void testEmailIsSend_EN() throws MessagingException {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setLang(Locale.ENGLISH.getLanguage());
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointment.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointmentService.saveAppointment(appointment);

        task.cancelAppointments();

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals(argument.getValue().getSubject(), "CANCELLATION of booking #" + appointment.getAppointmentId());
    }

    @Test
    void testEmailIsSend_DE() throws MessagingException {
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setLang(Locale.GERMAN.getLanguage());
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointment.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointmentService.saveAppointment(appointment);

        task.cancelAppointments();

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals(argument.getValue().getSubject(), "STORNIERUNG Termin #" + appointment.getAppointmentId());
    }
}
