package org.example.posts.Services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications")
public interface NotificationClient {

    @PostMapping("/notifications")
    void addNotification(@RequestBody NotificationRequest request);
}

