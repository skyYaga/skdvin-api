package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.EmailType;
import in.skdv.skdvinbackend.model.entity.OutgoingMail;
import in.skdv.skdvinbackend.model.entity.Status;
import in.skdv.skdvinbackend.repository.EmailOutboxRepository;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.impl.AppointmentService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class MailSchedulerTest extends AbstractSkdvinTest {

    @Autowired
    private MailScheduler scheduler;
    @Autowired
    private EmailOutboxRepository emailOutboxRepository;
    @Autowired
    private JumpdayRepository jumpdayRepository;
    @Autowired
    private AppointmentService appointmentService;
    @MockitoBean
    private IEmailService emailService;

    private Appointment appointment;

    @BeforeEach
    void setup() {
        jumpdayRepository.deleteAll();
        jumpdayRepository.save(ModelMockHelper.createJumpday());
        this.appointment = appointmentService.saveAppointment(ModelMockHelper.createSingleAppointment());
    }

    @ParameterizedTest
    @EnumSource(EmailType.class)
    void testSend(EmailType emailType) {
        emailOutboxRepository.deleteAll();
        emailOutboxRepository.save(new OutgoingMail(emailType, appointment.getAppointmentId()));

        scheduler.send();

        assertEquals(0, emailOutboxRepository.findByStatus(Status.OPEN).size());
        List<OutgoingMail> sentMails = emailOutboxRepository.findByStatus(Status.SENT);
        assertEquals(1, sentMails.size());
        assertEquals(emailType, sentMails.get(0).getEmailType());
    }

    @Test
    void testSend_StatusFailedOnException() throws MessagingException {
        doThrow(new MessagingException()).when(emailService).sendAppointmentConfirmation(any(Appointment.class));
        emailOutboxRepository.save(new OutgoingMail(EmailType.APPOINTMENT_CONFIRMATION, appointment.getAppointmentId()));

        scheduler.send();

        assertEquals(0, emailOutboxRepository.findByStatus(Status.OPEN).size());
        assertEquals(1, emailOutboxRepository.findByStatus(Status.FAILED).size());
    }
}
