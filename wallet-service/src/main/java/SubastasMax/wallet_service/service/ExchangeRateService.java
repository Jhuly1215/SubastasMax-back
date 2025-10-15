package SubastasMax.wallet_service.service;

import SubastasMax.wallet_service.dto.ExchangeRateRequestDTO;
import SubastasMax.wallet_service.dto.ExchangeRateResponseDTO;
import SubastasMax.wallet_service.model.ExchangeRate;
import SubastasMax.wallet_service.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public ExchangeRateResponseDTO createExchangeRate(ExchangeRateRequestDTO request) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRates(request.getRates());
        
        ExchangeRate saved = exchangeRateRepository.save(exchangeRate);
        return toResponseDTO(saved);
    }

    public ExchangeRateResponseDTO updateExchangeRate(String id, ExchangeRateRequestDTO request) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRates(request.getRates());
        
        ExchangeRate updated = exchangeRateRepository.update(id, exchangeRate);
        return toResponseDTO(updated);
    }

    public ExchangeRateResponseDTO getExchangeRate(String id) {
        ExchangeRate exchangeRate = exchangeRateRepository.findById(id);
        return toResponseDTO(exchangeRate);
    }

    public ExchangeRateResponseDTO getLatestExchangeRate() {
        ExchangeRate exchangeRate = exchangeRateRepository.findLatest();
        return toResponseDTO(exchangeRate);
    }

    private ExchangeRateResponseDTO toResponseDTO(ExchangeRate exchangeRate) {
        // Convertir explícitamente todos los valores a Double
        Map<String, Double> convertedRates = convertRatesToDouble(exchangeRate.getRates());
        
        return new ExchangeRateResponseDTO(
            exchangeRate.getId(),
            exchangeRate.getDate(),
            convertedRates,
            exchangeRate.getUpdatedAt()
        );
    }
    
    private Map<String, Double> convertRatesToDouble(Map<String, Double> rates) {
        if (rates == null) {
            return new HashMap<>();
        }
        
        Map<String, Double> convertedRates = new HashMap<>();
        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            Double value = entry.getValue();
            // Asegurarse de que el valor es realmente Double
            convertedRates.put(entry.getKey(), value != null ? value : 0.0);
        }
        
        return convertedRates;
    }
}