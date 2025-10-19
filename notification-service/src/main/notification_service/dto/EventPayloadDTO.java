package SubastasMax.notification_service.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO de eventos que vienen de auction-service / settlement-service / wallet-service.
 * - type: auction.winner.decided | wallet.captured | wallet.released | bid.outbid | payment.accredited ...
 * - auctionId: id de la subasta afectada
 * - winnerUserId: usuario ganador (si aplica)
 * - losersUserIds: lista de perdedores (si aplica)
 * - amount: monto afectado
 * - eventId: id único del evento (para idempotencia)
 * - details: mapa libre para payload adicional
 */
public class EventPayloadDTO {
    private String type;
    private String auctionId;
    private String winnerUserId;
    private List<String> losersUserIds;
    private Long amount;
    private String eventId;
    private Map<String, Object> details;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }
    public String getWinnerUserId() { return winnerUserId; }
    public void setWinnerUserId(String winnerUserId) { this.winnerUserId = winnerUserId; }
    public List<String> getLosersUserIds() { return losersUserIds; }
    public void setLosersUserIds(List<String> losersUserIds) { this.losersUserIds = losersUserIds; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}