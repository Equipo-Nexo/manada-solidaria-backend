ALTER TABLE notification_delivery
    ADD COLUMN title       VARCHAR(255),
    ADD COLUMN message     VARCHAR(255),
    ADD COLUMN redirect_to VARCHAR(255);

UPDATE notification_delivery delivery
    JOIN notification template ON template.id = delivery.notification_id
SET delivery.title       = template.title,
    delivery.message     = template.message,
    delivery.redirect_to = template.redirect_to;

-- ROLLBACK
-- Ojo: el backfill copia el texto de la plantilla, que puede tener los parametros
-- sin resolver. Las entregas anteriores a esta migracion quedan con el placeholder;
-- las nuevas guardan el texto ya resuelto.
-- ALTER TABLE notification_delivery
--     DROP COLUMN title,
--     DROP COLUMN message,
--     DROP COLUMN redirect_to;
