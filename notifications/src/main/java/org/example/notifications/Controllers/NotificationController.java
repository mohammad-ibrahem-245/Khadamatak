package org.example.notifications.Controllers;

import org.example.notifications.Models.Notification;
import org.example.notifications.Services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> add(@RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.add(notification));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> findByUserId(
            @PathVariable Long userId,
            @RequestHeader("X-User-Id") Long currentUserId) {

        return ResponseEntity.ok(notificationService.findAllByUserId(userId, currentUserId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return notificationService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

