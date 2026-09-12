ALTER TABLE password_recovery RENAME COLUMN code_hash TO verification_code;
ALTER TABLE password_recovery RENAME COLUMN reset_token_hash TO reset_token;
ALTER TABLE password_recovery RENAME INDEX uk_password_recovery_reset_token_hash TO uk_password_recovery_reset_token;

ALTER TABLE password_recovery ADD COLUMN status varchar(20);
UPDATE password_recovery SET status = CASE WHEN used_at IS NOT NULL THEN 'USED' ELSE 'REVOKED' END;
ALTER TABLE password_recovery MODIFY COLUMN status varchar(20) NOT NULL;

ALTER TABLE password_recovery ADD COLUMN revoked_at datetime(6);

ALTER TABLE password_recovery ADD COLUMN open_user_id binary(16) GENERATED ALWAYS AS (
    CASE WHEN status IN ('ACTIVE', 'VERIFIED') THEN user_id END
) STORED;
CREATE UNIQUE INDEX uk_password_recovery_open_user ON password_recovery (open_user_id);

-- ROLLBACK
-- DROP INDEX uk_password_recovery_open_user ON password_recovery;
-- ALTER TABLE password_recovery DROP COLUMN open_user_id;
-- ALTER TABLE password_recovery DROP COLUMN revoked_at;
-- ALTER TABLE password_recovery DROP COLUMN status;
-- ALTER TABLE password_recovery RENAME INDEX uk_password_recovery_reset_token TO uk_password_recovery_reset_token_hash;
-- ALTER TABLE password_recovery RENAME COLUMN reset_token TO reset_token_hash;
-- ALTER TABLE password_recovery RENAME COLUMN verification_code TO code_hash;
