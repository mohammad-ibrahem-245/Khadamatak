package org.example.gateway.Feign;

import org.example.gateway.Models.SiteUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-api")
public interface UserClient {
    @GetMapping("/search/{username}")
    SiteUser search(@PathVariable("username") String username);

    @PostMapping("/signup")
    ResponseEntity<String> save(@RequestBody SiteUser user);
}

