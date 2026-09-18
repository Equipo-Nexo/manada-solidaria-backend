ALTER TABLE notification_status_history
    MODIFY COLUMN status ENUM ('FAILED', 'PENDING', 'READ', 'SENT') NOT NULL;

-- ROLLBACK
-- Ojo: revertir solo es seguro si todavia no se marco ninguna notificacion como leida.
-- Con filas en READ, MySQL las trunca y se pierde el estado.
-- ALTER TABLE notification_status_history
--     MODIFY COLUMN status ENUM ('FAILED', 'PENDING', 'SENT') NOT NULL;
