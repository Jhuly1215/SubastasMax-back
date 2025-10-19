package SubastasMax.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain chain(ServerHttpSecurity http) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .authorizeExchange(ex -> ex
            // Actuator del propio gateway
            .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            // Actuator de los servicios detrás del gateway (ej. /auth/actuator/**)
            .pathMatchers("/*/actuator/**").permitAll()
            // El resto: el gateway NO autentica, delega a cada microservicio
            .anyExchange().permitAll()
        )
        .build();
  }
}
