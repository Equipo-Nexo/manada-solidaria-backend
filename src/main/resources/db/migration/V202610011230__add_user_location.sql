CREATE TABLE user_location (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    INDEX idx_user_location_user_created_at (user_id, created_at),

    CONSTRAINT fk_user_location_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ROLLBACK (borra el historial de ubicaciones; no se puede restaurar)
-- DROP TABLE user_location;
