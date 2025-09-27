package SubastasMax.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

@Configuration
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
