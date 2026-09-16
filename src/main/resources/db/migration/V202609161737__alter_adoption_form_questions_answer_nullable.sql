ALTER TABLE adoption_form_questions
    ALTER COLUMN answer DROP NOT NULL;

-- ROLLBACK
-- ALTER TABLE adoption_form_questions
--     MODIFY answer VARCHAR(1000) NOT NULL;