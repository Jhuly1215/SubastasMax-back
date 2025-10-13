package SubastasMax.auth_service.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import SubastasMax.auth_service.dto.UpdateRolesRequest;
import SubastasMax.auth_service.service.RoleService;
import SubastasMax.auth_service.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    // Constructor explícito (Spring lo usa para inyección)
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PutMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> updateRoles(@RequestBody @Valid UpdateRolesRequest req,
                                           @RequestParam(name = "syncClaims", defaultValue = "false") boolean syncClaims)
            throws Exception {
        var normalized = req.roles().stream().map(String::toUpperCase).toList();
        userService.setRoles(req.uid(), normalized);
        if (syncClaims) {
            roleService.pushRolesToCustomClaims(req.uid(), normalized);
        }
        return Map.of("ok", true);
    }
}
