CREATE TABLE adoption_forms (
    id BINARY(16) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    applicant_id BINARY(16) NOT NULL,
    adoption_post_id BINARY(16) NOT NULL,
    area_code VARCHAR(4),
    phone_number VARCHAR(7),
    PRIMARY KEY (id),
    CONSTRAINT fk_adoption_forms_applicant FOREIGN KEY (applicant_id) REFERENCES users (id),
    CONSTRAINT fk_adoption_forms_post FOREIGN KEY (adoption_post_id) REFERENCES animal_post (id)
);

CREATE TABLE adoption_form_questions (
    adoption_form_id BINARY(16) NOT NULL,
    question VARCHAR(500) NOT NULL,
    answer VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_adoption_form_questions_form FOREIGN KEY (adoption_form_id) REFERENCES adoption_forms (id) ON DELETE CASCADE
);

-- ROLLBACK
-- DROP TABLE IF EXISTS adoption_form_questions;
-- DROP TABLE IF EXISTS adoption_forms;