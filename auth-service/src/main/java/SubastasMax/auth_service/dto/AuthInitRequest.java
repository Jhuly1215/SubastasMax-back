// auth-service/src/main/java/SubastasMax/auth_service/dto/AuthInitRequest.java
package SubastasMax.auth_service.dto;

public record AuthInitRequest(
    String preferredRole, // "PARTICIPANTE" | "SUBASTADOR" (cliente puede enviar en minúsculas)
    String role,          // alias por compat
    String displayName,    // opcional
    String plan            // "FREE" | "PREMIUM"
) {}