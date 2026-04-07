package org.example.userapi.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HashConfig {

    @Bean
    public BCrypt.Hasher passwordHasher() {
        return BCrypt.withDefaults();
    }
}
