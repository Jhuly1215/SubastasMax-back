package SubastasMax.auth_service.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Opcional: sincronizar roles a custom claims (para que viajen en el token)
@Service
public class RoleService {

    public void pushRolesToCustomClaims(String uid, List<String> roles) throws Exception {
        UserRecord user = FirebaseAuth.getInstance().getUser(uid);
        Map<String, Object> claims = user.getCustomClaims() == null ? new HashMap<>() : new HashMap<>(user.getCustomClaims());
        claims.put("roles", roles);
        FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
        // Nota: el usuario debe refrescar su ID token para ver los claims nuevos.
    }
}

