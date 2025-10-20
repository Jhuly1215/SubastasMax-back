package SubastasMax.wallet_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    private String transactionId;
    private String fromUserId;
    private String toUserId;
    private String amount;
    private String currency;
    private String type;
    private String status;
    private String description;
    private String requestId;
    
    private Balance balanceBefore;
    private Balance balanceAfter;
    
    private Date createdAt;
    private Date completedAt;
    private Date failedAt;
    
    private String errorMessage;
    private TransactionMetadata metadata;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Balance {
        private Double available;
        private Double frozen;
        private Double total;
        
        public Map<String, Object> toMap() {
            return Map.of(
                "available", available != null ? available : 0.0,
                "frozen", frozen != null ? frozen : 0.0,
                "total", total != null ? total : 0.0
            );
        }
        
        public static Balance fromMap(Map<String, Object> map) {
            if (map == null) return new Balance(0.0, 0.0, 0.0);
            return Balance.builder()
                .available(getDoubleFromMap(map, "available"))
                .frozen(getDoubleFromMap(map, "frozen"))
                .total(getDoubleFromMap(map, "total"))
                .build();
        }
        
        private static Double getDoubleFromMap(Map<String, Object> map, String key) {
            Object value = map.get(key);
            if (value == null) return 0.0;
            if (value instanceof Number) return ((Number) value).doubleValue();
            return 0.0;
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionMetadata {
        private String auctionId;
        private String bidId;
        private String bankAccount;
        private String paymentMethod;
        private String externalTransactionId;
        private String fromCurrency;
        private String toCurrency;
        private String exchangeRate;
        private String convertedAmount;
        
        public Map<String, Object> toMap() {
            Map<String, Object> map = new java.util.HashMap<>();
            if (auctionId != null) map.put("auctionId", auctionId);
            if (bidId != null) map.put("bidId", bidId);
            if (bankAccount != null) map.put("bankAccount", bankAccount);
            if (paymentMethod != null) map.put("paymentMethod", paymentMethod);
            if (externalTransactionId != null) map.put("externalTransactionId", externalTransactionId);
            if (fromCurrency != null) map.put("fromCurrency", fromCurrency);
            if (toCurrency != null) map.put("toCurrency", toCurrency);
            if (exchangeRate != null) map.put("exchangeRate", exchangeRate);
            if (convertedAmount != null) map.put("convertedAmount", convertedAmount);
            return map;
        }
        
        public static TransactionMetadata fromMap(Map<String, Object> map) {
            if (map == null) return null;
            return TransactionMetadata.builder()
                .auctionId((String) map.get("auctionId"))
                .bidId((String) map.get("bidId"))
                .bankAccount((String) map.get("bankAccount"))
                .paymentMethod((String) map.get("paymentMethod"))
                .externalTransactionId((String) map.get("externalTransactionId"))
                .fromCurrency((String) map.get("fromCurrency"))
                .toCurrency((String) map.get("toCurrency"))
                .exchangeRate((String) map.get("exchangeRate"))
                .convertedAmount((String) map.get("convertedAmount"))
                .build();
        }
    }
}