ALTER TABLE lost_post_status_history
    MODIFY COLUMN status ENUM ('CREATED', 'EXPIRED', 'FOUND', 'RESCUED', 'SEARCHING', 'TO_RESCUE') NOT NULL;

ALTER TABLE adoption_post_status_history
    MODIFY COLUMN status ENUM ('ADOPTED', 'CREATED', 'EXPIRED', 'SEARCHING_ADOPT', 'SEARCHING_ADOPT_AND_TRANSIT') NOT NULL;

-- ROLLBACK
-- Ojo: revertir solo es seguro si todavia no expiro ninguna publicacion.
-- Con filas en EXPIRED el ALTER falla (o las trunca si MySQL no esta en modo estricto):
-- antes hay que decidir a que estado vuelven esas publicaciones.
-- ALTER TABLE lost_post_status_history
--     MODIFY COLUMN status ENUM ('CREATED', 'FOUND', 'RESCUED', 'SEARCHING', 'TO_RESCUE') NOT NULL;
-- ALTER TABLE adoption_post_status_history
--     MODIFY COLUMN status ENUM ('ADOPTED', 'CREATED', 'SEARCHING_ADOPT', 'SEARCHING_ADOPT_AND_TRANSIT') NOT NULL;
