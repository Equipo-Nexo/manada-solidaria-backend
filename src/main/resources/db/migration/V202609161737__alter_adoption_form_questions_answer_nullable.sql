ALTER TABLE adoption_form_questions
    MODIFY COLUMN answer VARCHAR(1000) NULL;

-- Rollback:
-- ALTER TABLE adoption_form_questions
--     MODIFY COLUMN answer VARCHAR(1000) NOT NULL;