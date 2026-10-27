-- V202610040200__add_description_to_adoption_forms.sql
-- Agrega el campo description (motivo de adopción) a la tabla adoption_forms.

ALTER TABLE adoption_forms
ADD COLUMN description VARCHAR(1000) NULL AFTER adoption_post_id;


-- ============================================================
-- ROLLBACK SCRIPT
-- ============================================================
--ALTER TABLE adoption_forms
--DROP COLUMN description;