package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.service.IAppointmentService;
import in.skdv.skdvinbackend.service.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;

@Component
public class SendAppointmentReminderTask {

    private static final Logger LOG = LoggerFactory.getLogger(SendAppointmentReminderTask.class);

    private IAppointmentService appointmentService;
    private IEmailService emailService;

    @Autowired
    public SendAppointmentReminderTask(IAppointmentService appointmentService, IEmailService emailService) {
        this.appointmentService = appointmentService;
        this.emailService = emailService;
    }

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
                        emailService.sendAppointmentReminder(appointment);
                        appointmentService.reminderSent(appointment);
                    } catch (MessagingException e) {
                        LOG.error("Error sending AppointmentUnconfirmedCancellation", e);
                    }
                });
    }
}
