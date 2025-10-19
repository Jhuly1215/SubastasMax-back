package SubastasMax.notificationservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper para mapear Jwt -> Authentication.
 * Extrae claims "roles" (list), "authorities" (list) o "scope"/"scp" (string/space-separated).
 */
public final class JwtAuthenticationUtils {

    private JwtAuthenticationUtils() {}

    public static Authentication jwtToAuthentication(Jwt jwt) {
        Collection<SimpleGrantedAuthority> authorities = extractAuthorities(jwt.getClaims());
        String principal = extractPrincipal(jwt.getClaims());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
        return auth;
    }

    private static String extractPrincipal(Map<String, Object> claims) {
        // Prefer sub, then user_id, then preferred_username
        if (claims.containsKey("sub")) return claims.get("sub").toString();
        if (claims.containsKey("user_id")) return claims.get("user_id").toString();
        if (claims.containsKey("preferred_username")) return claims.get("preferred_username").toString();
        return "unknown";
    }

    private static Collection<SimpleGrantedAuthority> extractAuthorities(Map<String, Object> claims) {
        Set<String> roles = new HashSet<>();

        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof Collection) {
            ((Collection<?>) rolesClaim).forEach(o -> roles.add(String.valueOf(o)));
        } else if (rolesClaim instanceof String) {
            for (String r : ((String) rolesClaim).split("[, ]+")) if (!r.isBlank()) roles.add(r);
        }

        Object authClaim = claims.get("authorities");
        if (authClaim instanceof Collection) {
            ((Collection<?>) authClaim).forEach(o -> roles.add(String.valueOf(o)));
        } else if (authClaim instanceof String) {
            for (String r : ((String) authClaim).split("[, ]+")) if (!r.isBlank()) roles.add(r);
        }

        // scopes / scp
        Object scope = claims.get("scope");
        if (scope == null) scope = claims.get("scp");
        if (scope instanceof String) {
            for (String r : ((String) scope).split("[, ]+")) if (!r.isBlank()) roles.add(r);
        }

        return roles.stream()
                .map(r -> r.toUpperCase().startsWith("ROLE_") ? r : "ROLE_" + r.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}