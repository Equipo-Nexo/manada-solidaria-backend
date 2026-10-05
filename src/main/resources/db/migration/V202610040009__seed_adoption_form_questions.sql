-- V202610040009__seed_adoption_form_questions.sql

-- ============================================================
-- 1. CATEGORÍAS DE PREGUNTAS (QUESTION_CATEGORIES)
-- ============================================================

INSERT INTO question_categories (id, name, description, sort_order) VALUES
(
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1'),
    'Tu hogar',
    'Queremos conocer el lugar donde vivirá el animal.',
    1
),
(
    UUID_TO_BIN('f9d4c52e-0f23-4a43-94fa-223456789ab2'),
    'Sobre la adopción',
    'Estas preguntas ayudan a encontrar la mejor familia para cada animal.',
    2
);


-- ============================================================
-- 2. PREGUNTAS (QUESTION_FORMS)
-- ============================================================

-- ---------- Categoría 1: Tu hogar ----------

INSERT INTO question_forms (id, title, question_type, icon_name, placeholder, sort_order, is_active, category_id) VALUES
(
    UUID_TO_BIN('1a8b3c4d-5e6f-47a8-9b0c-111111111111'),
    'Tipo de vivienda',
    'SELECTION',
    'Home',
    'Seleccioná una opción',
    1,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
),
(
    UUID_TO_BIN('2b9c4d5e-6f7a-48b9-0c1d-222222222222'),
    '¿Alquilas?',
    'SELECTION',
    'Building',
    'Seleccioná una opción',
    2,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
),
(
    UUID_TO_BIN('3c0d5e6f-7a8b-49c0-1d2e-333333333333'),
    '¿Te permiten mascotas los dueños?',
    'SELECTION',
    'Dog',
    'Seleccioná una opción',
    3,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
),
(
    UUID_TO_BIN('4d1e6f7a-8b9c-40d1-2e3f-444444444444'),
    '¿Contás con patio cerrado?',
    'SELECTION',
    'Garden',
    'Seleccioná una opción',
    4,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
),
(
    UUID_TO_BIN('5e2f7a8b-9c0d-41e2-3f4a-555555555555'),
    '¿Vivís con otras personas?',
    'SELECTION',
    'Users',
    'Seleccioná una opción',
    5,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
),
(
    UUID_TO_BIN('6f3a8b9c-0d1e-42f3-4a5b-666666666666'),
    '¿Tenés otras mascotas?',
    'SELECTION',
    'PawPrint',
    'Seleccioná una opción',
    6,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
),
(
    UUID_TO_BIN('7a4b9c0d-1e2f-43a4-5b6c-777777777777'),
    'Si tenés otras mascotas, contanos cuáles y sus edades...',
    'TEXT',
    'File',
    'Ej: Tengo un perro de 3 años y un gato de 5 meses...',
    7,
    true,
    UUID_TO_BIN('e8c3b41d-9e12-4f32-83ef-1123456789a1')
);

-- ---------- Categoría 2: Sobre la adopción ----------

INSERT INTO question_forms (id, title, question_type, icon_name, placeholder, sort_order, is_active, category_id) VALUES
(
    UUID_TO_BIN('8b5c0d1e-2f3a-44b5-6c7d-888888888888'),
    '¿Tenés experiencia previa con mascotas?',
    'SELECTION',
    'HandHeart',
    'Seleccioná una opción',
    1,
    true,
    UUID_TO_BIN('f9d4c52e-0f23-4a43-94fa-223456789ab2')
),
(
    UUID_TO_BIN('9c6d1e2f-3a4b-45c6-7d8e-999999999999'),
    '¿Tenés posibilidad de darle atención veterinaria / vacunas / balanceado?',
    'SELECTION',
    'DollarSign',
    'Seleccioná una opción',
    2,
    true,
    UUID_TO_BIN('f9d4c52e-0f23-4a43-94fa-223456789ab2')
),
(
    UUID_TO_BIN('0d7e2f3a-4b5c-46d7-8e9f-000000000000'),
    '¿Algo más que quieras contarnos?',
    'TEXT',
    'MessageSquare',
    'Cualquier información adicional que creas relevante...',
    3,
    true,
    UUID_TO_BIN('f9d4c52e-0f23-4a43-94fa-223456789ab2')
);


-- ============================================================
-- 3. OPCIONES DE RESPUESTA (QUESTION_FORM_DETAILS)
-- ============================================================

-- Opciones: Tipo de vivienda
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('a1111111-2222-3333-4444-555555555555'), 'Casa', UUID_TO_BIN('1a8b3c4d-5e6f-47a8-9b0c-111111111111')),
(UUID_TO_BIN('a1111111-2222-3333-4444-666666666666'), 'Departamento', UUID_TO_BIN('1a8b3c4d-5e6f-47a8-9b0c-111111111111')),
(UUID_TO_BIN('a1111111-2222-3333-4444-777777777777'), 'Quinta / Campo', UUID_TO_BIN('1a8b3c4d-5e6f-47a8-9b0c-111111111111'));

-- Opciones: ¿Alquilas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b2222222-3333-4444-5555-666666666666'), 'Sí', UUID_TO_BIN('2b9c4d5e-6f7a-48b9-0c1d-222222222222')),
(UUID_TO_BIN('b2222222-3333-4444-5555-777777777777'), 'No', UUID_TO_BIN('2b9c4d5e-6f7a-48b9-0c1d-222222222222'));

-- Opciones: ¿Te permiten mascotas los dueños?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('c3333333-4444-5555-6666-777777777777'), 'Sí', UUID_TO_BIN('3c0d5e6f-7a8b-49c0-1d2e-333333333333')),
(UUID_TO_BIN('c3333333-4444-5555-6666-888888888888'), 'No', UUID_TO_BIN('3c0d5e6f-7a8b-49c0-1d2e-333333333333')),
(UUID_TO_BIN('c3333333-4444-5555-6666-999999999999'), 'No aplica (no alquilo)', UUID_TO_BIN('3c0d5e6f-7a8b-49c0-1d2e-333333333333'));

-- Opciones: ¿Contás con patio cerrado?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('d4444444-5555-6666-7777-888888888888'), 'Sí, totalmente cerrado', UUID_TO_BIN('4d1e6f7a-8b9c-40d1-2e3f-444444444444')),
(UUID_TO_BIN('d4444444-5555-6666-7777-999999999999'), 'Patio abierto o con rejas amplias', UUID_TO_BIN('4d1e6f7a-8b9c-40d1-2e3f-444444444444')),
(UUID_TO_BIN('d4444444-5555-6666-7777-000000000000'), 'No tengo patio', UUID_TO_BIN('4d1e6f7a-8b9c-40d1-2e3f-444444444444'));

-- Opciones: ¿Vivís con otras personas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('e5555555-6666-7777-8888-999999999999'), 'Vivo solo/a', UUID_TO_BIN('5e2f7a8b-9c0d-41e2-3f4a-555555555555')),
(UUID_TO_BIN('e5555555-6666-7777-8888-000000000000'), 'Con familia / pareja', UUID_TO_BIN('5e2f7a8b-9c0d-41e2-3f4a-555555555555')),
(UUID_TO_BIN('e5555555-6666-7777-8888-111111111111'), 'Con amigos / compañeros', UUID_TO_BIN('5e2f7a8b-9c0d-41e2-3f4a-555555555555'));

-- Opciones: ¿Tenés otras mascotas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('f6666666-7777-8888-9999-000000000000'), 'Sí', UUID_TO_BIN('6f3a8b9c-0d1e-42f3-4a5b-666666666666')),
(UUID_TO_BIN('f6666666-7777-8888-9999-111111111111'), 'No', UUID_TO_BIN('6f3a8b9c-0d1e-42f3-4a5b-666666666666'));

-- Opciones: ¿Tenés experiencia previa con mascotas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('07777777-8888-9999-0000-111111111111'), 'Sí', UUID_TO_BIN('8b5c0d1e-2f3a-44b5-6c7d-888888888888')),
(UUID_TO_BIN('07777777-8888-9999-0000-222222222222'), 'No, sería la primera vez', UUID_TO_BIN('8b5c0d1e-2f3a-44b5-6c7d-888888888888'));

-- Opciones: ¿Tenés posibilidad de darle atención veterinaria...?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('18888888-9999-0000-1111-222222222222'), 'Sí', UUID_TO_BIN('9c6d1e2f-3a4b-45c6-7d8e-999999999999')),
(UUID_TO_BIN('18888888-9999-0000-1111-333333333333'), 'No', UUID_TO_BIN('9c6d1e2f-3a4b-45c6-7d8e-999999999999'));