package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendAppointmentReminderTask {

    private final IAppointmentService appointmentService;
    private final IEmailService emailService;

    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60) // every 30 minutes
    @ConditionalOnProperty(
            value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
    )
    public void sendReminder() {
        List<Appointment> nextWeeksAppointments = appointmentService.findAppointmentsWithinNextWeek();
        nextWeeksAppointments.stream()
                .filter(a -> !a.isReminderSent() && a.getState() != AppointmentState.UNCONFIRMED)
                .forEach(appointment -> {
                    try {
                        log.info("Sending AppintmentReminder for appointment {}", appointment.getAppointmentId());
                        emailService.sendAppointmentReminder(appointment);
                        appointmentService.reminderSent(appointment);
                    } catch (MessagingException e) {
                        log.error("Error sending AppointmentReminder", e);
                    }
                });
    }
}
