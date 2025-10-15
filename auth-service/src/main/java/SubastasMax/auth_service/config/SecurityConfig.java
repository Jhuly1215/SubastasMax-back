//auth_service/config/SecurityConfig.java:
package SubastasMax.auth_service.config;

import SubastasMax.auth_service.security.FirebaseTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.firebase.enabled:true}")
    private boolean firebaseEnabled;

    @Value("${app.firebase.project-id:}")
    private String projectId;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      System.out.println("[SEC] app.firebase.enabled=" + firebaseEnabled);
      System.out.println("[SEC] app.firebase.project-id=" + projectId);
    
      http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(
            org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/auth/me").authenticated()
            .anyRequest().authenticated())
        .httpBasic(h -> h.disable())
        .formLogin(f -> f.disable())
        .logout(l -> l.disable());
    
      if (firebaseEnabled && projectId != null && !projectId.isBlank()) {
        System.out.println("[SEC] Añadiendo FirebaseTokenFilter");
        http.addFilterBefore(new FirebaseTokenFilter(projectId),
            org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
      } else {
        System.out.println("[SEC] NO se añade FirebaseTokenFilter (faltan props)");
      }
    
      return http.build();
    }
    
}
