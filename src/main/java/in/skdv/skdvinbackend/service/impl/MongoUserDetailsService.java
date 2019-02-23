package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.dto.PasswordDto;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.model.entity.VerificationToken;
import in.skdv.skdvinbackend.repository.UserRepository;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MongoUserDetailsService implements UserDetailsService, IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoUserDetailsService.class);

    private static final int EXPIRATION_HOURS = 24;

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private IEmailService emailService;

    @Autowired
    public MongoUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder, IEmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(String.valueOf(r))));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User registerNewUser(User user) throws EmailExistsException, MessagingException {
        if (emaiExist(user.getEmail())) {
            throw new EmailExistsException("There is already an account with email: " + user.getEmail());
        }

        user.setVerificationToken(generateVerificationToken());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        emailService.sendUserRegistrationToken(savedUser);

        return savedUser;
    }

    @Override
    public boolean hasVerificationToken(String token) {
        User user = userRepository.findByVerificationTokenToken(token);
        return user != null;
    }

    @Override
    public User confirmRegistration(String token) throws TokenExpiredException {
        User user = userRepository.findByVerificationTokenToken(token);
        if (user != null && user.getVerificationToken().getExpiryDate().isAfter(LocalDateTime.now())) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            return userRepository.save(user);
        } else {
            throw new TokenExpiredException("This token is expired or invalid: " + token);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public GenericResult<User> sendPasswordResetToken(User user) {
        user.setPasswordResetToken(generateVerificationToken());
        User savedUser = userRepository.save(user);

        try {
            emailService.sendPasswordResetToken(savedUser);
            GenericResult<User> result = new GenericResult<>(true, "user.resetPassword");
            result.setPayload(savedUser);
            return result;
        } catch (MessagingException e) {
            LOGGER.error("Error sending Password Reset Token: ", e);
            return new GenericResult<>(false, ErrorMessage.USER_RESET_PASSWORD_FAILED, e);
        }
    }

    @Override
    public GenericResult<User> validatePasswordResetToken(String token) {
        User user = userRepository.findByPasswordResetTokenToken(token);

        if (user != null) {

            if (user.getPasswordResetToken().getExpiryDate().isAfter(LocalDateTime.now())) {
                return new GenericResult<>(true, user);
            }

            return new GenericResult<>(false, ErrorMessage.USER_TOKEN_EXPIRED);
        }
        return new GenericResult<>(false, ErrorMessage.USER_TOKEN_NOT_FOUND);
    }

    @Override
    public GenericResult changePassword(User user, PasswordDto passwordDto) {
        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
        return new GenericResult(true, "user.changepassword.successful");
    }

    private VerificationToken generateVerificationToken() {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS));
        return verificationToken;
    }

    private boolean emaiExist(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

}
