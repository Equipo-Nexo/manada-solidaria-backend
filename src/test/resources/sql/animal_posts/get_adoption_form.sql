-- Profile para el solicitante
INSERT INTO profile (id, email, lastname, name, area_code, phone_number, profile_imageurl, user_notificationurl, roles)
VALUES ('b1111111-1111-1111-1111-111111111111', 'applicant@email.com', 'Gomez', 'Maria', '353', '123456', NULL, NULL, ARRAY['COMMUNITY']);

-- Usuario solicitante
INSERT INTO users (id, username, password, profile_id)
VALUES ('a1111111-1111-1111-1111-111111111111', 'applicant-user', 'password', 'b1111111-1111-1111-1111-111111111111');

-- Formulario asociado a la publicación 99999999-9999-9999-9999-999999999999
INSERT INTO adoption_forms (id, area_code, phone_number, is_read, created_at, applicant_id, adoption_post_id)
VALUES ('c1111111-1111-1111-1111-111111111111', '353', '4123456', false, CURRENT_TIMESTAMP, 'a1111111-1111-1111-1111-111111111111', '99999999-9999-9999-9999-999999999999');

-- Preguntas respondidas en el formulario
INSERT INTO adoption_form_questions (adoption_form_id, question, answer)
VALUES
('c1111111-1111-1111-1111-111111111111', '¿Alquilás? ¿Te permiten mascotas?', 'Alquilo y sí me permiten.'),
('c1111111-1111-1111-1111-111111111111', '¿Contás con patio cerrado?', 'Sí, totalmente cerrado.');