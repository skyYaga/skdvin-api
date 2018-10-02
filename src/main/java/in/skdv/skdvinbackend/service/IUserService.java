package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.entity.User;

public interface IUserService {
    User registerNewUser(User user) throws EmailExistsException;

    boolean hasVerificationToken(String token);

    User confirmRegistration(String token) throws TokenExpiredException;

}
