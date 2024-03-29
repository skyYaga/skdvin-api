package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.EmailType;
import in.skdv.skdvinbackend.model.entity.OutgoingMail;
import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.model.entity.settings.LanguageSettings;
import in.skdv.skdvinbackend.model.entity.settings.Settings;
import in.skdv.skdvinbackend.repository.EmailOutboxRepository;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private static final String APPOINTMENT_CONFIRMATION_ENDPOINT = "/%s/appointment/verify?id=%d&token=%s";

    private final ISettingsService settingsService;
    private final EmailOutboxRepository emailOutboxRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;
    private final MessageSource emailMessageSource;
    private final ZoneId zoneId;

    @Value("${skdvin.from}")
    private String fromEmail;

    @Value("${skdvin.baseurl}")
    private String baseurl;


    @Override
    public void saveMailInOutbox(int appointmentId, EmailType emailType) {
        saveMailInOutbox(appointmentId, emailType, null);
    }

    @Override
    public void saveMailInOutbox(int appointmentId, EmailType emailType, Appointment appointment) {
        OutgoingMail outgoingMail = new OutgoingMail(emailType, appointmentId);
        outgoingMail.setAppointment(appointment);
        emailOutboxRepository.save(outgoingMail);
    }

    @Override
    public void sendAppointmentVerification(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.verification.subject";
        String template = "html/appointment-verification";

        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("tokenurl", baseurl + String.format(APPOINTMENT_CONFIRMATION_ENDPOINT,
                appointment.getLang(),
                appointment.getAppointmentId(),
                appointment.getVerificationToken().getToken()));

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, contextVariables);

        log.info("Sending appointment verification mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.confirmation.subject";
        String template = "html/appointment-confirmation";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        log.info("Sending appointment confirmation mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentUnconfirmedCancellation(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.unconfirmed.cancellation.subject";
        String template = "html/appointment-unconfirmed-cancellation";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        log.info("Sending appointment unconfirmed cancellation mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentUpdated(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.updated.subject";
        String template = "html/appointment-updated";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        log.info("Sending appointment updated mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentDeleted(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.deleted.subject";
        String template = "html/appointment-deleted";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        log.info("Sending appointment deleted mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentReminder(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.reminder.subject";
        String template = "html/appointment-reminder";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        log.info("Sending appointment reminder mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    private void prepareAppointmentMessage(MimeMessage mimeMessage, Appointment appointment, String subject, String template, Map<String, Object> contextVariables) throws MessagingException {
        Locale locale = new Locale(appointment.getLang());
        Settings settings = settingsService.getSettings();
        LanguageSettings languageSettings = settings.getLanguageSettingsByLocaleOrDefault(locale.getLanguage());
        AdminSettings adminSettings = settings.getAdminSettings();
        CommonSettings commonSettings = settings.getCommonSettings();

        Context ctx = new Context(locale);
        ctx.setVariable("appointment", appointment);
        ctx.setVariable("languageSettings", languageSettings);
        ctx.setVariable("commonSettings", commonSettings);
        ctx.setVariable("baseurl", baseurl);
        ctx.setVariable("zonedAppointmentDate", appointment.getDate().atZone(zoneId));

        if (contextVariables != null) {
            contextVariables.forEach(ctx::setVariable);
        }

        String toEmail = appointment.getCustomer().getEmail();
        String htmlContent = emailTemplateEngine.process(template, ctx);

        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(emailMessageSource.getMessage(subject, new Object[]{appointment.getAppointmentId()}, locale));
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setText(htmlContent, true);

        if (languageSettings != null && languageSettings.getDropzone() != null && !languageSettings.getDropzone().getEmail().isBlank()) {
            message.setReplyTo(languageSettings.getDropzone().getEmail());
        }
        if (adminSettings != null && !adminSettings.getBccMail().isBlank()) {
            message.setBcc(adminSettings.getBccMail());
        }
    }

}
