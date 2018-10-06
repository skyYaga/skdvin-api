package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.User;
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
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class EmailService implements IEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private static final String USER_REGISTRATION_ENDPOINT = "/api/user/confirm/";
    private static final String USER_PASSWORD_RESET_ENDPOINT = "/api/user/resetpassword/";

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
    public void sendUserRegistrationToken(User user) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String toEmail = user.getEmail();

        Context ctx = new Context(locale);
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("tokenurl", baseurl + USER_REGISTRATION_ENDPOINT + user.getVerificationToken().getToken());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

        message.setSubject(emailMessageSource.getMessage("user.registration.subject", null, locale));
        message.setFrom(fromEmail);
        message.setTo(toEmail);

        String htmlContent = emailTemplateEngine.process("html/user-registration", ctx);
        message.setText(htmlContent, true);

        LOG.info("Sending user registration mail to {0}", toEmail);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendPasswordResetToken(User user) throws MessagingException {
        Locale locale = LocaleContextHolder.getLocale();
        String toEmail = user.getEmail();

        Context ctx = new Context(locale);
        ctx.setVariable("username", user.getUsername());
        ctx.setVariable("tokenurl", baseurl + USER_PASSWORD_RESET_ENDPOINT + user.getPasswordResetToken().getToken());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

        message.setSubject(emailMessageSource.getMessage("user.passwordreset.subject", null, locale));
        message.setFrom(fromEmail);
        message.setTo(toEmail);

        String htmlContent = emailTemplateEngine.process("html/user-password-reset", ctx);
        message.setText(htmlContent, true);

        LOG.info("Sending password reset mail to {}", toEmail);

        mailSender.send(mimeMessage);
    }
}
