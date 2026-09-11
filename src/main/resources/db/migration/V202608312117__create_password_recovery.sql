CREATE TABLE password_recovery (
    id binary(16) NOT NULL,
    user_id binary(16) NOT NULL,
    code_hash varchar(255) NOT NULL,
    created_at datetime(6),
    expires_at datetime(6),
    attempts int NOT NULL,
    verified_at datetime(6),
    reset_token_hash varchar(255),
    reset_token_expires_at datetime(6),
    used_at datetime(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_password_recovery_reset_token_hash UNIQUE (reset_token_hash),
    CONSTRAINT fk_password_recovery_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ROLLBACK
-- DROP TABLE password_recovery;
