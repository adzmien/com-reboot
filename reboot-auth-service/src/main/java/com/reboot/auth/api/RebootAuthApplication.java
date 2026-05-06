package com.reboot.auth.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Reboot Authentication Service.
 * Handles login, logout, token refresh, and account lock-out.
 */
@EnableJpaAuditing
@SpringBootApplication
public class RebootAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebootAuthApplication.class, args);
    }
}
