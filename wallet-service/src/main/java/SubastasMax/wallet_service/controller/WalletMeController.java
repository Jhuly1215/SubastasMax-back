// wallet-service/src/main/java/SubastasMax/wallet_service/controller/WalletMeController.java
package SubastasMax.wallet_service.controller;

import SubastasMax.wallet_service.dto.WalletRequestDTO;
import SubastasMax.wallet_service.dto.WalletResponseDTO;
import SubastasMax.wallet_service.service.WalletService;
import SubastasMax.wallet_service.security.FirebaseUserAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets/me")
public class WalletMeController {

    private final WalletService walletService;

    public WalletMeController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponseDTO> createMyWallet(FirebaseUserAuthentication auth,
                                                            @RequestBody(required = false) WalletRequestDTO body) {
        String uid = (String) auth.getPrincipal();
        WalletRequestDTO req = body != null ? body : new WalletRequestDTO();
        req.setUserId(uid); // fuerza uid del token
        if (req.getDefaultCurrency() == null) req.setDefaultCurrency("BOB");
        return ResponseEntity.ok(walletService.createWallet(req));
    }

    @GetMapping
    public ResponseEntity<WalletResponseDTO> getMyWallet(FirebaseUserAuthentication auth) {
        String uid = (String) auth.getPrincipal();
        return ResponseEntity.ok(walletService.getWallet(uid));
    }

    @PutMapping
    public ResponseEntity<WalletResponseDTO> updateMyWallet(FirebaseUserAuthentication auth,
                                                            @RequestBody WalletRequestDTO body) {
        String uid = (String) auth.getPrincipal();
        body.setUserId(uid);
        return ResponseEntity.ok(walletService.updateWallet(uid, body));
    }
}
