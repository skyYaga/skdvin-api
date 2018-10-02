package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.service.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService implements IEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private static final String USER_REGISTRATION_ENDPOINT = "/api/user/confirm/";

    private JavaMailSender mailSender;

    @Value("${skdvin.from}")
    private String fromEmail;

    @Value("${skdvin.baseurl}")
    private String baseurl;

    @Autowired
    EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendUserRegistrationToken(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom(fromEmail);
        message.setSubject("Please confirm Registration");
        message.setText("Hi " + user.getUsername() + "!\n"
        + "Go to " + baseurl + USER_REGISTRATION_ENDPOINT + user.getVerificationToken().getToken() +
                " to confirm registration.");

        LOG.info("Sending user registration mail to " + Arrays.toString(message.getTo()));
        try {
            mailSender.send(message);
        } catch (MailException e) {
            LOG.error("Error sending mail: " + e);
        }
    }
}
