-- Flyway baseline migration: uam_auth schema
-- Creates: internal_users table with audit columns and soft-delete
-- Seeds:   default admin account (force_password_change = true)

CREATE TABLE IF NOT EXISTS internal_users (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    full_name             VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    hashed_password       VARCHAR(255) NOT NULL,
    role                  VARCHAR(50)  NOT NULL,
    is_active             BOOLEAN      NOT NULL DEFAULT TRUE,
    force_password_change BOOLEAN      NOT NULL DEFAULT FALSE,
    failed_login_attempts INT          NOT NULL DEFAULT 0,
    locked_until          DATETIME              DEFAULT NULL,
    -- Audit columns (managed by JPA AuditingEntityListener on write)
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(255)          DEFAULT NULL,
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by            VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete
    is_deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at            DATETIME              DEFAULT NULL,
    CONSTRAINT pk_internal_users PRIMARY KEY (id),
    CONSTRAINT uk_internal_users_email UNIQUE (email)
);

CREATE INDEX idx_internal_users_email     ON internal_users (email);
CREATE INDEX idx_internal_users_role      ON internal_users (role);
CREATE INDEX idx_internal_users_is_active ON internal_users (is_active);

-- Default admin seed
-- Password: Admin@2024!
-- bcrypt hash (cost 12) generated offline — never log or expose this value
INSERT INTO internal_users (
    full_name,
    email,
    hashed_password,
    role,
    is_active,
    force_password_change,
    failed_login_attempts,
    created_by,
    updated_by
) VALUES (
    'System Administrator',
    'admin@reboot.local',
    '$2a$12$RxBEFkfuQUHr/4YNGmhsOOlM5oP8CGCXKWPCCaoBfDT4j6qlUKKKa',
    'ADMIN',
    TRUE,
    TRUE,
    0,
    'system',
    'system'
);
