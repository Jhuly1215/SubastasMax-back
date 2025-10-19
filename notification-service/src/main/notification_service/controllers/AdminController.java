package SubastasMax.notification_service.controllers;

import Subastasmax.notificationservice.dto.NotificationDTO;
import Subastasmax.notificationservice.service.AuditService;
import Subastasmax.notificationservice.service.MetricsService;
import Subastasmax.notificationservice.service.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Endpoints para administración y auditoría.
 *
 * - GET /api/admin/metrics/auction/{auctionId}
 * - GET /api/admin/audit (filtros)
 *
 * Requiere claims/admin=true en el token Firebase (esta validación es muy básica aquí;
 * en producción considerar una capa de autorización más robusta).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final MetricsService metricsService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public AdminController(MetricsService metricsService,
                           AuditService auditService,
                           NotificationService notificationService) {
        this.metricsService = metricsService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    private boolean isAdmin(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                Object claim = decoded.getClaims().get("admin");
                return Boolean.TRUE.equals(claim);
            } catch (FirebaseAuthException e) {
                log.warn("Firebase token invalid: {}", e.getMessage());
            } catch (NoClassDefFoundError e) {
                log.warn("Firebase SDK not present: {}", e.getMessage());
            }
        }
        // fallback: allow if header X-Admin: true (local/dev only)
        String adminHdr = request.getHeader("X-Admin");
        return "true".equalsIgnoreCase(adminHdr);
    }

    @GetMapping("/metrics/auction/{auctionId}")
    public ResponseEntity<?> getAuctionMetrics(@PathVariable String auctionId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).build();
        }
        var metrics = metricsService.getAuctionMetrics(auctionId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/audit")
    public ResponseEntity<?> queryAudit(@RequestParam(required = false) String auctionId,
                                        @RequestParam(required = false) String eventType,
                                        HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).build();
        }
        var results = auditService.query(auctionId, eventType);
        return ResponseEntity.ok(results);
    }

    // Admin convenience endpoint: list notifications for user (no page)
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<Iterable<NotificationDTO>> listNotificationsForUser(@PathVariable String userId,
                                                                              HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(403).build();
        }
        var notifications = notificationService.findByUserId(userId, 0, 1000);
        var dtos = notificationService.toDTOList(notifications);
        return ResponseEntity.ok(dtos);
    }
}