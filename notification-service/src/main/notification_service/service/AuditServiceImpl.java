package SubastasMax.notification_service.service;

import Subastasmax.notificationservice.model.AuditLog;
import Subastasmax.notificationservice.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para manejar logs de auditoría usando Firebase.
 */
@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);
    private final AuditLogRepository repository;

    public AuditServiceImpl(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void recordIfNotExists(String eventId, String eventType, String auctionId, String payload, String processedBy) {
        if (!repository.existsByEventId(eventId)) {
            AuditLog auditLog = new AuditLog(eventId, eventType, auctionId, payload, processedBy);
            repository.save(auditLog);
            log.info("Audit log recorded for event {}: type {}", eventId, eventType);
        } else {
            log.debug("Audit log already exists for event {}", eventId);
        }
    }
}