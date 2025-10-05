package SubastasMax.wallet_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ExchangeRateResponseDTO {
    
    private String id;
    private LocalDateTime date;
    private Map<String, Double> rates;
    private LocalDateTime updatedAt;

    public ExchangeRateResponseDTO() {
    }

    public ExchangeRateResponseDTO(String id, LocalDateTime date, Map<String, Double> rates, LocalDateTime updatedAt) {
        this.id = id;
        this.date = date;
        this.rates = rates;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}