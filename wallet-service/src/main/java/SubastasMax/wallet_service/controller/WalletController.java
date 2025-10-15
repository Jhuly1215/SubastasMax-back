package SubastasMax.wallet_service.controller;

import SubastasMax.wallet_service.dto.WalletRequestDTO;
import SubastasMax.wallet_service.dto.WalletResponseDTO;
import SubastasMax.wallet_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createWallet(@Valid @RequestBody WalletRequestDTO requestDTO) {
        WalletResponseDTO wallet = walletService.createWallet(requestDTO);
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponseDTO> getWallet(@PathVariable String userId) {
        WalletResponseDTO wallet = walletService.getWallet(userId);
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<WalletResponseDTO> updateWallet(
            @PathVariable String userId,
            @Valid @RequestBody WalletRequestDTO requestDTO) {
        WalletResponseDTO wallet = walletService.updateWallet(userId, requestDTO);
        return ResponseEntity.ok(wallet);
    }
}