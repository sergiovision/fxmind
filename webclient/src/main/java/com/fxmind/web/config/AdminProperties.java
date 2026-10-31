package com.fxmind.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Optional bootstrap administrator, typically injected via {@code FXMIND_ADMIN_EMAIL} /
 * {@code FXMIND_ADMIN_PASSWORD}. Only used when no administrator exists yet.
 */
@ConfigurationProperties(prefix = "fxmind.admin")
public record AdminProperties(String email, String password) {

    public boolean isConfigured() {
        return email != null && !email.isBlank() && password != null && !password.isBlank();
    }
}
