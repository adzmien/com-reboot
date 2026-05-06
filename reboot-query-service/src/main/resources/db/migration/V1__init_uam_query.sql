-- Flyway baseline migration: uam_query schema
-- Creates: dashboard_snapshots table for pre-aggregated read models

CREATE TABLE IF NOT EXISTS dashboard_snapshots (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    snapshot_key   VARCHAR(255) NOT NULL,
    snapshot_type  VARCHAR(100) NOT NULL,
    payload        JSON                  DEFAULT NULL,
    generated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Audit columns
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(255)          DEFAULT NULL,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by     VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete
    is_deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at     DATETIME              DEFAULT NULL,
    CONSTRAINT pk_dashboard_snapshots PRIMARY KEY (id),
    CONSTRAINT uk_dashboard_snapshots_key UNIQUE (snapshot_key)
);

CREATE INDEX idx_dashboard_snapshots_type ON dashboard_snapshots (snapshot_type);
CREATE INDEX idx_dashboard_snapshots_generated_at ON dashboard_snapshots (generated_at);
