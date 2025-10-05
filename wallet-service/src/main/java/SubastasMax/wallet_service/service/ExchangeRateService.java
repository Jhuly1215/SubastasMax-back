package SubastasMax.wallet_service.service;

import SubastasMax.wallet_service.dto.ExchangeRateRequestDTO;
import SubastasMax.wallet_service.dto.ExchangeRateResponseDTO;
import SubastasMax.wallet_service.exception.ExchangeRateNotFoundException;
import SubastasMax.wallet_service.model.ExchangeRate;
import SubastasMax.wallet_service.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Service
public class ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateResponseDTO updateExchangeRates(ExchangeRateRequestDTO requestDTO) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setDate(now);
            exchangeRate.setRates(requestDTO.getRates());
            exchangeRate.setUpdatedAt(now);

            ExchangeRate savedRate = exchangeRateRepository.save(exchangeRate);

            return mapToResponseDTO(savedRate);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error updating exchange rates: " + e.getMessage(), e);
        }
    }

    public ExchangeRateResponseDTO getLatestExchangeRates() {
        try {
            ExchangeRate exchangeRate = exchangeRateRepository.findLatest();
            
            if (exchangeRate == null) {
                throw new ExchangeRateNotFoundException("No exchange rates found");
            }

            return mapToResponseDTO(exchangeRate);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error retrieving latest exchange rates: " + e.getMessage(), e);
        }
    }

    public ExchangeRateResponseDTO getExchangeRatesByDate(String date) {
        try {
            ExchangeRate exchangeRate = exchangeRateRepository.findByDate(date);
            
            if (exchangeRate == null) {
                throw new ExchangeRateNotFoundException("No exchange rates found for date: " + date);
            }

            return mapToResponseDTO(exchangeRate);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error retrieving exchange rates by date: " + e.getMessage(), e);
        }
    }

    private ExchangeRateResponseDTO mapToResponseDTO(ExchangeRate exchangeRate) {
        return new ExchangeRateResponseDTO(
                exchangeRate.getId(),
                exchangeRate.getDate(),
                exchangeRate.getRates(),
                exchangeRate.getUpdatedAt()
        );
    }
}