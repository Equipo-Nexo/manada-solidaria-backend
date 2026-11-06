CREATE TABLE terms_acceptance (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    terms_version VARCHAR(20) NOT NULL,
    accepted_at DATETIME(6) NOT NULL,

    CONSTRAINT fk_terms_acceptance_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ROLLBACK (borra el registro de aceptaciones; no se puede restaurar)
-- DROP TABLE terms_acceptance;
