package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.Role;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.repository.UserRepository;
import in.skdv.skdvinbackend.util.EmailExistsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

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
                "user@example.com", Collections.singletonList(Role.ROLE_USER));
        User savedUser = userDetailsService.registerNewUser(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.get_id());
        assertEquals(user.getUsername(), savedUser.getUsername());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getRoles(), savedUser.getRoles());
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
            Assert.assertEquals(e.getLocalizedMessage(), "There is already an account with email: user@example.com");
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

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadNotExistingUser() {
        userDetailsService.loadUserByUsername("notExisting");
    }
}
