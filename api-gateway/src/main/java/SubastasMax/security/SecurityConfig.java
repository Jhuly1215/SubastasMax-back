package SubastasMax.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  SecurityWebFilterChain chain(ServerHttpSecurity http, AuthenticationWebFilter authFilter) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(e -> e
        .pathMatchers("/actuator/**").permitAll()
        .anyExchange().authenticated())
      .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .build();
  }
}