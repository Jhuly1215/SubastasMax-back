// auth-service/src/main/java/SubastasMax/auth_service/service/UserAdminService.java
package SubastasMax.auth_service.service;

import SubastasMax.auth_service.dto.*;
import SubastasMax.auth_service.security.Role;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.auth.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserAdminService {

    private final Firestore firestore;
    private final UserService userService;   // lee/escribe roles, email, perfil, plan
    private final RoleService roleService;   // sincroniza roles a custom claims

    public UserAdminService(
            @Value("${app.firebase.project-id}") String projectId,
            UserService userService,
            RoleService roleService) {
        this.firestore = FirestoreOptions.getDefaultInstance()
                .toBuilder().setProjectId(projectId).build().getService();
        this.userService = userService;
        this.roleService = roleService;
    }

    // ===== Helpers =====
    private List<String> normalizeAndValidateRoles(List<String> rolesRaw) {
        List<String> roles = (rolesRaw == null || rolesRaw.isEmpty())
                ? List.of("PARTICIPANTE")
                : rolesRaw;

        List<String> norm = roles.stream()
                .map(r -> r == null ? "" : r.trim().toUpperCase())
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());

        for (String r : norm) {
            try { Role.valueOf(r); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Rol inválido: " + r +
                        " (válidos: " + Arrays.toString(Role.values()) + ")");
            }
        }
        return norm;
    }

    private String normalizePlan(String raw) {
        if (raw == null) return null;
        String up = raw.trim().toUpperCase();
        return switch (up) {
            case "FREE", "PROFESSIONAL" -> up;
            default -> null;
        };
    }

    private UserResponse toResponse(UserRecord ur, List<String> roles) throws Exception {
        Map<String, Object> extras = userService.getProfileExtras(ur.getUid());
        String plan = (String) extras.get("plan"); // leído desde Firestore (si existe)
        return new UserResponse(
                ur.getUid(),
                ur.getEmail(),
                ur.getDisplayName(),
                ur.isDisabled(),
                roles == null ? List.of() : roles,
                (String) extras.get("avatarUrl"),
                (String) extras.get("phone"),
                plan
        );
    }

    // ===== CRUD =====

    public UserResponse createUser(CreateUserRequest req, boolean syncClaims) throws Exception {
        var createReq = new UserRecord.CreateRequest()
                .setEmail(req.email())
                .setPassword(req.password());

        if (req.displayName() != null && !req.displayName().isBlank()) {
            createReq.setDisplayName(req.displayName());
        }

        UserRecord created = FirebaseAuth.getInstance().createUser(createReq);

        // Roles -> Firestore
        List<String> roles = normalizeAndValidateRoles(req.roles());
        userService.setRoles(created.getUid(), roles);

        // Doc base -> Firestore
        userService.ensureUserDoc(
                created.getUid(),
                created.getEmail(),
                req.displayName(),
                roles
        );

        // Extras perfil -> Firestore (merge)
        userService.updateUserProfileFields(
                created.getUid(),
                req.displayName(),
                req.avatarUrl(),
                req.phone()
        );

        // Plan (opcional)
        String plan = normalizePlan(req.plan());
        if (plan != null) userService.setPlan(created.getUid(), plan);

        // Claims
        if (syncClaims) {
            roleService.pushRolesToCustomClaims(created.getUid(), roles);
        }

        return toResponse(created, roles);
    }

    public UserResponse getUser(String uid) throws Exception {
        UserRecord ur = FirebaseAuth.getInstance().getUser(uid);
        List<String> roles = userService.getRoles(uid);
        return toResponse(ur, roles);
    }

    public List<UserResponse> listUsers(int maxResults, String pageTokenOpt) throws Exception {
        List<UserResponse> out = new ArrayList<>();
        // Por simplicidad, primera página
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);

        for (ExportedUserRecord user : page.getValues()) {
            List<String> roles = userService.getRoles(user.getUid());
            Map<String, Object> extras = userService.getProfileExtras(user.getUid());
            out.add(new UserResponse(
                    user.getUid(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.isDisabled(),
                    roles == null ? List.of() : roles,
                    (String) extras.get("avatarUrl"),
                    (String) extras.get("phone"),
                    (String) extras.get("plan") // devuelve el plan si está
            ));
            if (out.size() >= maxResults) break;
        }
        return out;
    }

    public UserResponse updateUser(String uid, UpdateUserRequest req, boolean syncClaims) throws Exception {
        var upd = new UserRecord.UpdateRequest(uid);

        if (req.email() != null) upd.setEmail(req.email());
        if (req.password() != null) upd.setPassword(req.password());
        if (req.displayName() != null) upd.setDisplayName(req.displayName());
        if (req.disabled() != null) upd.setDisabled(req.disabled());

        UserRecord ur = FirebaseAuth.getInstance().updateUser(upd);

        // Extras perfil -> Firestore (merge)
        userService.updateUserProfileFields(
                uid,
                req.displayName(),
                req.avatarUrl(),
                req.phone()
        );

        // Plan (FREE | PROFESSIONAL) si vino en el request
        if (req.plan() != null) {
            String norm = normalizePlan(req.plan());
            if (norm != null) {
                userService.setPlan(uid, norm);
            }
        }

        // Roles
        List<String> roles;
        if (req.roles() != null) {
            roles = normalizeAndValidateRoles(req.roles());
            userService.setRoles(uid, roles);
            if (syncClaims) roleService.pushRolesToCustomClaims(uid, roles);
        } else {
            roles = userService.getRoles(uid);
        }

        return toResponse(ur, roles);
    }

    public void deleteUser(String uid) throws Exception {
        FirebaseAuth.getInstance().deleteUser(uid);
        try {
            firestore.collection("users").document(uid).delete().get();
        } catch (InterruptedException | ExecutionException ignored) {}
    }

    public Map<String, Object> setRoles(SetRolesRequest req, boolean syncClaims) throws Exception {
        String uid = req.uid();
        List<String> roles = normalizeAndValidateRoles(req.roles());
        userService.setRoles(uid, roles);
        if (syncClaims) {
            roleService.pushRolesToCustomClaims(uid, roles);
        }
        return Map.of("ok", true, "uid", uid, "roles", roles);
    }
}
