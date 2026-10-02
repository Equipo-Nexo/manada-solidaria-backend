ALTER TABLE users
    ADD COLUMN latitude DOUBLE NULL,
    ADD COLUMN longitude DOUBLE NULL,
    ADD COLUMN location_updated_at DATETIME(6) NULL;

-- ROLLBACK (borra las ubicaciones guardadas; no se pueden restaurar)
-- ALTER TABLE users
--     DROP COLUMN latitude,
--     DROP COLUMN longitude,
--     DROP COLUMN location_updated_at;
