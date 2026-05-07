package com.reboot.uam.lib.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for integration tests that require a live MariaDB instance.
 * <p>
 * Connects to the shared K8s-hosted MariaDB via the NodePort configured in
 * {@code application.properties} ({@code INFRA_HOST:100.66.8.44}, port 30306).
 * No additional setup is required in subclasses — the datasource is
 * auto-configured by Spring Boot from the service's {@code application.properties}.
 * <p>
 * Flyway is disabled and DDL is set to {@code none} so the test schema is not
 * modified at startup; subclasses manage their own test tables via JdbcTemplate
 * or JPA and are expected to clean up after themselves.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static void configureTest(DynamicPropertyRegistry registry) {
        // Pin credentials so any local REBOOT_DB_* env vars don't shadow the K8s defaults.
        registry.add("spring.datasource.username", () -> "rebootuser");
        registry.add("spring.datasource.password", () -> "abc@123");
        // Disable Flyway — tests manage their own tables directly
        registry.add("spring.flyway.enabled", () -> "false");
        // Skip schema validation — no entities exist yet in this slice
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        // Point Kafka to a non-connectable address so the producer bean starts without blocking
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:19999");
    }
}
