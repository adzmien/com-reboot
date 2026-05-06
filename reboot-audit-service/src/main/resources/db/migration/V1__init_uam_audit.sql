-- Flyway baseline migration: uam_audit schema
-- Creates: audit_events table (immutable; soft-delete columns kept for schema consistency)

CREATE TABLE IF NOT EXISTS audit_events (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    event_type   VARCHAR(100) NOT NULL,
    actor_id     BIGINT                DEFAULT NULL,
    actor_email  VARCHAR(255)          DEFAULT NULL,
    target_type  VARCHAR(100) NOT NULL,
    target_id    VARCHAR(255) NOT NULL,
    payload      JSON                  DEFAULT NULL,
    -- Audit columns
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(255)          DEFAULT NULL,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by   VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete (schema convention; audit records are logically immutable)
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at   DATETIME              DEFAULT NULL,
    CONSTRAINT pk_audit_events PRIMARY KEY (id)
);

CREATE INDEX idx_audit_events_event_type  ON audit_events (event_type);
CREATE INDEX idx_audit_events_actor_id    ON audit_events (actor_id);
CREATE INDEX idx_audit_events_target_id   ON audit_events (target_id);
CREATE INDEX idx_audit_events_created_at  ON audit_events (created_at);
