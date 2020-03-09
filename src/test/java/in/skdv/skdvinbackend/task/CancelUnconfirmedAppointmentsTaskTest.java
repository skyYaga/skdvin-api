package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.util.VerificationTokenUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CancelUnconfirmedAppointmentsTaskTest extends AbstractSkdvinTest {
    private static final String FROM_EMAIL = "skdvin@example.com";
    private static final String BASE_URL = "https://example.com";

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

    @Before
    public void setup() {
        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());

        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "baseurl", BASE_URL);
        doReturn(new JavaMailSenderImpl().createMimeMessage()).when(mailSender).createMimeMessage();
    }

    @Test
    public void testAppointmentIsCanceled() {
        Appointment appointment1 = ModelMockHelper.createSingleAppointment();
        Appointment appointment2 = ModelMockHelper.createSecondAppointment();
        appointment1.setVerificationToken(VerificationTokenUtil.generate());
        appointment1.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointmentService.saveAppointment(appointment1);
        appointmentService.saveAppointment(appointment2);

        task.cancelAppointments();

        List<Appointment> result = appointmentService.findAppointmentsByDay(LocalDate.now());
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testEmailIsSend() throws MessagingException {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Appointment appointment = ModelMockHelper.createSingleAppointment();
        appointment.setVerificationToken(VerificationTokenUtil.generate());
        appointment.getVerificationToken().setExpiryDate(LocalDateTime.now().minus(25, ChronoUnit.HOURS));
        appointmentService.saveAppointment(appointment);

        task.cancelAppointments();

        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(argument.capture());
        assertEquals(argument.getValue().getSubject(), "CANCELLATION of booking #" + appointment.getAppointmentId());
    }
}
