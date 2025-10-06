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
public class TransactionUpdateDTO {
    
    private String status;
    
    private String errorMessage;
    
    private Date completedAt;
    
    private Date failedAt;
    
    private BalanceDTO balanceAfter;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TransactionMetadataDTO metadata;
}