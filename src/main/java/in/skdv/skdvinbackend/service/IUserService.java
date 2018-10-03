package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.entity.User;

import javax.mail.MessagingException;

public interface IUserService {
    User registerNewUser(User user) throws EmailExistsException, MessagingException;

    boolean hasVerificationToken(String token);

    User confirmRegistration(String token) throws TokenExpiredException;

}
