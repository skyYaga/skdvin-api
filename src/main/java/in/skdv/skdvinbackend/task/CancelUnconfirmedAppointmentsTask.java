package in.skdv.skdvinbackend.task;

import in.skdv.skdvinbackend.model.entity.Appointment;
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
public class CancelUnconfirmedAppointmentsTask {

    private final IAppointmentService appointmentService;
    private final IEmailService emailService;

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000 * 60) // every 10 minutes
    @ConditionalOnProperty(
            value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
    )
    public void cancelAppointments() {
        List<Appointment> unconfirmedAppointments = appointmentService.findUnconfirmedAppointments();
        unconfirmedAppointments.forEach(appointment -> {
            appointmentService.deleteAppointment(appointment.getAppointmentId());
            try {
                log.info("Sending unconfirmed cancellation for appointment {}", appointment.getAppointmentId());
                emailService.sendAppointmentUnconfirmedCancellation(appointment);
            } catch (Exception e) {
                log.error("Error sending AppointmentUnconfirmedCancellation", e);
            }
        });
    }

}
