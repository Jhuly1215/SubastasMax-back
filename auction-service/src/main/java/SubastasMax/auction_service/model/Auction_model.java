package SubastasMax.auction_service.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auction_model {
    private String id;
    private Timestamp actualEndAt;
    private Timestamp actualStartAt;
    private Timestamp approvedAt;
    private String approvedBy;
    private Boolean autoExtension;
    private String category;
    private Timestamp createdAt;
    private String createdBy;
    private String currency;
    private Double currentPrice;
    private String description;
    private Long duration;
    private Timestamp endAt;
    private Long extensionThreshold;
    private Long extensionTime;
    private Boolean featured;
    
    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    private Double initialPrice;
    private Double minimumIncrement;
    
    @Builder.Default
    private List<String> participants = new ArrayList<>();
    
    private Double reservePrice;
    private Timestamp scheduledDate;
    private Timestamp scheduledTime;
    private Timestamp settledAt;
    private Timestamp startAt;
    private String status; // draft, pending, live, paused, closed
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    private String title;
    private Integer totalBids;
    private Integer uniqueBidders;
    private Timestamp updatedAt;
    private Integer viewCount;
    private Integer visibility;
    private String winnerId;
    private Double winningBid;
}