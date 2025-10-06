package SubastasMax.auction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Amount is required")
    private String amount;

    @NotBlank(message = "AuctionId is required")
    private String auctionId;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "UserId is required")
    private String userId;

    @NotBlank(message = "UserName is required")
    private String userName;
}