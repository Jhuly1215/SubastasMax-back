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
public class TransactionResponseDTO {
    
    private String transactionId;
    private String userId;
    private String amount;
    private String currency;
    private String type;
    private String status;
    private String description;
    private String requestId;
    
    private BalanceDTO balanceBefore;
    private BalanceDTO balanceAfter;
    
    private Date createdAt;
    private Date completedAt;
    private Date failedAt;
    
    private String errorMessage;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TransactionMetadataDTO metadata;
}