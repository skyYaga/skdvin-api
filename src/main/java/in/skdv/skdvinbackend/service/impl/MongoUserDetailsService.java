package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.model.entity.VerificationToken;
import in.skdv.skdvinbackend.repository.UserRepository;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MongoUserDetailsService implements UserDetailsService, IUserService {

    private static final int EXPIRATION_HOURS = 24;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmailService emailService;

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
                user.getUsername(), user.getPassword(), authorities);
    }


    @Override
    public User registerNewUser(User user) throws EmailExistsException {
        if (emaiExist(user.getEmail())) {
            throw new EmailExistsException("There is already an account with email: " + user.getEmail());
        }

        user.setVerificationToken(generateVerificationToken());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        emailService.sendUserRegistrationToken(savedUser);

        return savedUser;
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
