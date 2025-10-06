package SubastasMax.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

public class WalletRequestDTO {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "defaultCurrency is required")
    private String defaultCurrency;

    private Map<String, BalanceDTO> balances;
    
    private Map<String, ActiveBidDTO> activeBids;

    public WalletRequestDTO() {
        this.balances = new HashMap<>();
        this.activeBids = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Map<String, BalanceDTO> getBalances() {
        return balances;
    }

    public void setBalances(Map<String, BalanceDTO> balances) {
        this.balances = balances;
    }

    public Map<String, ActiveBidDTO> getActiveBids() {
        return activeBids;
    }

    public void setActiveBids(Map<String, ActiveBidDTO> activeBids) {
        this.activeBids = activeBids;
    }

    public static class BalanceDTO {
        private Double available;
        private Double frozen;
        private Double total;

        public BalanceDTO() {
            this.available = 0.0;
            this.frozen = 0.0;
            this.total = 0.0;
        }

        public Double getAvailable() {
            return available;
        }

        public void setAvailable(Double available) {
            this.available = available;
        }

        public Double getFrozen() {
            return frozen;
        }

        public void setFrozen(Double frozen) {
            this.frozen = frozen;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }
    }

    public static class ActiveBidDTO {
        private String auctionId;
        private Double bidAmount;
        private String currency;
        private Long frozenAt;

        public String getAuctionId() {
            return auctionId;
        }

        public void setAuctionId(String auctionId) {
            this.auctionId = auctionId;
        }

        public Double getBidAmount() {
            return bidAmount;
        }

        public void setBidAmount(Double bidAmount) {
            this.bidAmount = bidAmount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Long getFrozenAt() {
            return frozenAt;
        }

        public void setFrozenAt(Long frozenAt) {
            this.frozenAt = frozenAt;
        }
    }
}