package SubastasMax.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Profile;

@Profile("gateway-auth")
class AuthFilterConfig {

  @Bean
  AuthenticationWebFilter authFilter(FirebaseAuthConverter converter) {
    // Manager explícito para evitar ambigüedad de constructores
    ReactiveAuthenticationManager authManager = authentication -> Mono.just(authentication);

    AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
    filter.setServerAuthenticationConverter(converter);
    return filter;
  }
}
