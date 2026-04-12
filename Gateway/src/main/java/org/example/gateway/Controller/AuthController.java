package org.example.gateway.Controller;

import org.example.gateway.Security.JwtUtil;
import org.example.gateway.Models.LoginRequest;
import org.example.gateway.Models.SiteUser;
import org.example.gateway.Feign.UserClient;
import org.example.gateway.Service.GatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final GatewayService gatewayService;

    public AuthController(JwtUtil jwtUtil, UserClient userClient, PasswordEncoder passwordEncoder, GatewayService gatewayService) {
        this.jwtUtil = jwtUtil;
        this.userClient = userClient;
        this.passwordEncoder = passwordEncoder;
        this.gatewayService = gatewayService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody LoginRequest request){
        return Mono.fromCallable(() -> {
            SiteUser user = userClient.search(request.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not found"));
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid password"));
            }

            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(Map.of("token", token));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<String>> signup(@RequestBody SiteUser siteUser){
        return gatewayService.signup(siteUser);

    }

    @GetMapping("/test")
    public String test() {
        return "Gateway is working";
    }

}
