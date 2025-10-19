package SubastasMax.notification_service.service;

import fasterxml.jackson.databind.JsonNode;
import fasterxml.jackson.databind.ObjectMapper;
import Subastasmax.notificationservice.model.NotificationType;
import Subastasmax.notificationservice.service.AuditService;
import Subastasmax.notificationservice.service.MetricsService;
import com.subastasmax.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Handler para eventos de settlement usando Firebase para notificaciones, métricas y auditoría.
 */
@Component
public class SettlementEventHandler {

    private static final Logger log = LoggerFactory.getLogger(SettlementEventHandler.class);
    private final NotificationService notificationService;
    private final MetricsService metricsService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public SettlementEventHandler(NotificationService notificationService, MetricsService metricsService, AuditService auditService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.metricsService = metricsService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.auction}", groupId = "${kafka.group-id}")
    public void handleAuctionEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            String eventId = event.get("eventId").asText();
            String auctionId = event.get("auctionId").asText();
            String payload = message;

            auditService.recordIfNotExists(eventId, eventType, auctionId, payload, "notification-service");

            switch (eventType) {
                case "auction.winner.decided":
                    handleWinnerDecided(event);
                    break;
                case "auction.bid.placed":
                    handleBidPlaced(event);
                    break;
                // Añade más casos según eventos
                default:
                    log.info("Unhandled event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing auction event", e);
        }
    }

    @KafkaListener(topics = "${kafka.topic.wallet}", groupId = "${kafka.group-id}")
    public void handleWalletEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            String eventId = event.get("eventId").asText();
            String auctionId = event.get("auctionId").asText();
            String payload = message;

            auditService.recordIfNotExists(eventId, eventType, auctionId, payload, "notification-service");

            switch (eventType) {
                case "wallet.captured":
                    handleWalletCaptured(event);
                    break;
                case "wallet.released":
                    handleWalletReleased(event);
                    break;
                // Añade más casos
                default:
                    log.info("Unhandled event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing wallet event", e);
        }
    }

    private void handleWinnerDecided(JsonNode event) {
        String winnerUserId = event.get("winnerUserId").asText();
        String auctionId = event.get("auctionId").asText();
        long finalPrice = event.get("finalPrice").asLong();

        notificationService.createNotification(winnerUserId, NotificationType.AUCTION_WIN, null, "¡Felicidades! Ganaste la subasta para " + auctionId, "{\"auctionId\":\"" + auctionId + "\"}");
        metricsService.recordSettlement(auctionId, winnerUserId, finalPrice);
    }

    private void handleBidPlaced(JsonNode event) {
        String bidderUserId = event.get("bidderUserId").asText();
        String auctionId = event.get("auctionId").asText();
        long bidAmount = event.get("bidAmount").asLong();

        notificationService.createNotification(bidderUserId, NotificationType.NEW_BID, null, "Nueva puja en subasta " + auctionId, "{\"auctionId\":\"" + auctionId + "\"}");
        metricsService.recordBid(auctionId, bidAmount);
    }

    private void handleWalletCaptured(JsonNode event) {
        String userId = event.get("userId").asText();
        long amount = event.get("amount").asLong();

        notificationService.createNotification(userId, NotificationType.WALLET_CAPTURED, null, "Fondos capturados: " + amount, null);
        // Asume auctionId si es aplicable, ajusta según payload
    }

    private void handleWalletReleased(JsonNode event) {
        String userId = event.get("userId").asText();
        long amount = event.get("amount").asLong();

        notificationService.createNotification(userId, NotificationType.WALLET_RELEASED, null, "Fondos liberados: " + amount, null);
    }
}