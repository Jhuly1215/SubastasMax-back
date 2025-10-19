package SubastasMax.notification_service.controllers;

import Subastasmax.notificationservice.dto.EventPayloadDTO;
import Subastasmax.notificationservice.service.SettlementEventHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Webhook HTTP para recibir eventos de otros microservicios (alternativa a Kafka).
 * En producción se recomienda consumir topics en Kafka en lugar de usar HTTP para alta carga.
 *
 * Endpoint: POST /api/events
 * Payload: EventPayloadDTO (tipo, auctionId, winnerUserId, losers[], amount, eventId, details)
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final SettlementEventHandler settlementEventHandler;

    public EventController(SettlementEventHandler settlementEventHandler) {
        this.settlementEventHandler = settlementEventHandler;
    }

    private String resolveServiceIdentity(HttpServletRequest request) {
        // Validar que quien llama es un servicio autorizado (auction-service / settlement-service)
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                return decoded.getUid();
            } catch (FirebaseAuthException e) {
                log.warn("Firebase token invalid for service call: {}", e.getMessage());
            } catch (NoClassDefFoundError e) {
                log.warn("Firebase SDK not present: {}", e.getMessage());
            }
        }
        // fallback to a special header for local: X-Service-Name
        return request.getHeader("X-Service-Name");
    }

    @PostMapping
    public ResponseEntity<Void> receiveEvent(@RequestBody EventPayloadDTO payload,
                                             HttpServletRequest request) {
        String caller = resolveServiceIdentity(request);
        if (caller == null) {
            log.warn("Unauthorized event sender");
            return ResponseEntity.status(401).build();
        }
        log.info("Event received from {}: type={}, auctionId={}", caller, payload.getType(), payload.getAuctionId());
        // Dispatch to handler (handler debe ser idempotente)
        settlementEventHandler.handleEvent(payload);
        return ResponseEntity.accepted().build();
    }
}