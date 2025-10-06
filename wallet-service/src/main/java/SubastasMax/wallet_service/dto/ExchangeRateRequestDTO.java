package SubastasMax.wallet_service.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class ExchangeRateRequestDTO {

    @NotNull(message = "Rates map cannot be null")
    private Map<String, Double> rates;

    public ExchangeRateRequestDTO() {}

    public ExchangeRateRequestDTO(Map<String, Double> rates) {
        this.rates = rates;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}