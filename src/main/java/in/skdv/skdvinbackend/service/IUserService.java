package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.util.EmailExistsException;

public interface IUserService {
    User registerNewUser(User user) throws EmailExistsException;
}
