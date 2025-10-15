// auth-service/src/main/java/SubastasMax/auth_service/controller/AdminUserController.java
package SubastasMax.auth_service.controller;

import SubastasMax.auth_service.dto.*;
import SubastasMax.auth_service.security.Role;
import SubastasMax.auth_service.service.UserAdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    // ===== Roles =====

    @GetMapping("/roles")
    public List<String> listRoles() {
        return Arrays.stream(Role.values()).map(Enum::name).toList();
    }

    @PutMapping("/roles")
    public Map<String, Object> setRoles(@RequestBody @Valid SetRolesRequest req,
                                        @RequestParam(defaultValue = "true") boolean syncClaims) throws Exception {
        return userAdminService.setRoles(req, syncClaims);
    }

    // ===== Usuarios =====

    @PostMapping("/users")
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest req,
                                   @RequestParam(defaultValue = "true") boolean syncClaims) throws Exception {
        return userAdminService.createUser(req, syncClaims);
    }

    @GetMapping("/users/{uid}")
    public UserResponse get(@PathVariable String uid) throws Exception {
        return userAdminService.getUser(uid);
    }

    @GetMapping("/users")
    public List<UserResponse> list(@RequestParam(defaultValue = "50") int size,
                                   @RequestParam(required = false) String pageToken) throws Exception {
        return userAdminService.listUsers(size, pageToken);
    }

    @PutMapping("/users/{uid}")
    public UserResponse update(@PathVariable String uid,
                               @RequestBody @Valid UpdateUserRequest req,
                               @RequestParam(defaultValue = "true") boolean syncClaims) throws Exception {
        return userAdminService.updateUser(uid, req, syncClaims);
    }

    @DeleteMapping("/users/{uid}")
    public Map<String, Object> delete(@PathVariable String uid) throws Exception {
        userAdminService.deleteUser(uid);
        return Map.of("ok", true, "uid", uid);
    }
}
