package Subastasmax.notificationservice.model;

import fasterxml.jackson.annotation.JsonFormat;
import fasterxml.jackson.annotation.JsonIgnoreProperties;
import fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Modelo para métricas de subastas, serializable para Firebase Firestore.
 * - auctionId: usado como document ID en Firestore.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionMetrics implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("auctionId")
    private String auctionId;

    @JsonProperty("totalBids")
    private Long totalBids = 0L;

    @JsonProperty("totalValue")
    private Long totalValue = 0L;

    @JsonProperty("winnerUserId")
    private String winnerUserId;

    @JsonProperty("capturedAmount")
    private Long capturedAmount = 0L;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @JsonProperty("updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public AuctionMetrics() { }

    public AuctionMetrics(String auctionId) {
        this.auctionId = auctionId;
    }

    // Business helpers
    public synchronized void incrementBid(long amount) {
        this.totalBids = (this.totalBids == null ? 0L : this.totalBids) + 1L;
        this.totalValue = (this.totalValue == null ? 0L : this.totalValue) + amount;
        this.updatedAt = OffsetDateTime.now();
    }

    public synchronized void recordSettlement(String winnerUserId, long amount) {
        this.winnerUserId = winnerUserId;
        this.totalBids = (this.totalBids == null ? 0L : this.totalBids);
        this.totalValue = (this.totalValue == null ? 0L : this.totalValue) + amount;
        this.updatedAt = OffsetDateTime.now();
    }

    public synchronized void recordCapture(long amount) {
        this.capturedAmount = (this.capturedAmount == null ? 0L : this.capturedAmount) + amount;
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters / Setters
    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }

    public Long getTotalBids() { return totalBids; }
    public void setTotalBids(Long totalBids) { this.totalBids = totalBids; }

    public Long getTotalValue() { return totalValue; }
    public void setTotalValue(Long totalValue) { this.totalValue = totalValue; }

    public String getWinnerUserId() { return winnerUserId; }
    public void setWinnerUserId(String winnerUserId) { this.winnerUserId = winnerUserId; }

    public Long getCapturedAmount() { return capturedAmount; }
    public void setCapturedAmount(Long capturedAmount) { this.capturedAmount = capturedAmount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuctionMetrics)) return false;
        AuctionMetrics that = (AuctionMetrics) o;
        return Objects.equals(auctionId, that.auctionId);
    }

    @Override
    public int hashCode() { return Objects.hashCode(auctionId); }

    @Override
    public String toString() {
        return "AuctionMetrics{" +
                "auctionId='" + auctionId + '\'' +
                ", totalBids=" + totalBids +
                ", totalValue=" + totalValue +
                ", winnerUserId='" + winnerUserId + '\'' +
                ", capturedAmount=" + capturedAmount +
                '}';
    }
}