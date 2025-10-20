// auth-service/src/main/java/SubastasMax/auth_service/dto/SelfInitRequest.java
package SubastasMax.auth_service.dto;

public record SelfInitRequest(
    String displayName,
    String avatarUrl,
    String phone
) {}
