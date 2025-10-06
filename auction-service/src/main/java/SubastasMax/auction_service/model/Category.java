package SubastasMax.auction_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    private String id;
    private String amount;
    private String auctionId;
    private String status;
    private Instant timestamp;
    private String userId;
    private String userName;
}