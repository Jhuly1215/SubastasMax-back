package SubastasMax.notificationservice.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Filter that attempts:
 *  - JWT decode via JWKS (if jwkSetUri provided)
 *  - fallback to introspection endpoint via IntrospectionService
 *
 * Maps claims ["roles"] or ["authorities"] or scopes into GrantedAuthorities with ROLE_ prefix.
 */
public class TokenAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenAuthFilter.class);

    private final String jwkSetUri;
    private final IntrospectionService introspectionService;
    private JwtDecoder jwtDecoder;

    public TokenAuthFilter(String jwkSetUri, IntrospectionService introspectionService) {
        this.jwkSetUri = jwkSetUri;
        this.introspectionService = introspectionService;
        if (StringUtils.hasText(jwkSetUri)) {
            try {
                // small timeout for JWK retrieval
                DefaultResourceRetriever rr = new DefaultResourceRetriever(2000, 2000);
                JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(jwkSetUri), rr);
                // Use NimbusJwtDecoder with jwkSetUri (spring provides convenience builder but we do manual)
                this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            } catch (Exception e) {
                log.warn("Unable to initialize JwtDecoder with jwkSetUri {}: {}", jwkSetUri, e.getMessage());
                this.jwtDecoder = null;
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = auth.substring(7).trim();
            // 1) Try JWT decode if available
            if (jwtDecoder != null) {
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    Authentication authToken = JwtAuthenticationUtils.jwtToAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                    return;
                } catch (JwtException e) {
                    log.debug("JWT validation failed (will try introspection): {}", e.getMessage());
                }
            }
            // 2) Try introspection
            var claimsOpt = introspectionService.introspect(token);
            if (claimsOpt.isPresent()) {
                Map<String, Object> claims = claimsOpt.get();
                // build Jwt-like object for downstream usage
                Instant iat = getInstantFromClaim(claims.get("iat"));
                Instant exp = getInstantFromClaim(claims.get("exp"));
                if (iat == null) iat = Instant.now();
                if (exp == null) exp = iat.plusSeconds(3600);

                Map<String, Object> headers = new HashMap<>();
                headers.put("alg", "none");

                Jwt jwt = new Jwt(token, iat, exp, headers, claims);
                Authentication authToken = JwtAuthenticationUtils.jwtToAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
                return;
            }

            // If neither succeeded: continue without authentication (will be rejected if endpoint requires auth)
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error in TokenAuthFilter", e);
            try {
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                log.error("Error continuing filter chain after failure", ex);
            }
        }
    }

    private Instant getInstantFromClaim(Object obj) {
        if (obj == null) return null;
        try {
            if (obj instanceof Number) {
                long epoch = ((Number) obj).longValue();
                return Instant.ofEpochSecond(epoch);
            } else {
                long epoch = Long.parseLong(obj.toString());
                return Instant.ofEpochSecond(epoch);
            }
        } catch (Exception e) {
            return null;
        }
    }
}