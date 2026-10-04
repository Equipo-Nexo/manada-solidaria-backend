INSERT INTO question_categories (id, name, description, sort_order)
VALUES
('11111111-1111-1111-1111-111111111111', 'Tu hogar', 'Queremos conocer el lugar donde vivirá el animal.', 1),
('22222222-2222-2222-2222-222222222222', 'Sobre la adopción', 'Estas preguntas ayudan a encontrar la mejor familia para cada animal.', 2);

INSERT INTO question_forms (id, title, question_type, icon_name, placeholder, sort_order, is_active, category_id)
VALUES
('a1111111-1111-1111-1111-111111111111', 'Tipo de vivienda', 'SELECTION', 'Home', 'Seleccioná una opción', 1, true, '11111111-1111-1111-1111-111111111111'),
('a2222222-2222-2222-2222-222222222222', '¿Tenés experiencia previa con mascotas?', 'SELECTION', 'HeartHandshake', 'Seleccioná una opción', 1, true, '22222222-2222-2222-2222-222222222222');

INSERT INTO question_form_details (id, description, question_form_id)
VALUES
('b1111111-1111-1111-1111-111111111111', 'Casa', 'a1111111-1111-1111-1111-111111111111'),
('b2222222-2222-2222-2222-222222222222', 'Departamento', 'a1111111-1111-1111-1111-111111111111');