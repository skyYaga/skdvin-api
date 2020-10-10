package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:users')")
    public ResponseEntity<GenericResult<List<UserDTO>>> getAllUsers() {
        List<UserDTO> userList = userService.getUsers();
        return ResponseEntity.ok(new GenericResult<>(true, userList));
    }
}
