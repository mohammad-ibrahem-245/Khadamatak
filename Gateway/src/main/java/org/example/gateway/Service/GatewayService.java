package org.example.gateway.Service;

import org.example.gateway.Feign.UserClient;
import org.example.gateway.Models.SiteUser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class GatewayService {

    private final UserClient userClient;

    public GatewayService(UserClient userClient) {
        this.userClient = userClient;
    }

    public Mono<ResponseEntity<String>> signup(SiteUser siteUser) {
        return Mono.fromCallable(() -> userClient.save(siteUser))
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .body(response.getBody() != null ? response.getBody() : ""))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                        .body("Error: " + e.getMessage())));
    }

}
