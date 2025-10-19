package SubastasMax.notification_service.controllers;

import Subastasmax.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para notificaciones usando Firebase.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userId,
                                                               @RequestParam(defaultValue = "10") int limit) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId, limit);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{userId}/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String userId, @PathVariable String notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }
}