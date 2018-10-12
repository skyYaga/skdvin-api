package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.EmailExistsException;
import in.skdv.skdvinbackend.exception.TokenExpiredException;
import in.skdv.skdvinbackend.model.dto.PasswordDto;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.model.dto.UserDtoIncoming;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private IUserService userService;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    UserDTO addUser(@RequestBody @Valid UserDtoIncoming input, HttpServletResponse response) {

        User user = null;
        response.setStatus(HttpServletResponse.SC_CREATED);

        try {
            user = userService.registerNewUser(convertToEntity(input));
        } catch (EmailExistsException e) {
            LOGGER.warn("E-Mail already exists", e);
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch (MessagingException e) {
            LOGGER.error("Error sending registration mail", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        // do not return pw
        return convertToDto(user);
    }

    @PostMapping("/setup")
    UserDTO setupUser(@RequestBody @Valid UserDtoIncoming input, HttpServletResponse response) {

        List<User> userList = userService.findAll();

        if (!userList.isEmpty()) {
            LOGGER.error("There are already existing users. No setup available");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return addUser(input, response);
    }

    @PostMapping("/resetpassword")
    ResponseEntity resetPassword(@RequestParam("email") String email) {

        User user = userService.findUserByEmail(email);
        if (user == null) {
            LOGGER.warn("E-Mail address {} not found", email);
            return ResponseEntity.notFound().build();
        }

        GenericResult result = userService.sendPasswordResetToken(user);
        if (result.isSuccess()) {
            return ResponseEntity.ok(messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale()));
        } else {
            LOGGER.error("Error sending password reset mail", result.getException());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale()));
        }
    }

    @PostMapping("/changepassword/{token}")
    ResponseEntity changePassword(@PathVariable String token, @RequestBody @Valid PasswordDto passwordDto, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        GenericResult<User> validationResult = userService.validatePasswordResetToken(token);
        if (!validationResult.isSuccess()) {
            LOGGER.error("Password reset token validation failed: {}", validationResult.getMessage());
            return ResponseEntity.notFound().build();
        }

        GenericResult changePasswordResult = userService.changePassword(validationResult.getPayload(), passwordDto);
        if (!changePasswordResult.isSuccess()) {
            LOGGER.error("Password reset failed: {}", changePasswordResult.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(messageSource.getMessage("user.changePassword.successful", null, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/confirm/{token}")
    UserDTO confirmRegistrationToken(@PathVariable String token, HttpServletResponse response) {
        boolean hasToken = userService.hasVerificationToken(token);

        if (!hasToken) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        User user = null;
        try {
            user = userService.confirmRegistration(token);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (TokenExpiredException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToEntity(UserDtoIncoming userDto) {
        if (userDto == null) {
            return null;
        }
        return modelMapper.map(userDto, User.class);
    }
}
