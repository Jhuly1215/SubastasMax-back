package SubastasMax.admin_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String category;

    @Column
    private String status;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private double currentBid;

    @Column
    private double startingBid;

    @Column
    private double increment;

    @Column
    private int bidCount;

    @Column
    private int participants;

    @Column
    private String auctioneerId;

    @Column
    private String auctioneerName;

    @Column
    private String imageUrl;

    @Column
    private String videoUrl;

    @Column
    private boolean autoExtend;

    @Column
    private String tenantId;

    @Column
    private boolean featured;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getCurrentBid() { return currentBid; }
    public void setCurrentBid(double currentBid) { this.currentBid = currentBid; }

    public double getStartingBid() { return startingBid; }
    public void setStartingBid(double startingBid) { this.startingBid = startingBid; }

    public double getIncrement() { return increment; }
    public void setIncrement(double increment) { this.increment = increment; }

    public int getBidCount() { return bidCount; }
    public void setBidCount(int bidCount) { this.bidCount = bidCount; }

    public int getParticipants() { return participants; }
    public void setParticipants(int participants) { this.participants = participants; }

    public String getAuctioneerId() { return auctioneerId; }
    public void setAuctioneerId(String auctioneerId) { this.auctioneerId = auctioneerId; }

    public String getAuctioneerName() { return auctioneerName; }
    public void setAuctioneerName(String auctioneerName) { this.auctioneerName = auctioneerName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public boolean isAutoExtend() { return autoExtend; }
    public void setAutoExtend(boolean autoExtend) { this.autoExtend = autoExtend; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
}