package com.reboot.uam.api.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Activates JPA auditing only when a JPA EntityManagerFactory is present.
 * Keeping this separate from the main application class allows test contexts
 * that exclude JPA auto-configuration to start without errors.
 */
@Configuration
@ConditionalOnBean(EntityManagerFactory.class)
@EnableJpaAuditing
public class JpaAuditingConfig {
}
