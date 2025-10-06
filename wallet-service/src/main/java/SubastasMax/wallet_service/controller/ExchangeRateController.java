package SubastasMax.wallet_service.controller;

import SubastasMax.wallet_service.dto.ExchangeRateRequestDTO;
import SubastasMax.wallet_service.dto.ExchangeRateResponseDTO;
import SubastasMax.wallet_service.service.ExchangeRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponseDTO> createExchangeRate(
            @Valid @RequestBody ExchangeRateRequestDTO request) {
        ExchangeRateResponseDTO response = exchangeRateService.createExchangeRate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExchangeRateResponseDTO> updateExchangeRate(
            @PathVariable String id,
            @Valid @RequestBody ExchangeRateRequestDTO request) {
        ExchangeRateResponseDTO response = exchangeRateService.updateExchangeRate(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeRateResponseDTO> getExchangeRate(@PathVariable String id) {
        ExchangeRateResponseDTO response = exchangeRateService.getExchangeRate(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<ExchangeRateResponseDTO> getLatestExchangeRate() {
        ExchangeRateResponseDTO response = exchangeRateService.getLatestExchangeRate();
        return ResponseEntity.ok(response);
    }
}