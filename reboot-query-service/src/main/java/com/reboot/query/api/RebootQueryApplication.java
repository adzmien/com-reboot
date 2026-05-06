package com.reboot.query.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Reboot Query Service.
 * Serves pre-aggregated read models and dashboard endpoints (CQRS read side).
 */
@EnableJpaAuditing
@SpringBootApplication
public class RebootQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebootQueryApplication.class, args);
    }
}
