package SubastasMax.wallet_service.controller;

import SubastasMax.wallet_service.dto.ExchangeRateRequestDTO;
import SubastasMax.wallet_service.dto.ExchangeRateResponseDTO;
import SubastasMax.wallet_service.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @PostMapping
    public ResponseEntity<ExchangeRateResponseDTO> updateExchangeRates(
            @Valid @RequestBody ExchangeRateRequestDTO requestDTO) {
        ExchangeRateResponseDTO response = exchangeRateService.updateExchangeRates(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/latest")
    public ResponseEntity<ExchangeRateResponseDTO> getLatestExchangeRates() {
        ExchangeRateResponseDTO response = exchangeRateService.getLatestExchangeRates();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{date}")
    public ResponseEntity<ExchangeRateResponseDTO> getExchangeRatesByDate(@PathVariable String date) {
        ExchangeRateResponseDTO response = exchangeRateService.getExchangeRatesByDate(date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}