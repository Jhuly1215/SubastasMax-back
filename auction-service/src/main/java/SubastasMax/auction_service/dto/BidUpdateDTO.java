package SubastasMax.auction_service.dto;

public class BidUpdateDTO {
    
    private String amount;
    private String status;

    public BidUpdateDTO() {}

    public BidUpdateDTO(String amount, String status) {
        this.amount = amount;
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
