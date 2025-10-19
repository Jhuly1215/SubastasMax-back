package SubastasMax.notification_service.service;

/**
 * Interfaz para servicio de auditoría.
 */
public interface AuditService {
    void recordIfNotExists(String eventId, String eventType, String auctionId, String payload, String processedBy);
}