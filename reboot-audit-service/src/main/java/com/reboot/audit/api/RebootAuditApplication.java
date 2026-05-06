package com.reboot.audit.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Reboot Audit Service.
 * Maintains an immutable audit log of domain events across all services.
 */
@EnableJpaAuditing
@SpringBootApplication
public class RebootAuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebootAuditApplication.class, args);
    }
}
