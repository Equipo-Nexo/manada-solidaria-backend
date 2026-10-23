ALTER TABLE notification_delivery
    DROP FOREIGN KEY fk_notification_delivery_notification;

-- Se agrega el payload como columna que tiene toda la informacion de la notificación enviada
ALTER TABLE notification_delivery
    DROP COLUMN notification_id,
    DROP COLUMN title,
    DROP COLUMN message,
    DROP COLUMN redirect_to,
    ADD COLUMN payload JSON NULL;