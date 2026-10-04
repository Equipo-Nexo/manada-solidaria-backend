-- V202610032330__seed_adoption_form_questions.sql

-- ============================================================
-- 1. CATEGORÍAS DE PREGUNTAS (QUESTION_CATEGORIES)
-- ============================================================

INSERT INTO question_categories (id, name, description, sort_order) VALUES
(
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111'),
    'Tu hogar',
    'Queremos conocer el lugar donde vivirá el animal.',
    1
),
(
    UUID_TO_BIN('22222222-2222-4222-a222-222222222222'),
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
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000001'),
    'Tipo de vivienda',
    'SELECTION',
    'Home',
    'Seleccioná una opción',
    1,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
),
(
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000002'),
    '¿Alquilas?',
    'SELECTION',
    'Building',
    'Seleccioná una opción',
    2,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
),
(
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000003'),
    '¿Te permiten mascotas los dueños?',
    'SELECTION',
    'Key',
    'Seleccioná una opción',
    3,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
),
(
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000004'),
    '¿Contás con patio cerrado?',
    'SELECTION',
    'Fence',
    'Seleccioná una opción',
    4,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
),
(
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000005'),
    '¿Vivís con otras personas?',
    'SELECTION',
    'Users',
    'Seleccioná una opción',
    5,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
),
(
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000006'),
    '¿Tenés otras mascotas?',
    'SELECTION',
    'PawPrint',
    'Seleccioná una opción',
    6,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
),
(
    UUID_TO_BIN('a1000000-0000-4000-a000-000000000007'),
    'Si tenés otras mascotas, contanos cuáles y sus edades...',
    'TEXT',
    'FileText',
    'Ej: Tengo un perro de 3 años y un gato de 5 meses...',
    7,
    true,
    UUID_TO_BIN('11111111-1111-4111-a111-111111111111')
);

-- ---------- Categoría 2: Sobre la adopción ----------

INSERT INTO question_forms (id, title, question_type, icon_name, placeholder, sort_order, is_active, category_id) VALUES
(
    UUID_TO_BIN('a2000000-0000-4000-a000-000000000001'),
    '¿Tenés experiencia previa con mascotas?',
    'SELECTION',
    'HeartHandshake',
    'Seleccioná una opción',
    1,
    true,
    UUID_TO_BIN('22222222-2222-4222-a222-222222222222')
),
(
    UUID_TO_BIN('a2000000-0000-4000-a000-000000000002'),
    '¿Tenés posibilidad de darle atención veterinaria / vacunas / balanceado?',
    'SELECTION',
    'DollarSign',
    'Seleccioná una opción',
    2,
    true,
    UUID_TO_BIN('22222222-2222-4222-a222-222222222222')
),
(
    UUID_TO_BIN('a2000000-0000-4000-a000-000000000003'),
    '¿Algo más que quieras contarnos?',
    'TEXT',
    'MessageSquare',
    'Cualquier información adicional que creas relevante...',
    3,
    true,
    UUID_TO_BIN('22222222-2222-4222-a222-222222222222')
);


-- ============================================================
-- 3. OPCIONES DE RESPUESTA (QUESTION_FORM_DETAILS)
-- ============================================================

-- Opciones: Tipo de vivienda
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000001'), 'Casa', UUID_TO_BIN('a1000000-0000-4000-a000-000000000001')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000002'), 'Departamento', UUID_TO_BIN('a1000000-0000-4000-a000-000000000001')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000003'), 'Quinta / Campo', UUID_TO_BIN('a1000000-0000-4000-a000-000000000001'));

-- Opciones: ¿Alquilas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000004'), 'Sí', UUID_TO_BIN('a1000000-0000-4000-a000-000000000002')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000005'), 'No', UUID_TO_BIN('a1000000-0000-4000-a000-000000000002'));

-- Opciones: ¿Te permiten mascotas los dueños?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000006'), 'Sí', UUID_TO_BIN('a1000000-0000-4000-a000-000000000003')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000007'), 'No', UUID_TO_BIN('a1000000-0000-4000-a000-000000000003')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000008'), 'No aplica (no alquilo)', UUID_TO_BIN('a1000000-0000-4000-a000-000000000003'));

-- Opciones: ¿Contás con patio cerrado?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000009'), 'Sí, totalmente cerrado', UUID_TO_BIN('a1000000-0000-4000-a000-000000000004')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000010'), 'Patio abierto o con rejas amplias', UUID_TO_BIN('a1000000-0000-4000-a000-000000000004')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000011'), 'No tengo patio', UUID_TO_BIN('a1000000-0000-4000-a000-000000000004'));

-- Opciones: ¿Vivís con otras personas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000012'), 'Vivo solo/a', UUID_TO_BIN('a1000000-0000-4000-a000-000000000005')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000013'), 'Con familia / pareja', UUID_TO_BIN('a1000000-0000-4000-a000-000000000005')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000014'), 'Con amigos / compañeros', UUID_TO_BIN('a1000000-0000-4000-a000-000000000005'));

-- Opciones: ¿Tenés otras mascotas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000015'), 'Sí', UUID_TO_BIN('a1000000-0000-4000-a000-000000000006')),
(UUID_TO_BIN('b1000000-0000-4000-a000-000000000016'), 'No', UUID_TO_BIN('a1000000-0000-4000-a000-000000000006'));

-- Opciones: ¿Tenés experiencia previa con mascotas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b2000000-0000-4000-a000-000000000001'), 'Sí', UUID_TO_BIN('a2000000-0000-4000-a000-000000000001')),
(UUID_TO_BIN('b2000000-0000-4000-a000-000000000002'), 'No, sería la primera vez', UUID_TO_BIN('a2000000-0000-4000-a000-000000000001'));

-- Opciones: ¿Tenés posibilidad de darle atención veterinaria...?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('b2000000-0000-4000-a000-000000000003'), 'Sí', UUID_TO_BIN('a2000000-0000-4000-a000-000000000002')),
(UUID_TO_BIN('b2000000-0000-4000-a000-000000000004'), 'No', UUID_TO_BIN('a2000000-0000-4000-a000-000000000002'));


-- ============================================================
-- ROLLBACK SCRIPT
-- ============================================================
--DELETE FROM question_form_details WHERE question_form_id IN (
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000001'),
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000002'),
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000003'),
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000004'),
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000005'),
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000006'),
--    UUID_TO_BIN('a1000000-0000-4000-a000-000000000007'),
--    UUID_TO_BIN('a2000000-0000-4000-a000-000000000001'),
--    UUID_TO_BIN('a2000000-0000-4000-a000-000000000002'),
--    UUID_TO_BIN('a2000000-0000-4000-a000-000000000003')
--);

--DELETE FROM question_forms WHERE category_id IN (
--    UUID_TO_BIN('11111111-1111-4111-a111-111111111111'),
--    UUID_TO_BIN('22222222-2222-4222-a222-222222222222')
--);

--DELETE FROM question_categories WHERE id IN (
--    UUID_TO_BIN('11111111-1111-4111-a111-111111111111'),
--    UUID_TO_BIN('22222222-2222-4222-a222-222222222222')
--);