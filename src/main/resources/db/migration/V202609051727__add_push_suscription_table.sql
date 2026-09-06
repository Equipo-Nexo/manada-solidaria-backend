CREATE TABLE push_subscription (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    endpoint VARCHAR(2048) NOT NULL,
    endpoint_hash BINARY(32) NOT NULL,
    p256dh VARCHAR(512) NOT NULL,
    auth VARCHAR(512) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),

    PRIMARY KEY (id),

    CONSTRAINT uk_push_subscription_endpoint_hash
        UNIQUE (endpoint_hash),

    CONSTRAINT fk_push_subscription_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);