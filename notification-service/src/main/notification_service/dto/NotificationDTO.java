package SubastasMax.notification_service.dto;

import Subastasmax.notificationservice.model.NotificationType;

import java.time.OffsetDateTime;

/**
 * DTO simplificado para exponer notificaciones en el feed del usuario.
 */
public class NotificationDTO {
    private Long id;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private boolean readFlag;
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}