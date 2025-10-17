package SubastasMax.auction_service.service;

import SubastasMax.auction_service.model.Notification;
import SubastasMax.auction_service.repository.NotificationRepository;
import SubastasMax.auction_service.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(Notification n) {
        return notificationRepository.save(n);
    }

    public Page<Notification> listNotifications(Long userId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, p);
    }

    @Transactional
    public Optional<Notification> markAsRead(Long id) {
        Optional<Notification> o = notificationRepository.findById(id);
        o.ifPresent(n -> { n.setReadFlag(true); notificationRepository.save(n); });
        return o;
    }
}