package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.dto.PasswordDto;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.util.GenericResult;

import javax.mail.MessagingException;

public interface IUserService {
    User registerNewUser(User user) throws EmailExistsException, MessagingException;

    boolean hasVerificationToken(String token);

    User confirmRegistration(String token) throws TokenExpiredException;

    User findUserByEmail(String email);

    GenericResult<User> sendPasswordResetToken(User user);

    GenericResult<User> validatePasswordResetToken(String token);

    GenericResult changePassword(User user, PasswordDto passwordDto);
}
