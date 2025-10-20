package SubastasMax.wallet_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateDTO {

    @NotBlank(message = "From User ID is required")
    private String fromUserId;

    @NotBlank(message = "To User ID is required")
    private String toUserId;

    @NotBlank(message = "Amount is required")
    private String amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Type is required")
    private String type;

    private String description;

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotNull(message = "Balance before is required")
    private BalanceDTO balanceBefore;

    @NotNull(message = "Balance after is required")
    private BalanceDTO balanceAfter;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TransactionMetadataDTO metadata;
}