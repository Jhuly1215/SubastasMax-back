// auth-service/src/main/java/SubastasMax/auth_service/controller/AuthInitController.java
package SubastasMax.auth_service.controller;

import SubastasMax.auth_service.dto.AuthInitRequest;
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

    /** Llamar tras el primer login del cliente; crea/mergea el doc con el rol y plan elegidos */
    @PostMapping("/init")
    public Map<String, Object> init(Authentication authentication,
                                    @RequestBody(required = false) AuthInitRequest body) throws Exception {
        String uid = (String) authentication.getPrincipal();

        // Lee email de Firebase para persistirlo
        String email = userService.getEmail(uid);

        // Determinar rol elegido (solo PARTICIPANTE o SUBASTADOR)
        String rawRole = null;
        if (body != null) {
            if (body.preferredRole() != null && !body.preferredRole().isBlank()) {
                rawRole = body.preferredRole();
            } else if (body.role() != null && !body.role().isBlank()) {
                rawRole = body.role();
            }
        }
        String preferredRole = (rawRole == null ? "PARTICIPANTE" : rawRole.trim().toUpperCase());
        if (!preferredRole.equals("PARTICIPANTE") && !preferredRole.equals("SUBASTADOR")) {
            preferredRole = "PARTICIPANTE"; // no permitir ADMIN desde cliente
        }

        // Crear o mergear documento base
        userService.ensureUserDoc(uid, email, body != null ? body.displayName() : null, List.of(preferredRole));

        // Manejo de plan (FREE | PROFESSIONAL)
        String plan = null;
        if (body != null && body.plan() != null && !body.plan().isBlank()) {
            String up = body.plan().trim().toUpperCase();
            if (up.equals("FREE") || up.equals("PROFESSIONAL")) {
                userService.setPlan(uid, up); // guardamos en Firestore
                plan = up;
            }
        } else {
            plan = userService.getPlan(uid); // leer si ya tenía
        }

        // Devuelve estado actual
        var roles = userService.getRoles(uid);
        return Map.of(
                "uid", uid,
                "email", email,
                "roles", roles,
                "plan", plan
        );
    }
}
