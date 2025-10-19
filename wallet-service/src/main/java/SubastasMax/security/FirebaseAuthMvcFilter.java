package SubastasMax.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class FirebaseAuthMvcFilter extends OncePerRequestFilter {

    private final AuthServiceClient authClient;

    public FirebaseAuthMvcFilter(AuthServiceClient authClient) {
        this.authClient = authClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Map<String, Object> userInfo = authClient.verifyToken(token);
                
                if (userInfo != null && userInfo.get("uid") != null) {
                    String uid = (String) userInfo.get("uid");
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    uid, 
                                    token,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
                    return;
                }
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error validando token: " + e.getMessage());
                return;
            }
        }
        // Si no hay Authorization header, Spring Security se encargará de rechazarlo en anyRequest().authenticated()

        chain.doFilter(req, res);
    }
}