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
    private final UserService userService;   // ya lo tienes (lee/escribe roles, getEmail, etc.)
    private final RoleService roleService;   // ya lo tienes (push a custom claims)

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

        // valida contra enum
        for (String r : norm) {
            try { Role.valueOf(r); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Rol inválido: " + r +
                        " (válidos: " + Arrays.toString(Role.values()) + ")");
            }
        }
        return norm;
    }

    private UserResponse toResponse(UserRecord ur, List<String> roles) {
        return new UserResponse(
                ur.getUid(),
                ur.getEmail(),
                ur.getDisplayName(),
                ur.isDisabled(),
                roles == null ? List.of() : roles
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

        List<String> roles = normalizeAndValidateRoles(req.roles());
        // Persistir roles en Firestore
        userService.setRoles(created.getUid(), roles);

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
        ListUsersPage page;
        if (pageTokenOpt != null && !pageTokenOpt.isBlank()) {
            // Firebase Admin SDK no lista por token arbitrario directamente desde el SDK.
            // Se hace secuencial. Para simplificar demo: primera página.
            page = FirebaseAuth.getInstance().listUsers(null);
        } else {
            page = FirebaseAuth.getInstance().listUsers(null);
        }
        for (ExportedUserRecord user : page.getValues()) {
            List<String> roles = userService.getRoles(user.getUid());
            out.add(new UserResponse(user.getUid(), user.getEmail(), user.getDisplayName(), user.isDisabled(), roles));
            if (out.size() >= maxResults) break;
        }
        return out;
    }

    public UserResponse updateUser(String uid, UpdateUserRequest req, boolean syncClaims) throws Exception {
        var upd = new UserRecord.UpdateRequest(uid);

        if (req.email() != null) upd.setEmail(req.email());
        if (req.password() != null) upd.setPassword(req.password());
        if (req.displayName() != null) upd.setDisplayName(req.displayName());
        if (req.disabled() != null) {
            if (req.disabled()) upd.setDisabled(true); else upd.setDisabled(false);
        }

        UserRecord ur = FirebaseAuth.getInstance().updateUser(upd);

        List<String> roles = null;
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
        // Borra doc de roles para mantener limpio
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
