package SubastasMax.notification_service.service;

import Subastasmax.notificationservice.model.AuctionMetrics;
import java.util.Optional;

/**
 * Interfaz para servicio de métricas.
 */
public interface MetricsService {
    void recordBid(String auctionId, long amount);
    void recordSettlement(String auctionId, String winnerUserId, long amount);
    void recordCapture(String auctionId, long amount);
    Optional<AuctionMetrics> getMetrics(String auctionId);
}