// MeResponse.java
package SubastasMax.auth_service.dto;

import java.util.List;

public record MeResponse(String uid, String email, List<String> roles) {}

