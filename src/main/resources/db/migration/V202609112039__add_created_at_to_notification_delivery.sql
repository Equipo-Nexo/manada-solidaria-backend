ALTER TABLE notification_delivery
ADD COLUMN created_at DATETIME(6) NOT NULL;

-- ALTER TABLE notification_delivery DROP COLUMN created_at;