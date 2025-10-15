package SubastasMax.auth_service.controller;

// auth_service/controller/AuthInitController.java

import SubastasMax.auth_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthInitController {

    private final UserService userService;

    public AuthInitController(UserService userService) {
        this.userService = userService;
    }

    /** Llamar tras el primer login del cliente */
    @PostMapping("/init")
    public Map<String, Object> init(Authentication authentication) throws Exception {
        String uid = (String) authentication.getPrincipal();
        userService.ensureUserDoc(uid);
        var roles = userService.getRoles(uid);
        var email = userService.getEmail(uid);
        return Map.of("uid", uid, "email", email, "roles", roles);
    }
}
