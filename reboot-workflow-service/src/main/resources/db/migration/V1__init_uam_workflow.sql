-- Flyway baseline migration: uam_workflow schema
-- Creates: workflow_instances and workflow_steps tables

CREATE TABLE IF NOT EXISTS workflow_instances (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    reference_id  VARCHAR(255) NOT NULL,
    workflow_type VARCHAR(100) NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    -- Audit columns
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    VARCHAR(255)          DEFAULT NULL,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by    VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at    DATETIME              DEFAULT NULL,
    CONSTRAINT pk_workflow_instances PRIMARY KEY (id),
    CONSTRAINT uk_workflow_instances_reference_id UNIQUE (reference_id)
);

CREATE INDEX idx_workflow_instances_status ON workflow_instances (status);
CREATE INDEX idx_workflow_instances_type   ON workflow_instances (workflow_type);

CREATE TABLE IF NOT EXISTS workflow_steps (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    workflow_instance_id BIGINT       NOT NULL,
    step_name            VARCHAR(100) NOT NULL,
    status               VARCHAR(50)  NOT NULL,
    attempt_count        INT          NOT NULL DEFAULT 0,
    error_message        TEXT                  DEFAULT NULL,
    -- Audit columns
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by           VARCHAR(255)          DEFAULT NULL,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by           VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete
    is_deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at           DATETIME              DEFAULT NULL,
    CONSTRAINT pk_workflow_steps PRIMARY KEY (id),
    CONSTRAINT fk_workflow_steps_instance FOREIGN KEY (workflow_instance_id) REFERENCES workflow_instances (id)
);

CREATE INDEX idx_workflow_steps_instance_id ON workflow_steps (workflow_instance_id);
CREATE INDEX idx_workflow_steps_status      ON workflow_steps (status);
