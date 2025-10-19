package SubastasMax.notification_service.service;

import Subastasmax.notificationservice.model.Notification;
import Subastasmax.notificationservice.model.NotificationType;
import java.util.List;

/**
 * Interfaz para servicio de notificaciones.
 */
public interface NotificationService {
    Notification createNotification(String userId, NotificationType type, String title, String message, String metadata);
    List<Notification> getNotificationsForUser(String userId, int limit);
    void markAsRead(String userId, String notificationId);
    long getUnreadCount(String userId);
}