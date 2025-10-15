package SubastasMax.wallet_service.model;

import com.google.cloud.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRate {

    private String id;
    private Timestamp date;
    private Map<String, Double> rates;
    private Timestamp updatedAt;

    public ExchangeRate() {
        this.rates = new HashMap<>();
        initializeDefaultRates();
    }

    public ExchangeRate(String id, Timestamp date, Map<String, Double> rates, Timestamp updatedAt) {
        this.id = id;
        this.date = date;
        this.rates = rates != null ? rates : new HashMap<>();
        this.updatedAt = updatedAt;
        if (this.rates.isEmpty()) {
            initializeDefaultRates();
        }
    }

    private void initializeDefaultRates() {
        this.rates.put("BOB_EUR", 0.0);
        this.rates.put("BOB_USD", 0.0);
        this.rates.put("EUR_BOB", 0.0);
        this.rates.put("EUR_USD", 0.0);
        this.rates.put("USD_BOB", 0.0);
        this.rates.put("USD_EUR", 0.0);
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