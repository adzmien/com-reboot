-- Flyway baseline migration: uam_core schema
-- Creates: users table with audit columns and soft-delete

CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    full_name  VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    -- Audit columns
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)          DEFAULT NULL,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at DATETIME              DEFAULT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email     ON users (email);
CREATE INDEX idx_users_role      ON users (role);
CREATE INDEX idx_users_is_active ON users (is_active);

-- Customer records table
CREATE TABLE IF NOT EXISTS customer_records (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(50)           DEFAULT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    -- Audit columns
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(255)          DEFAULT NULL,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by  VARCHAR(255)          DEFAULT NULL,
    -- Soft-delete
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at  DATETIME              DEFAULT NULL,
    CONSTRAINT pk_customer_records PRIMARY KEY (id),
    CONSTRAINT uk_customer_records_email UNIQUE (email),
    CONSTRAINT fk_customer_records_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_customer_records_user_id   ON customer_records (user_id);
CREATE INDEX idx_customer_records_email     ON customer_records (email);
CREATE INDEX idx_customer_records_is_active ON customer_records (is_active);
