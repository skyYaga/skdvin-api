package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.Appointment;
import in.skdv.skdvinbackend.service.IEmailService;
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
import java.util.Locale;

public class EmailService implements IEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private static final String APPOINTMENT_CONFIRMATION_ENDPOINT = "/api/appointment/confirm/";

    private JavaMailSender mailSender;
    private TemplateEngine emailTemplateEngine;
    private MessageSource emailMessageSource;

    @Value("${skdvin.from}")
    private String fromEmail;

    @Value("${skdvin.baseurl}")
    private String baseurl;


    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine emailTemplateEngine, @Qualifier("emailMessageSource") MessageSource emailMessageSource) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.emailMessageSource = emailMessageSource;
    }

    @Override
    public void sendAppointmentVerification(Appointment appointment) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String toEmail = appointment.getCustomer().getEmail();

        Context ctx = new Context(locale);
        ctx.setVariable("appointment", appointment);
        ctx.setVariable("tokenurl", baseurl + APPOINTMENT_CONFIRMATION_ENDPOINT + appointment.getVerificationToken().getToken());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

        message.setSubject(emailMessageSource.getMessage("appointment.verification.subject", new Object[]{appointment.getAppointmentId()}, locale));
        message.setFrom(fromEmail);
        message.setTo(toEmail);

        String htmlContent = emailTemplateEngine.process("html/appointment-verification", ctx);
        message.setText(htmlContent, true);

        LOG.info("Sending appointment verification mail to {}", toEmail);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String toEmail = appointment.getCustomer().getEmail();

        Context ctx = new Context(locale);
        ctx.setVariable("appointment", appointment);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

        message.setSubject(emailMessageSource.getMessage("appointment.confirmation.subject", new Object[]{appointment.getAppointmentId()}, locale));
        message.setFrom(fromEmail);
        message.setTo(toEmail);

        String htmlContent = emailTemplateEngine.process("html/appointment-confirmation", ctx);
        message.setText(htmlContent, true);

        LOG.info("Sending appointment confirmation mail to {}", toEmail);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAppointmentUnconfirmedCancellation(Appointment appointment) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String toEmail = appointment.getCustomer().getEmail();

        Context ctx = new Context(locale);
        ctx.setVariable("appointment", appointment);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

        message.setSubject(emailMessageSource.getMessage("appointment.unconfirmed.cancellation.subject", new Object[]{appointment.getAppointmentId()}, locale));
        message.setFrom(fromEmail);
        message.setTo(toEmail);

        String htmlContent = emailTemplateEngine.process("html/appointment-unconfirmed-cancellation", ctx);
        message.setText(htmlContent, true);

        LOG.info("Sending appointment unconfirmed cancellation mail to {}", toEmail);

        mailSender.send(mimeMessage);
    }
}
