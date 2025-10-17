package SubastasMax.auction_service.controllers;

import SubastasMax.auction_service.model.Notification;
import SubastasMax.auction_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // simple auth assumption: caller provides userId (or use auth token to extract)
    @GetMapping
    public Page<Notification> list(@RequestParam Long userId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return notificationService.listNotifications(userId, page, size);
    }

    @PostMapping("/{id}/mark-read")
    public Notification markRead(@PathVariable Long id) {
        return notificationService.markAsRead(id).orElseThrow(() -> new RuntimeException("Notification not found"));
    }
}