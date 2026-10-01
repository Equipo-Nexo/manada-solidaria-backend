-- 1. Eliminar tabla obsoleta de preguntas embebidas
DROP TABLE IF EXISTS adoption_form_questions;

-- 2. Crear tabla de categorías de preguntas
CREATE TABLE question_categories (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    sort_order INT,
    PRIMARY KEY (id)
);

-- 3. Crear tabla de preguntas (QuestionForm)
CREATE TABLE question_forms (
    id BINARY(16) NOT NULL,
    title VARCHAR(255) NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    icon_name VARCHAR(255),
    placeholder VARCHAR(255),
    sort_order INT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BINARY(16),
    PRIMARY KEY (id),
    CONSTRAINT fk_question_forms_category FOREIGN KEY (category_id) REFERENCES question_categories (id) ON DELETE SET NULL
);

-- 4. Crear tabla de opciones/detalles de la pregunta (QuestionFormDetail)
CREATE TABLE question_form_details (
    id BINARY(16) NOT NULL,
    description VARCHAR(500) NOT NULL,
    question_form_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_question_form_details_question FOREIGN KEY (question_form_id) REFERENCES question_forms (id) ON DELETE CASCADE
);

-- 5. Crear tabla de respuestas por formulario (AdoptionFormDetail)
CREATE TABLE adoption_form_details (
    id BINARY(16) NOT NULL,
    answer VARCHAR(1000),
    adoption_form_id BINARY(16) NOT NULL,
    question_form_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_adoption_form_details_form FOREIGN KEY (adoption_form_id) REFERENCES adoption_forms (id) ON DELETE CASCADE,
    CONSTRAINT fk_adoption_form_details_question FOREIGN KEY (question_form_id) REFERENCES question_forms (id)
);

-- ROLLBACK
-- DROP TABLE IF EXISTS adoption_form_details;
-- DROP TABLE IF EXISTS question_form_details;
-- DROP TABLE IF EXISTS question_forms;
-- DROP TABLE IF EXISTS question_categories;
-- CREATE TABLE adoption_form_questions (
--     adoption_form_id BINARY(16) NOT NULL,
--     question VARCHAR(500) NOT NULL,
--     answer VARCHAR(1000) NOT NULL,
--     CONSTRAINT fk_adoption_form_questions_form FOREIGN KEY (adoption_form_id) REFERENCES adoption_forms (id) ON DELETE CASCADE
-- );