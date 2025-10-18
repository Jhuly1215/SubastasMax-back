// wallet-service/src/main/java/SubastasMax/wallet_service/security/FirebaseAuthFilter.java
package SubastasMax.wallet_service.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.io.IOException;
import java.util.List;

public class FirebaseAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        String idToken = auth.substring("Bearer ".length()).trim();
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            List<String> roles = (List<String>) decoded.getClaims().getOrDefault("roles", List.of());
            var authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
            return new FirebaseUserAuthentication(decoded.getUid(), authorities);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, AbstractAuthenticationToken authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
