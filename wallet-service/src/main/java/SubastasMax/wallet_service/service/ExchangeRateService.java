package SubastasMax.wallet_service.service;

import SubastasMax.wallet_service.dto.ExchangeRateRequestDTO;
import SubastasMax.wallet_service.dto.ExchangeRateResponseDTO;
import SubastasMax.wallet_service.model.ExchangeRate;
import SubastasMax.wallet_service.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;

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
        return new ExchangeRateResponseDTO(
            exchangeRate.getId(),
            exchangeRate.getDate(),
            exchangeRate.getRates(),
            exchangeRate.getUpdatedAt()
        );
    }
}