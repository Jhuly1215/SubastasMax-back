package SubastasMax.api_gateway.security;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

import org.springframework.context.annotation.Profile;

@Profile("gateway-auth")
public class FirebaseAuthConverter implements ServerAuthenticationConverter {
  @Override
  public Mono<Authentication> convert(ServerWebExchange exchange) {
    String h = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (h == null || !h.startsWith("Bearer ")) return Mono.empty();
    String token = h.substring(7);
    return Mono.fromCallable(() -> FirebaseAuth.getInstance().verifyIdToken(token))
      .map(t -> new UsernamePasswordAuthenticationToken(
          t.getUid(), t, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
  }
}

