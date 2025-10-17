package SubastasMax.auth_service.service;

import SubastasMax.auth_service.security.Role;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String COL_USERS = "users";

    private final Firestore firestore;

    public UserService(@Value("${app.firebase.project-id}") String projectId) {
        this.firestore = FirestoreOptions.getDefaultInstance()
                .toBuilder().setProjectId(projectId).build().getService();
    }

    // ===== Helpers =====

    private List<String> normalizeAndValidateRoles(List<String> rolesRaw) {
        List<String> base = (rolesRaw == null || rolesRaw.isEmpty())
                ? List.of("PARTICIPANTE")
                : rolesRaw;

        List<String> out = base.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.toList());

        for (String r : out) {
            try { Role.valueOf(r); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Rol inválido: " + r + " (válidos: " + Arrays.toString(Role.values()) + ")"
                );
            }
        }
        return out;
    }

    // ===== Lecturas básicas =====

    public String getEmail(String uid) throws Exception {
        UserRecord ur = FirebaseAuth.getInstance().getUser(uid);
        return ur.getEmail();
    }

    public String getDisplayNameFromAuth(String uid) throws Exception {
        UserRecord ur = FirebaseAuth.getInstance().getUser(uid);
        return ur.getDisplayName();
    }

    /** Devuelve roles; si no hay doc o campo, fallback PARTICIPANTE. */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String uid) throws Exception {
        var snap = firestore.collection(COL_USERS).document(uid).get().get();
        if (snap.exists() && snap.get("roles") != null) {
            return (List<String>) snap.get("roles");
        }
        return List.of("PARTICIPANTE");
    }

    /**
     * Lee extras del perfil en Firestore:
     * - displayName (String)
     * - avatarUrl   (String)
     * - phone       (String)
     */
    public Map<String, Object> getProfileExtras(String uid) throws Exception {
        var snap = firestore.collection(COL_USERS).document(uid).get().get();
        Map<String, Object> out = new HashMap<>();
        if (snap.exists()) {
            Object dn = snap.get("displayName");
            Object av = snap.get("avatarUrl");
            Object ph = snap.get("phone");
            if (dn instanceof String) out.put("displayName", dn);
            if (av instanceof String) out.put("avatarUrl", av);
            if (ph instanceof String) out.put("phone", ph);
        }
        return out;
    }

    // ===== Escrituras / upserts =====

    /** Upsert de roles con merge seguro. */
    public void setRoles(String uid, List<String> rolesRaw) throws Exception {
        List<String> roles = normalizeAndValidateRoles(rolesRaw);
        firestore.collection(COL_USERS).document(uid)
                .set(Map.of("roles", roles), SetOptions.merge())
                .get();
    }

    /**
     * Crea o completa users/{uid}:
     * - Si no existe: email, roles(normalizados), displayName(opc), createdAt
     * - Si existe: hace merge de email (si no estaba), roles (si se envían), displayName (si se envía)
     */
    public void ensureUserDoc(String uid, String email, String displayName, List<String> rolesRaw) throws Exception {
        List<String> roles = (rolesRaw == null || rolesRaw.isEmpty())
                ? List.of("PARTICIPANTE")
                : normalizeAndValidateRoles(rolesRaw);

        DocumentReference ref = firestore.collection(COL_USERS).document(uid);
        DocumentSnapshot snap = ref.get().get();

        Map<String, Object> data = new HashMap<>();
        if (email != null && !email.isBlank()) data.put("email", email);
        if (displayName != null && !displayName.isBlank()) data.put("displayName", displayName);
        data.put("roles", roles);

        if (!snap.exists()) {
            data.put("createdAt", Timestamp.now());
            ref.set(data).get();
        } else {
            ref.set(data, SetOptions.merge()).get();
        }
    }

    /** Merge parcial de campos de perfil (no toca roles). Ignora nulls. */
    public void updateUserProfileFields(String uid, String displayName, String avatarUrl, String phone) throws Exception {
        Map<String, Object> patch = new HashMap<>();
        if (displayName != null && !displayName.isBlank()) patch.put("displayName", displayName);
        if (avatarUrl != null && !avatarUrl.isBlank())   patch.put("avatarUrl", avatarUrl);
        if (phone != null && !phone.isBlank())           patch.put("phone", phone);
        if (!patch.isEmpty()) {
            firestore.collection(COL_USERS).document(uid)
                    .set(patch, SetOptions.merge()).get();
        }
    }
}
