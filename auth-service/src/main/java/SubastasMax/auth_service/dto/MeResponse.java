// auth-service/src/main/java/SubastasMax/auth_service/dto/MeResponse.java
package SubastasMax.auth_service.dto;

import java.util.List;

public record MeResponse(
    String uid,
    String email,
    String displayName,  
    String avatarUrl,     
    List<String> roles
) {}
