package org.example.gateway.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.gateway.Models.SiteUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
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
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));


                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String userId = String.valueOf(claims.get("userId"));
                SiteUser.UserRole role = extractRole(claims);

                ServerWebExchange mutated = exchange.mutate()
                        .request(r -> r.headers(h -> {
                            h.set("X-User-Name", username);
                            h.set("X-User-Id", userId);
                            h.set("X-Role", role.name());
                        }))
                        .build();

                return chain.filter(mutated);

            } catch (Exception e) {
                return chain.filter(exchange);
            }
        };
    }

    private SiteUser.UserRole extractRole(Claims claims) {
        Object roleClaim = claims.get("role");
        if (roleClaim != null) {
            return SiteUser.UserRole.valueOf(String.valueOf(roleClaim).toUpperCase());
        }

        boolean isAdmin = Boolean.parseBoolean(String.valueOf(claims.get("isAdmin")));
        boolean isProvider = Boolean.parseBoolean(String.valueOf(claims.get("isProvider")));
        if (isAdmin) {
            return SiteUser.UserRole.ADMIN;
        }
        if (isProvider) {
            return SiteUser.UserRole.PROVIDER;
        }
        return SiteUser.UserRole.USER;
    }
}
