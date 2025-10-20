package SubastasMax.wallet_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionMetadataDTO {
    private String auctionId;
    private String bidId;
    private String bankAccount;
    private String paymentMethod;
    private String externalTransactionId;
    private String fromCurrency;
    private String toCurrency;
    private String exchangeRate;
    private String convertedAmount;
}