package Subastasmax.notificationservice.config;

import Subastasmax.notificationservice.security.TokenAuthFilter;
import Subastasmax.notificationservice.security.IntrospectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig que registra TokenAuthFilter. El filtro intenta:
 * 1) validar JWT contra jwkSetUri si está configurado
 * 2) si falla, llamar al endpoint de introspección (si está configurado)
 *
 * Reglas:
 * - /api/events -> ROLE_SERVICE
 * - /api/admin/** -> ROLE_ADMIN
 * - /api/notifications/** -> authenticated
 */
@Configuration
public class SecurityConfig {

    @Value("${auth.jwk-set-uri:}")
    private String jwkSetUri;

    @Value("${auth.introspection.url:}")
    private String introspectionUrl;

    @Value("${auth.introspection.client-id:}")
    private String introspectionClientId;

    @Value("${auth.introspection.client-secret:}")
    private String introspectionClientSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, IntrospectionService introspectionService) throws Exception {
        TokenAuthFilter tokenFilter = new TokenAuthFilter(jwkSetUri, introspectionService);

        http.csrf().disable();
        http.cors();

        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .antMatchers("/actuator/health", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .antMatchers("/api/events").hasAuthority("ROLE_SERVICE")
                .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/notifications/**").authenticated()
                .anyRequest().denyAll();

        return http.build();
    }

    @Bean
    public IntrospectionService introspectionService(org.springframework.web.client.RestTemplateBuilder restTemplateBuilder) {
        return new IntrospectionService(restTemplateBuilder, introspectionUrl, introspectionClientId, introspectionClientSecret);
    }
}