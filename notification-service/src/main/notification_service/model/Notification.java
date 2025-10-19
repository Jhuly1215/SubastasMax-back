package Subastasmax.notificationservice.model;

import fasterxml.jackson.annotation.JsonFormat;
import fasterxml.jackson.annotation.JsonIgnoreProperties;
import fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Modelo para notificaciones, serializable para Firebase Firestore.
 * - id: string (usado como document ID en Firestore)
 * - Sin anotaciones JPA; compatible con Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("message")
    private String message;

    @JsonProperty("type")
    private NotificationType type = NotificationType.SYSTEM;

    @JsonProperty("readFlag")
    private boolean readFlag = false;

    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonProperty("metadata")
    private String metadata;

    public Notification() {
        this.createdAt = OffsetDateTime.now();
    }

    // Getters / Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isReadFlag() { return readFlag; }
    public void setReadFlag(boolean readFlag) { this.readFlag = readFlag; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // Helpers
    public void markRead() { this.readFlag = true; }
    public void markUnread() { this.readFlag = false; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", readFlag=" + readFlag +
                ", createdAt=" + createdAt +
                '}';
    }
}