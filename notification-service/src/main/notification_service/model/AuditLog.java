package Subastasmax.notificationservice.model;

import fasterxml.jackson.annotation.JsonFormat;
import fasterxml.jackson.annotation.JsonIgnoreProperties;
import fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Modelo para logs de auditoría, serializable para Firebase Firestore.
 * - eventId: usado como document ID en Firestore.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("auctionId")
    private String auctionId;

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("processedBy")
    private String processedBy;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public AuditLog() { }

    public AuditLog(String eventId, String eventType, String auctionId, String payload, String processedBy) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.auctionId = auctionId;
        this.payload = payload;
        this.processedBy = processedBy;
    }

    // Getters / Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog)) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(eventId, auditLog.eventId);
    }

    @Override
    public int hashCode() { return Objects.hashCode(eventId); }

    @Override
    public String toString() {
        return "AuditLog{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", processedBy='" + processedBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}