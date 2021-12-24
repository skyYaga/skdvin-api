package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.common.user.UserListResult;
import in.skdv.skdvinbackend.model.dto.RoleDTO;
import in.skdv.skdvinbackend.model.dto.UserDTO;
import in.skdv.skdvinbackend.service.IUserService;
import in.skdv.skdvinbackend.util.GenericResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:users')")
    public GenericResult<UserListResult> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "per_page", defaultValue = "5") Integer amountPerPage) {
        log.info("Getting users page {}, perPage: {}", pageNumber, amountPerPage);
        UserListResult userList = userService.getUsers(pageNumber, amountPerPage);
        return new GenericResult<>(true, userList);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_update:users')")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UserDTO input) {
        userService.updateUser(input);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('SCOPE_read:users')")
    public GenericResult<List<RoleDTO>> getRoles() {
        log.info("Getting roles");
        List<RoleDTO> roles = userService.getRoles();
        return new GenericResult<>(true, roles);
    }
}
