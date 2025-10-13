//auth_service/security/FirebaseTokenFilter.java:
package SubastasMax.auth_service.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.*;
import java.util.stream.Collectors;

public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final Firestore firestore;

    public FirebaseTokenFilter(String projectId) {
        this.firestore = FirestoreOptions.getDefaultInstance()
                .toBuilder().setProjectId(projectId).build().getService();
    }

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
    String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
    System.out.println("AUTH HDR => " + auth); // TEMP: quita luego
        if (auth == null || !auth.startsWith("Bearer ")) {
            try {
                chain.doFilter(req, res);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }

        String idToken = auth.substring("Bearer ".length()).trim();

        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken, true);

            String uid = decoded.getUid();
            // 1) Intentar roles desde custom claims
            @SuppressWarnings("unchecked")
            List<String> rolesFromClaims = (List<String>) decoded.getClaims().get("roles");

            List<String> roles;
            if (rolesFromClaims != null && !rolesFromClaims.isEmpty()) {
                roles = rolesFromClaims;
            } else {
                // 2) Fallback: Firestore users/{uid}.roles: ["PARTICIPANTE", ...]
                var snap = firestore.collection("users").document(uid).get().get();
                roles = (snap.exists() && snap.get("roles") != null)
                        ? (List<String>) snap.get("roles")
                        : List.of("PARTICIPANTE");
            }

            var authorities = roles.stream()
                    .map(String::toUpperCase)
                    .map(r -> "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            AbstractAuthenticationToken authToken = new AbstractAuthenticationToken(authorities) {
                @Override public Object getCredentials() { return idToken; }
                @Override public Object getPrincipal() { return uid; }
            };
            authToken.setAuthenticated(true);

            // colocar en SecurityContext
            var context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            org.springframework.security.core.context.SecurityContextHolder.setContext(context);

        } catch (Exception e) {
            System.err.println("[FIREBASE] verifyIdToken FALLÓ: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace(); // temporal, para ver detalle (issuer/aud/exp/skew/creds)
        }

        try {
            chain.doFilter(req, res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
