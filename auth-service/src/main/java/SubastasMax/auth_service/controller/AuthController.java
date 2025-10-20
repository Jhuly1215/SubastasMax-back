// auth_service/controller/AuthController.java
package SubastasMax.auth_service.controller;

import SubastasMax.auth_service.dto.MeResponse;
import SubastasMax.auth_service.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) throws Exception {
        String uid = (String) authentication.getPrincipal();
        String email = userService.getEmail(uid);
        List<String> roles = userService.getRoles(uid);

        // displayName: preferir Firestore si existe, sino FirebaseAuth
        Map<String,Object> extras = userService.getProfileExtras(uid);
        String displayName = extras.get("displayName") != null
                ? (String) extras.get("displayName")
                : userService.getDisplayNameFromAuth(uid);
        String avatarUrl = (String) extras.get("avatarUrl");
        String plan = (String) extras.getOrDefault("plan", "FREE");

        return new MeResponse(uid, email, displayName, avatarUrl, roles, plan);
    }
}