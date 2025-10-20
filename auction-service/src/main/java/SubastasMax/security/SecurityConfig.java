package SubastasMax.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
SecurityFilterChain chain(HttpSecurity http, FirebaseAuthMvcFilter firebaseAuthMvcFilter, HandlerMappingIntrospector introspector) throws Exception {
    http.csrf(csrf -> csrf.disable());

    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.exceptionHandling(h -> h.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

    MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

    http.authorizeHttpRequests(a -> a
        .requestMatchers(mvc.pattern("/actuator/**")).permitAll()
        .requestMatchers(mvc.pattern("/test/**")).permitAll()
        .requestMatchers(mvc.pattern("/public/**")).permitAll()
        .requestMatchers(mvc.pattern("/api/auctions")).permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .anyRequest().authenticated()
    )
    .httpBasic(basic -> basic.disable())  // Deshabilita autenticación básica
    .formLogin(form -> form.disable())     // Deshabilita login por formulario
    .addFilterBefore(firebaseAuthMvcFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
