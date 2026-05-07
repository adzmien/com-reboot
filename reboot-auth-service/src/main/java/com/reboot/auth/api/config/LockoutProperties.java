package com.reboot.auth.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Typed configuration for account lockout parameters.
 * Bound from {@code auth.lockout.*} properties.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth.lockout")
public class LockoutProperties {

    /** Number of failed login attempts before the account is locked. */
    private int maxAttempts;

    /** Duration of the account lock in minutes. */
    private int lockDurationMinutes;
}
