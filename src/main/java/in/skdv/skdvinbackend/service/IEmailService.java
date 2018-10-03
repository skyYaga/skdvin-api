package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.User;

import javax.mail.MessagingException;

public interface IEmailService {

    void sendUserRegistrationToken(User user) throws MessagingException;

    void sendPasswordResetToken(User user) throws MessagingException;

}
