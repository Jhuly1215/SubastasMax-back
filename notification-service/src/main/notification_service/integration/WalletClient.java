package SubastasMax.notification_service.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client stub para wallet-service. Definir métodos necesarios según integración.
 * La URL puede pilotearse vía configuración (balance, tx lookup, etc).
 * En el application.yml se puede configurar services.wallet.url.
 */
@FeignClient(name = "wallet-service", url = "${services.wallet.url:}", decode404 = true)
public interface WalletClient {

    @GetMapping("/api/wallets/users/{userId}/balance")
    Long getBalance(@PathVariable("userId") String userId);

    // Ejemplo: obtener detalle de captura/transacción
    @GetMapping("/api/wallets/tx/{txId}")
    Object getTransaction(@PathVariable("txId") String txId);
}