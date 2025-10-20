// auth-service/src/main/java/SubastasMax/auth_service/controller/AuthInitController.java
package SubastasMax.auth_service.controller;

import SubastasMax.auth_service.dto.AuthInitRequest;
import SubastasMax.auth_service.service.UserService;
import SubastasMax.auth_service.security.Role;
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

    /** Llamar tras el primer login del cliente; crea/mergea el doc con el rol elegido */
    @PostMapping("/init")
    public Map<String, Object> init(Authentication authentication,
                                    @RequestBody(required = false) AuthInitRequest body) throws Exception {
        String uid = (String) authentication.getPrincipal();

        // Lee email de Firebase para persistirlo
        String email = userService.getEmail(uid);

        // Determinar rol elegido (solo público: PARTICIPANTE o SUBASTADOR)
        // en AuthInitController.init(...)
        String raw = null;
        if (body != null) {
            if (body.preferredRole() != null && !body.preferredRole().isBlank()) {
                raw = body.preferredRole();
            } else if (body.role() != null && !body.role().isBlank()) { // <-- alias
                raw = body.role();
            }
        }
        String preferred = (raw == null ? "PARTICIPANTE" : raw.trim().toUpperCase());
        if (!preferred.equals("PARTICIPANTE") && !preferred.equals("SUBASTADOR")) {
        preferred = "PARTICIPANTE"; // no permitir ADMIN desde cliente
        }
        userService.ensureUserDoc(uid, email, body != null ? body.displayName() : null, List.of(preferred));


        // Devuelve estado actual
        var roles = userService.getRoles(uid);
        return Map.of("uid", uid, "email", email, "roles", roles);
    }
}
