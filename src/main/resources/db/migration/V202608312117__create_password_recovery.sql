CREATE TABLE password_recovery (
    id binary(16) NOT NULL,
    user_id binary(16) NOT NULL,
    verification_code varchar(255) NOT NULL,
    status varchar(20) NOT NULL,
    created_at datetime(6),
    expires_at datetime(6),
    attempts int NOT NULL,
    verified_at datetime(6),
    reset_token varchar(255),
    reset_token_expires_at datetime(6),
    used_at datetime(6),
    open_user_id binary(16) GENERATED ALWAYS AS (
        CASE WHEN status IN ('ACTIVE', 'VERIFIED') THEN user_id END
    ) STORED,
    PRIMARY KEY (id),
    CONSTRAINT uk_password_recovery_reset_token UNIQUE (reset_token),
    CONSTRAINT uk_password_recovery_open_user UNIQUE (open_user_id),
    CONSTRAINT fk_password_recovery_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ROLLBACK
-- DROP TABLE password_recovery;
