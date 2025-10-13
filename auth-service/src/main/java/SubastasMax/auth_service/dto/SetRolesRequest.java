// auth-service/src/main/java/SubastasMax/auth_service/dto/SetRolesRequest.java
package SubastasMax.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SetRolesRequest(
    @NotBlank String uid,
    @NotNull List<String> roles
) {}
