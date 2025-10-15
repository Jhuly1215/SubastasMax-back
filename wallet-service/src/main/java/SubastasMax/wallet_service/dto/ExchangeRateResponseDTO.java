package SubastasMax.wallet_service.dto;

import com.google.cloud.Timestamp;
import java.util.Map;

public class ExchangeRateResponseDTO {

    private String id;
    private Timestamp date;
    private Map<String, Double> rates;
    private Timestamp updatedAt;

    public ExchangeRateResponseDTO() {}

    public ExchangeRateResponseDTO(String id, Timestamp date, Map<String, Double> rates, Timestamp updatedAt) {
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}