// auth-service/src/main/java/SubastasMax/auth_service/dto/UpdateUserRequest.java
package SubastasMax.auth_service.dto;

import jakarta.validation.constraints.Email;
import java.util.List;

public record UpdateUserRequest(
    @Email String email,
    String password,
    String displayName,
    Boolean disabled,
    List<String> roles // si lo mandas, reemplaza roles
) {}
