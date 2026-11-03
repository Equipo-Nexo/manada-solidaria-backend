-- Migration: Update foreign keys to cascade deletion from adoption_post down to details
-- Version: 202610260103

-- 1. Foreign Key entre adoption_forms y adoption_post (singular)
ALTER TABLE adoption_forms
DROP FOREIGN KEY fk_adoption_forms_post;

ALTER TABLE adoption_forms
ADD CONSTRAINT fk_adoption_forms_adoption_posts
FOREIGN KEY (adoption_post_id) REFERENCES adoption_post(id)
ON DELETE CASCADE;

-- 2. Foreign Key entre adoption_form_details y adoption_forms
ALTER TABLE adoption_form_details
DROP FOREIGN KEY fk_adoption_form_details_form;

ALTER TABLE adoption_form_details
ADD CONSTRAINT fk_adoption_form_details_adoption_forms
FOREIGN KEY (adoption_form_id) REFERENCES adoption_forms(id)
ON DELETE CASCADE;

-- ============================================================================
-- ROLLBACK SCRIPT
-- ============================================================================
-- ALTER TABLE adoption_forms
-- DROP FOREIGN KEY fk_adoption_forms_adoption_posts;
--
-- ALTER TABLE adoption_forms
-- ADD CONSTRAINT fk_adoption_forms_post
-- FOREIGN KEY (adoption_post_id) REFERENCES adoption_post(id);
--
-- ALTER TABLE adoption_form_details
-- DROP FOREIGN KEY fk_adoption_form_details_adoption_forms;
--
-- ALTER TABLE adoption_form_details
-- ADD CONSTRAINT fk_adoption_form_details_form
-- FOREIGN KEY (adoption_form_id) REFERENCES adoption_forms(id);