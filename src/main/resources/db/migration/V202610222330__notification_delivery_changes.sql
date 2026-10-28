ALTER TABLE notification_delivery
    DROP FOREIGN KEY fk_notification_delivery_notification;
-- ALTER TABLE notification_delivery ADD COLUMN notification_id BINARY(16) NULL;
-- ALTER TABLE notification_delivery ADD CONSTRAINT fk_notification_delivery_notification FOREIGN KEY (notification_id) REFERENCES notification (id);


-- Se agrega el payload como columna que tiene toda la informacion de la notificación enviada
ALTER TABLE notification_delivery
    DROP COLUMN notification_id,
    DROP COLUMN title,
    DROP COLUMN message,
    DROP COLUMN redirect_to,
    ADD COLUMN payload JSON NULL,
    ADD COLUMN `type` VARCHAR(50) NULL;

-- ALTER TABLE notification_delivery ADD COLUMN title VARCHAR(255) NULL, ADD COLUMN message VARCHAR(255) NULL, ADD COLUMN redirect_to VARCHAR(255) NULL, DROP COLUMN payload, DROP COLUMN `type`;