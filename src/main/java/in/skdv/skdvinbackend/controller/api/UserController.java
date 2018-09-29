package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.model.entity.User;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.EmailExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private IUserService userService;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    UserDTO addUser(@RequestBody @Valid UserDTO input, HttpServletResponse response) {

        User user = null;
        response.setStatus(HttpServletResponse.SC_CREATED);

        try {
            user = userService.registerNewUser(convertToEntity(input));
        } catch (EmailExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        // do not return pw
        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        if (user == null) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToEntity(UserDTO userDto) {
        if (userDto == null) {
            return null;
        }
        return modelMapper.map(userDto, User.class);
    }
}
