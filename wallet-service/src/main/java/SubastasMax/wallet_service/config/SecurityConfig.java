// wallet-service/src/main/java/SubastasMax/wallet_service/config/SecurityConfig.java
package SubastasMax.wallet_service.config;

import SubastasMax.wallet_service.security.FirebaseAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.addFilter(new FirebaseAuthFilter());
        http.authorizeHttpRequests(reg -> reg
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        );
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
