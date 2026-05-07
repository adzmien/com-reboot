package com.reboot.auth.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Typed configuration for JWT issuance parameters.
 * Bound from {@code jwt.*} properties.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HMAC-SHA256 signing secret; injected from {@code JWT_SECRET} env var in prod. */
    private String secret;

    /** Access token time-to-live in minutes. */
    private int accessTokenTtlMinutes;

    /** Refresh token time-to-live in days. */
    private int refreshTokenTtlDays;
}
