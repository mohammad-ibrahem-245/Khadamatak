package org.example.notifications.Services;

import org.example.notifications.Models.Notification;
import org.example.notifications.Repositories.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification add(Notification notification) {
        notification.setId(null);
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(new Date());
        }
        return notificationRepository.save(notification);
    }

    public List<Notification> findAllByUserId(Long userId, Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own notifications");
        }
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public boolean delete(Long id) {
        if (!notificationRepository.existsById(id)) {
            return false;
        }
        notificationRepository.deleteById(id);
        return true;
    }
}

