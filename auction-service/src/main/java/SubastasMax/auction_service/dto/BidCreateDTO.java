package SubastasMax.auction_service.dto;

import jakarta.validation.constraints.NotBlank;

public class BidCreateDTO {
    
    @NotBlank(message = "Amount is required")
    private String amount;
    
    @NotBlank(message = "Auction ID is required")
    private String auctionId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "User name is required")
    private String userName;
    
    private String status = "ACTIVE";

    public BidCreateDTO() {}

    public BidCreateDTO(String amount, String auctionId, String userId, String userName, String status) {
        this.amount = amount;
        this.auctionId = auctionId;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
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
}