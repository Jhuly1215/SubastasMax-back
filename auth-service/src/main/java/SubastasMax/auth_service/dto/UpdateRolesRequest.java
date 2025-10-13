// UpdateRolesRequest.java
package SubastasMax.auth_service.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateRolesRequest(
        String uid,
        @NotEmpty List<String> roles
) {}

