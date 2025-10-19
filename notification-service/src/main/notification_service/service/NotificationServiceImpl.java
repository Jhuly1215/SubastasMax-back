package SubastasMax.notification_service.service;

import Subastasmax.notificationservice.model.Notification;
import Subastasmax.notificationservice.model.NotificationType;
import Subastasmax.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para manejar notificaciones usando Firebase.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification createNotification(String userId, NotificationType type, String title, String message, String metadata) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title != null ? title : type.defaultTitle());
        notification.setMessage(message);
        notification.setMetadata(metadata);

        repository.save(notification);
        log.info("Notification created for user {}: {}", userId, notification.getId());
        return notification;
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId, int limit) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId, limit);
    }

    @Override
    public void markAsRead(String userId, String notificationId) {
        repository.markAsRead(userId, notificationId);
        log.info("Notification {} marked as read for user {}", notificationId, userId);
    }

    @Override
    public long getUnreadCount(String userId) {
        return repository.countUnreadByUserId(userId);
    }
}