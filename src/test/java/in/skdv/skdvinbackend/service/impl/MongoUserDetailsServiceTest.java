package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoUserDetailsServiceTest {

    @Autowired
    private MongoUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Before
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser() throws EmailExistsException {
        User user = new User("user","password",
                "andy.skydiver@gmail.com", Collections.singletonList(Role.ROLE_USER));
        User savedUser = userDetailsService.registerNewUser(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.get_id());
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getRoles(), savedUser.getRoles());
        assertFalse(savedUser.isEnabled());
        assertNotNull("VerificationToken should not be null", savedUser.getVerificationToken());
        assertNotNull("Token should not be null", savedUser.getVerificationToken().getToken());
        assertTrue("Token expiration date should be in the future", savedUser.getVerificationToken().getExpiryDate().isAfter(LocalDateTime.now()));
        assertTrue(passwordEncoder.matches("password", savedUser.getPassword()));
    }

    @Test
    public void testCreateUser_UserExists() throws EmailExistsException {
        User user = new User("user","password",
                "user@example.com", Collections.singletonList(Role.ROLE_USER));
        User savedUser = userDetailsService.registerNewUser(user);

        assertNotNull(savedUser);

        try {
            userDetailsService.registerNewUser(user);
        } catch (EmailExistsException e) {
            assertEquals("There is already an account with email: user@example.com", e.getLocalizedMessage());
        }
    }

    @Test
    public void testLoadUser() throws EmailExistsException {
        User user = new User("user","password",
                "user@example.com", Collections.singletonList(Role.ROLE_USER));
        User savedUser = userDetailsService.registerNewUser(user);

        UserDetails loadedUser = userDetailsService.loadUserByUsername("user");
        assertNotNull(loadedUser);
    }

    @Test
    public void testHasVerificationToken() throws EmailExistsException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);

        assertTrue(userDetailsService.hasVerificationToken(savedUser.getVerificationToken().getToken()));
    }

    @Test
    public void testHasVerificationToken_NotFound() {
        assertFalse(userDetailsService.hasVerificationToken("footoken"));
    }

    @Test
    public void testConfirmRegistration() throws EmailExistsException, TokenExpiredException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);
        assertFalse(savedUser.isEnabled());

        User verifiedUser = userDetailsService.confirmRegistration(savedUser.getVerificationToken().getToken());
        assertNull(verifiedUser.getVerificationToken());
        assertTrue(verifiedUser.isEnabled());
    }

    @Test
    public void testConfirmRegistration_TokenExpired() throws EmailExistsException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);
        savedUser.getVerificationToken().setExpiryDate(LocalDateTime.now().minusHours(1));
        userRepository.save(savedUser);

        try {
            userDetailsService.confirmRegistration(savedUser.getVerificationToken().getToken());
        } catch (TokenExpiredException e) {
            assertEquals("This token is expired or invalid: " + savedUser.getVerificationToken().getToken(),
                    e.getLocalizedMessage());
        }
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadNotExistingUser() {
        userDetailsService.loadUserByUsername("notExisting");
    }
}
