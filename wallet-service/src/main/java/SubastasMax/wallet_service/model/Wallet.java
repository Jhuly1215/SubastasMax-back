package SubastasMax.wallet_service.model;

import java.util.HashMap;
import java.util.Map;

public class Wallet {

    private String userId;
    private String defaultCurrency;
    private Map<String, Balance> balances;
    private Map<String, ActiveBid> activeBids;
    private Long createdAt;
    private Long updatedAt;

    public Wallet() {
        this.balances = new HashMap<>();
        this.activeBids = new HashMap<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
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

    public Map<String, Balance> getBalances() {
        return balances;
    }

    public void setBalances(Map<String, Balance> balances) {
        this.balances = balances;
    }

    public Map<String, ActiveBid> getActiveBids() {
        return activeBids;
    }

    public void setActiveBids(Map<String, ActiveBid> activeBids) {
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

    public static class Balance {
        private Double available;
        private Double frozen;
        private Double total;

        public Balance() {
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

    public static class ActiveBid {
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