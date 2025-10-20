package SubastasMax.wallet_service.dto;

import java.util.Map;

public class WalletResponseDTO {

    private String userId;
    private String defaultCurrency;
    private Map<String, BalanceDTO> balances;
    private Map<String, ActiveBidDTO> activeBids;
    private Long createdAt;
    private Long updatedAt;

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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class BalanceDTO {
        private Double available;
        private Double frozen;
        private Double total;

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