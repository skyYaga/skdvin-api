package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.EmailType;
import in.skdv.skdvinbackend.model.entity.OutgoingMail;
import in.skdv.skdvinbackend.model.entity.Status;
import in.skdv.skdvinbackend.repository.EmailOutboxRepository;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailScheduler {

    private final IEmailService emailService;
    private final EmailOutboxRepository emailOutboxRepository;
    private final IAppointmentService appointmentService;

    @Scheduled(fixedDelay = 1000 * 10, initialDelay = 1000 * 30) // every 10 seconds
    @ConditionalOnProperty(
            value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
    )
    public void send() {
        List<OutgoingMail> outgoingMails = emailOutboxRepository.findByStatus(Status.OPEN);
        outgoingMails.forEach(this::sendMail);
    }

    void sendMail(OutgoingMail outgoingMail) {
        log.info("Sending {} for appointment {}", outgoingMail.getEmailType(), outgoingMail.getAppointmentId());

        try {
            Appointment appointment = null;
            if (outgoingMail.getEmailType() != EmailType.APPOINTMENT_DELETED) {
                appointment = appointmentService.findAppointment(outgoingMail.getAppointmentId());
            }

            switch (outgoingMail.getEmailType()) {
                case APPOINTMENT_CONFIRMATION -> emailService.sendAppointmentConfirmation(appointment);
                case APPOINTMENT_DELETED -> emailService.sendAppointmentDeleted(outgoingMail.getAppointment());
                case APPOINTMENT_REMINDER -> emailService.sendAppointmentReminder(appointment);
                case APPOINTMENT_UNCONFIRMED_CANCELLATION -> emailService.sendAppointmentUnconfirmedCancellation(appointment);
                case APPOINTMENT_UPDATED -> emailService.sendAppointmentUpdated(appointment);
                case APPOINTMENT_VERIFICATION -> emailService.sendAppointmentVerification(appointment);
            }

            outgoingMail.setStatus(Status.SENT);
            emailOutboxRepository.save(outgoingMail);
            log.info("Mail sent successfully: {} for appointment {}", outgoingMail.getEmailType(), outgoingMail.getAppointmentId());
        } catch (Exception e) {
            log.error("Exception sending {} for appointment {}", outgoingMail.getEmailType(), outgoingMail.getAppointmentId(), e);
            outgoingMail.setStatus(Status.FAILED);
            emailOutboxRepository.save(outgoingMail);
        }
    }

}
