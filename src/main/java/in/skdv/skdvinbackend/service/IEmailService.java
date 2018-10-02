package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.User;

public interface IEmailService {
    void sendUserRegistrationToken(User user);
}
