package SubastasMax.notificationservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Cliente simple para token introspection (RFC 7662).
 * Configurar en application.yml:
 * auth.introspection.url
 * auth.introspection.client-id
 * auth.introspection.client-secret
 */
public class IntrospectionService {

    private static final Logger log = LoggerFactory.getLogger(IntrospectionService.class);

    private final RestTemplate restTemplate;
    private final String introspectionUrl;
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IntrospectionService(RestTemplateBuilder restTemplateBuilder, String introspectionUrl,
                                String clientId, String clientSecret) {
        this.restTemplate = restTemplateBuilder.build();
        this.introspectionUrl = introspectionUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Introspect token. Returns Optional of claims map if active=true, otherwise empty.
     */
    public Optional<Map<String, Object>> introspect(String token) {
        if (!StringUtils.hasText(introspectionUrl) || !StringUtils.hasText(token)) {
            return Optional.empty();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // Basic auth with client credentials
            if (StringUtils.hasText(clientId) && clientSecret != null) {
                String basic = clientId + ":" + (clientSecret == null ? "" : clientSecret);
                headers.set(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(basic.getBytes()));
            }

            String body = UriComponentsBuilder.newInstance()
                    .queryParam("token", token)
                    .build()
                    .toUriString()
                    .substring(1); // remove leading "?"

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = restTemplate.exchange(introspectionUrl, HttpMethod.POST, entity, String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Map<String, Object> map = objectMapper.readValue(resp.getBody(), Map.class);
                Object active = map.get("active");
                if (active instanceof Boolean && (Boolean) active) {
                    // Successful active token: return claims map
                    return Optional.of(Collections.unmodifiableMap(map));
                } else {
                    log.debug("Introspection reported inactive token: {}", token);
                    return Optional.empty();
                }
            } else {
                log.warn("Introspection returned non-2xx: {} body={}", resp.getStatusCode(), resp.getBody());
            }
        } catch (RestClientException rc) {
            log.error("Error calling introspection endpoint", rc);
        } catch (Exception ex) {
            log.error("Error parsing introspection response", ex);
        }
        return Optional.empty();
    }
}