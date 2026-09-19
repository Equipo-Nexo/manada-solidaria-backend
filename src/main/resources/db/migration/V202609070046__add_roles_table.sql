CREATE TABLE profile_roles (
    profile_id BINARY(16) NOT NULL,
    role VARCHAR(255) NOT NULL,

    PRIMARY KEY (profile_id, role),

    CONSTRAINT fk_profile_roles_profile
        FOREIGN KEY (profile_id)
        REFERENCES profile(id)
        ON DELETE CASCADE
);