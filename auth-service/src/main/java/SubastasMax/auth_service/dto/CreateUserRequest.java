// auth-service/src/main/java/SubastasMax/auth_service/dto/CreateUserRequest.java
package SubastasMax.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record CreateUserRequest(
    @Email @NotBlank String email,
    @NotBlank String password,
    String displayName,
    List<String> roles // opcional; default PARTICIPANTE si null/vacío
) {}
