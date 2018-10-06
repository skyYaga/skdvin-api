package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.ModelMockHelper;
import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.dto.PasswordDto;
import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.repository.UserRepository;
import in.skdv.skdvinbackend.service.IEmailService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoUserDetailsServiceTest {

    @MockBean
    private IEmailService emailService;

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
    public void testCreateUser() throws EmailExistsException, MessagingException {
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

        assertFalse(userDetailsService.loadUserByUsername(user.getUsername()).isEnabled());
    }

    @Test
    public void testCreateUser_UserExists() throws EmailExistsException, MessagingException {
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
    public void testLoadUser() throws EmailExistsException, MessagingException {
        User user = new User("user","password",
                "user@example.com", Collections.singletonList(Role.ROLE_USER));
        User savedUser = userDetailsService.registerNewUser(user);

        UserDetails loadedUser = userDetailsService.loadUserByUsername("user");
        assertNotNull(loadedUser);
    }

    @Test
    public void testHasVerificationToken() throws EmailExistsException, MessagingException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);

        assertTrue(userDetailsService.hasVerificationToken(savedUser.getVerificationToken().getToken()));
    }

    @Test
    public void testHasVerificationToken_NotFound() {
        assertFalse(userDetailsService.hasVerificationToken("footoken"));
    }

    @Test
    public void testConfirmRegistration() throws EmailExistsException, TokenExpiredException, MessagingException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);
        assertFalse(savedUser.isEnabled());

        User verifiedUser = userDetailsService.confirmRegistration(savedUser.getVerificationToken().getToken());
        assertNull(verifiedUser.getVerificationToken());
        assertTrue(verifiedUser.isEnabled());
    }

    @Test
    public void testConfirmRegistration_TokenExpired() throws EmailExistsException, MessagingException {
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

    @Test
    public void testFindByEmail() throws EmailExistsException, MessagingException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);

        User foundUser = userDetailsService.findUserByEmail(savedUser.getEmail());
        assertNotNull(foundUser);
    }

    @Test
    public void testFindByEmail_NotExisting() {
        User foundUser = userDetailsService.findUserByEmail("foo@example.com");
        assertNull(foundUser);
    }

    @Test
    public void testSendPasswordResetToken() throws EmailExistsException, MessagingException {
        User user = ModelMockHelper.createUser();
        User savedUser = userDetailsService.registerNewUser(user);

        GenericResult<User> resetUser = userDetailsService.sendPasswordResetToken(user);
        assertNotNull("PasswordResetToken should not be null", resetUser.getPayload().getPasswordResetToken());
        assertNotNull("Token should not be null", resetUser.getPayload().getPasswordResetToken().getToken());
        assertTrue("Token expiration date should be in the future", savedUser.getPasswordResetToken().getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testValidatePasswordResetToken() {
        User user = ModelMockHelper.createUser();
        GenericResult<User> userWithPwToken = userDetailsService.sendPasswordResetToken(user);

        GenericResult<User> result = userDetailsService.validatePasswordResetToken(userWithPwToken.getPayload().getPasswordResetToken().getToken());

        assertTrue(result.isSuccess());
        assertNotNull(result.getPayload());
    }

    @Test
    public void testValidatePasswordResetToken_NotFound() {
        GenericResult<User> result = userDetailsService.validatePasswordResetToken("footoken");

        assertFalse(result.isSuccess());
        assertEquals("user.token.notfound", result.getMessage());
    }

    @Test
    public void testValidatePasswordResetToken_TokenExpired() {
        User user = ModelMockHelper.createUser();
        GenericResult<User> result = userDetailsService.sendPasswordResetToken(user);
        User userWithPwToken = result.getPayload();
        userWithPwToken.getPasswordResetToken().setExpiryDate(LocalDateTime.now().minusHours(1));
        userRepository.save(userWithPwToken);

        result = userDetailsService.validatePasswordResetToken(userWithPwToken.getPasswordResetToken().getToken());

        assertFalse(result.isSuccess());
        assertEquals("user.token.expired", result.getMessage());
    }

    @Test
    public void testChangePassword() {
        String newPassword = "foo";
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setNewPassword(newPassword);

        User user = ModelMockHelper.createUser();

        String oldPassword = user.getPassword();

        GenericResult result = userDetailsService.changePassword(user, passwordDto);
        assertTrue(result.isSuccess());
        assertEquals("user.changepassword.successful", result.getMessage());

        User updatedUser = userRepository.findByUsername(user.getUsername());

        // The new password should be encoded
        assertNotEquals(newPassword, updatedUser.getPassword());
        // The hash of the new password shouldn't match the old one
        assertNotEquals(oldPassword, updatedUser.getPassword());
    }
}
