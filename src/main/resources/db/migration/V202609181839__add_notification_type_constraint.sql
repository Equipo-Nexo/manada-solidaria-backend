ALTER TABLE notification
ADD CONSTRAINT uk_notification_type UNIQUE (type);

-- ROLLBACK
-- ALTER TABLE notification DROP INDEX uk_notification_type;