-- V202610301500__add_cancelled_status_to_news_campaign_status_history.sql
-- Modifica la columna status de la tabla news_campaign_status_history para soportar el estado CANCELLED.

ALTER TABLE news_campaign_status_history
MODIFY COLUMN status VARCHAR(20) NOT NULL;


-- ============================================================
-- ROLLBACK SCRIPT
-- ============================================================
-- ALTER TABLE news_campaign_status_history
-- MODIFY COLUMN status VARCHAR(10) NOT NULL;