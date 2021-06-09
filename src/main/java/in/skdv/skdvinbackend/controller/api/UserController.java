package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_update:users')")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserDTO input) {
        userService.updateUser(input);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('SCOPE_read:users')")
    public ResponseEntity<GenericResult<List<RoleDTO>>> getRoles() {
        List<RoleDTO> roles = userService.getRoles();
        return ResponseEntity.ok(new GenericResult<>(true, roles));
    }
}
