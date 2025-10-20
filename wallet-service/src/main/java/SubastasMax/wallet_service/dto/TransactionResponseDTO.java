package SubastasMax.wallet_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO {

    private String transactionId;
    private String fromUserId;
    private String toUserId;
    private String amount;
    private String currency;
    private String type;
    private String status;
    private String description;
    private String requestId;
    private Date createdAt;
    private Date completedAt;
    private Date failedAt;
    private String errorMessage;
}