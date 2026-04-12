package org.example.gateway.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtAuthFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public GlobalFilter authenticationFilter() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return chain.filter(exchange);
            }

            try {
                String token = authHeader.substring(7);
                SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");


                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String userId = String.valueOf(claims.get("userId"));
                String isAdmin = String.valueOf(claims.get("isAdmin"));
                String isProvider = String.valueOf(claims.get("isProvider"));

                ServerWebExchange mutated = exchange.mutate()
                        .request(r -> r.headers(h -> {
                            h.set("X-User-Name", username);
                            h.set("X-User-Id", userId);
                            h.set("X-Is-Admin", isAdmin);
                            h.set("X-Is-Provider", isProvider);
                        }))
                        .build();

                return chain.filter(mutated);

            } catch (Exception e) {
                return chain.filter(exchange);
            }
        };
    }
}
