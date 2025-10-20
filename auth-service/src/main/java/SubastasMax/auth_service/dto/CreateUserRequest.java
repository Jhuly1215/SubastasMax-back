// auth-service/src/main/java/SubastasMax/auth_service/dto/CreateUserRequest.java
package SubastasMax.auth_service.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
    @Email @NotBlank String email,
    @NotBlank String password,
    String displayName,
    List<String> roles,
    String avatarUrl,   // opcional
    String phone,
    String plan
) {}