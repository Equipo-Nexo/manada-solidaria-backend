ALTER TABLE users ADD COLUMN created_at datetime(6);

UPDATE users SET created_at = NOW() WHERE created_at IS NULL;
