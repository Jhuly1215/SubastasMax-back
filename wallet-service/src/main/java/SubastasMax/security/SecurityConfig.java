package SubastasMax.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain chain(HttpSecurity http, FirebaseAuthMvcFilter f) throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.authorizeHttpRequests(a -> a
      .requestMatchers("/actuator/**").permitAll()
      .anyRequest().authenticated());
    http.addFilterBefore(f, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}

