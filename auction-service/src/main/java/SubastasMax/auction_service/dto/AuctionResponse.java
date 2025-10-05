package SubastasMax.auction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResponse {
    private String id;
    private String title;
    private String description;
    private String category;
    private Double initialPrice;
    private Double currentPrice;
    private Double minimumIncrement;
    private Double reservePrice;
    private String currency;
    private Date startAt;
    private Date endAt;
    private Date actualStartAt;
    private Date actualEndAt;
    private Long duration;
    private Boolean autoExtension;
    private Long extensionThreshold;
    private Long extensionTime;
    private Boolean featured;
    private List<String> images;
    private List<String> tags;
    private List<String> participants;
    private String status;
    private Integer totalBids;
    private Integer uniqueBidders;
    private Integer viewCount;
    private Integer visibility;
    private String winnerId;
    private Double winningBid;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    private String approvedBy;
    private Date approvedAt;
    private Date settledAt;
}
