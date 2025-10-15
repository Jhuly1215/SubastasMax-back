package SubastasMax.auth_service.controller;

import SubastasMax.auth_service.dto.MeResponse;
import SubastasMax.auth_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    // Constructor explícito para inyección
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) throws Exception {
        String uid = (String) authentication.getPrincipal();
        String email = userService.getEmail(uid);
        List<String> roles = userService.getRoles(uid);
        return new MeResponse(uid, email, roles);
    }
}
