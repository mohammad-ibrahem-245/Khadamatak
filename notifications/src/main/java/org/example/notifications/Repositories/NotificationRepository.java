package org.example.notifications.Repositories;

import org.example.notifications.Models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

