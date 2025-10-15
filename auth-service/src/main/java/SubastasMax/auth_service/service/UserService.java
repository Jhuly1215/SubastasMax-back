// auth_service/service/UserService.java
package SubastasMax.auth_service.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Firestore firestore;

    public UserService(@Value("${app.firebase.project-id}") String projectId) {
        this.firestore = FirestoreOptions.getDefaultInstance()
                .toBuilder().setProjectId(projectId).build().getService();
    }

    /** Lee roles; si no hay doc, devuelve PARTICIPANTE (fallback) */
    public List<String> getRoles(String uid) throws Exception {
        var snap = firestore.collection("users").document(uid).get().get();
        if (snap.exists() && snap.get("roles") != null) {
            return (List<String>) snap.get("roles");
        }
        return List.of("PARTICIPANTE");
    }

    /** Upsert de roles en Firestore */
    public void setRoles(String uid, List<String> roles) throws Exception {
        firestore.collection("users").document(uid)
                .set(Map.of("roles", roles), com.google.cloud.firestore.SetOptions.merge())
                .get();
    }

    public String getEmail(String uid) throws Exception {
        UserRecord ur = FirebaseAuth.getInstance().getUser(uid);
        return ur.getEmail();
    }

    /** Crea (o completa) users/{uid} con rol por defecto si no existe */
    public void ensureUserDoc(String uid) throws Exception {
        var ref = firestore.collection("users").document(uid);
        var snap = ref.get().get();
        if (!snap.exists()) {
            String email = getEmail(uid);
            ref.set(Map.of(
                    "email", email,
                    "roles", List.of("PARTICIPANTE"),
                    "createdAt", com.google.cloud.Timestamp.now()
            )).get();
        } else if (snap.get("roles") == null) {
            // Si existe sin roles, los añade
            ref.set(Map.of("roles", List.of("PARTICIPANTE")),
                    com.google.cloud.firestore.SetOptions.merge()).get();
        }
    }
}
