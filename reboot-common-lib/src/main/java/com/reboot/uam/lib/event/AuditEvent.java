package com.reboot.uam.lib.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable envelope for every auditable action in the platform.
 * Published to Kafka and persisted by the audit service.
 *
 * @param auditId      unique identifier for this audit event
 * @param actorId      ID of the user who performed the action
 * @param action       verb describing the action (e.g. {@code USER_CREATED})
 * @param resourceType type of the affected resource (e.g. {@code User})
 * @param resourceId   string representation of the affected resource's ID
 * @param timestamp    wall-clock time when the action was performed
 * @param payload      additional context; KYC-sensitive fields must be masked before inclusion
 */
public record AuditEvent(
        UUID auditId,
        Long actorId,
        String action,
        String resourceType,
        String resourceId,
        Instant timestamp,
        Map<String, Object> payload
) {
}
