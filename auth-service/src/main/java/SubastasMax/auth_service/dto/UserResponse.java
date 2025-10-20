// auth-service/src/main/java/SubastasMax/auth_service/dto/UserResponse.java
package SubastasMax.auth_service.dto;

import java.util.List;

// + opcional
public record UserResponse(
    String uid,
    String email,
    String displayName,
    boolean disabled,
    List<String> roles,
    String avatarUrl,  // opcional
    String phone,       // opcional
    String plan 
) {}