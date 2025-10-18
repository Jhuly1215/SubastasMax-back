// auth-service/src/main/java/SubastasMax/auth_service/controller/AuthInitController.java
package SubastasMax.auth_service.controller;

import SubastasMax.auth_service.dto.AuthInitRequest;
import SubastasMax.auth_service.service.UserService;
import SubastasMax.auth_service.security.Role;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// auth-service/src/main/java/SubastasMax/auth_service/controller/AuthInitController.java
@RestController
@RequestMapping("/auth")
public class AuthInitController {

    private final UserService userService;

    public AuthInitController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/init")
    public Map<String, Object> init(Authentication authentication,
                                    @RequestBody(required = false) AuthInitRequest body) throws Exception {
        String uid = (String) authentication.getPrincipal();
        String email = userService.getEmail(uid);

        // Rol preferido (sólo PARTICIPANTE | SUBASTADOR)
        String rawRole = null;
        if (body != null) {
            if (body.preferredRole() != null && !body.preferredRole().isBlank()) {
                rawRole = body.preferredRole();
            } else if (body.role() != null && !body.role().isBlank()) {
                rawRole = body.role();
            }
        }
        String preferred = (rawRole == null ? "PARTICIPANTE" : rawRole.trim().toUpperCase());
        if (!preferred.equals("PARTICIPANTE") && !preferred.equals("SUBASTADOR")) {
            preferred = "PARTICIPANTE";
        }

        // Plan ("FREE" | "PROFESSIONAL"), default FREE
        String plan = "FREE";
        if (body != null && body.plan() != null && !body.plan().isBlank()) {
            String p = body.plan().trim().toUpperCase();
            plan = (p.equals("FREE") || p.equals("PROFESSIONAL")) ? p : "FREE";
        }

        // Upsert user + claims (roles + plan)
        userService.ensureUserDoc(uid, email, body != null ? body.displayName() : null, java.util.List.of(preferred), plan);

        var roles = userService.getRoles(uid);
        var finalPlan = userService.getPlan(uid);

        return Map.of(
            "uid", uid,
            "email", email,
            "roles", roles,
            "plan", finalPlan
        );
    }
}
