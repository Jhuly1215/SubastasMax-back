package SubastasMax.auction_service.dto;

import com.google.cloud.Timestamp;

public class BidResponseDTO {
    
    private String id;
    private String amount;
    private String auctionId;
    private String userId;
    private String userName;
    private String status;
    private Timestamp timestamp;

    public BidResponseDTO() {}

    public BidResponseDTO(String id, String amount, String auctionId, String userId, 
                         String userName, String status, Timestamp timestamp) {
        this.id = id;
        this.amount = amount;
        this.auctionId = auctionId;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}