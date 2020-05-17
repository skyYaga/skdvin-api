package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.ISettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmailService implements IEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private static final String APPOINTMENT_CONFIRMATION_ENDPOINT = "/%s/appointment/verify?id=%d&token=%s";

    private ISettingsService settingsService;
    private JavaMailSender mailSender;
    private TemplateEngine emailTemplateEngine;
    private MessageSource emailMessageSource;

    @Value("${skdvin.from}")
    private String fromEmail;

    @Value("${skdvin.baseurl}")
    private String baseurl;


    @Autowired
    public EmailService(ISettingsService settingsService, JavaMailSender mailSender, TemplateEngine emailTemplateEngine, @Qualifier("emailMessageSource") MessageSource emailMessageSource) {
        this.settingsService = settingsService;
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.emailMessageSource = emailMessageSource;
    }

    @Override
    public void sendAppointmentVerification(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.verification.subject";
        String template = "html/appointment-verification";

        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("tokenurl", baseurl + String.format(APPOINTMENT_CONFIRMATION_ENDPOINT,
                LocaleContextHolder.getLocale().getLanguage(),
                appointment.getAppointmentId(),
                appointment.getVerificationToken().getToken()));

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, contextVariables);

        LOG.info("Sending appointment verification mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.confirmation.subject";
        String template = "html/appointment-confirmation";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        LOG.info("Sending appointment confirmation mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentUnconfirmedCancellation(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.unconfirmed.cancellation.subject";
        String template = "html/appointment-unconfirmed-cancellation";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        LOG.info("Sending appointment unconfirmed cancellation mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentUpdated(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.updated.subject";
        String template = "html/appointment-updated";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        LOG.info("Sending appointment updated mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentDeleted(Appointment appointment) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        String subject = "appointment.deleted.subject";
        String template = "html/appointment-deleted";

        prepareAppointmentMessage(mimeMessage, appointment, subject, template, null);

        LOG.info("Sending appointment deleted mail to {}", appointment.getCustomer().getEmail());

        mailSender.send(mimeMessage);
    }

    private void prepareAppointmentMessage(MimeMessage mimeMessage, Appointment appointment, String subject, String template, Map<String, Object> contextVariables) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        CommonSettings settings = settingsService.getCommonSettingsByLanguage(locale.getLanguage());

        Context ctx = new Context(locale);
        ctx.setVariable("appointment", appointment);
        ctx.setVariable("settings", settings);

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

        if (settings != null && settings.getDropzone() != null && !settings.getDropzone().getEmail().isBlank()) {
            message.setReplyTo(settings.getDropzone().getEmail());
        }
        if (settings != null && !settings.getBccMail().isBlank()) {
            message.setBcc(settings.getBccMail());
        }
    }

}
