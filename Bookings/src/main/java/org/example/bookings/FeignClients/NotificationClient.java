package org.example.bookings.FeignClients;

import org.example.bookings.Models.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications")
public interface NotificationClient {

    @PostMapping("/notifications")
    void addNotification(@RequestBody NotificationRequest request);
}

