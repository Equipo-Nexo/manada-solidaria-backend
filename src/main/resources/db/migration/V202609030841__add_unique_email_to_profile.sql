ALTER TABLE profile ADD CONSTRAINT uk_profile_email UNIQUE (email);

-- ROLLBACK
-- ALTER TABLE profile DROP INDEX uk_profile_email;
