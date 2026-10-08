-- V202610040009__seed_adoption_form_questions.sql

-- ============================================================
-- 1. CATEGORÍAS DE PREGUNTAS (QUESTION_CATEGORIES)
-- ============================================================

INSERT INTO question_categories (id, name, description, sort_order) VALUES
(
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd'),
    'Tu hogar',
    'Queremos conocer el lugar donde vivirá el animal.',
    1
),
(
    UUID_TO_BIN('1896cb4b-7c64-4930-9ad8-c9f8ec577bb7'),
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
    UUID_TO_BIN('4b844480-e9dc-4c0b-a086-5d27482f8b0f'),
    'Tipo de vivienda',
    'SELECTION',
    'Home',
    'Seleccioná una opción',
    1,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
),
(
    UUID_TO_BIN('7d9404de-5768-49f7-b99f-8e9663be7d79'),
    '¿Alquilas?',
    'SELECTION',
    'Building',
    'Seleccioná una opción',
    2,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
),
(
    UUID_TO_BIN('e8a243fd-1d00-4f52-af0a-770e8be7d823'),
    '¿Te permiten mascotas los dueños?',
    'SELECTION',
    'Dog',
    'Seleccioná una opción',
    3,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
),
(
    UUID_TO_BIN('7d543533-5c00-4cdd-8210-ddf07e44b2fd'),
    '¿Contás con patio cerrado?',
    'SELECTION',
    'Garden',
    'Seleccioná una opción',
    4,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
),
(
    UUID_TO_BIN('1747a2ec-a953-4fb2-8188-3abdc8a8112d'),
    '¿Vivís con otras personas?',
    'SELECTION',
    'Users',
    'Seleccioná una opción',
    5,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
),
(
    UUID_TO_BIN('dca76420-5d42-43c3-a06b-515dfbe23788'),
    '¿Tenés otras mascotas?',
    'SELECTION',
    'PawPrint',
    'Seleccioná una opción',
    6,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
),
(
    UUID_TO_BIN('57c89cde-c335-438f-b345-28cedcf4256c'),
    'Si tenés otras mascotas, contanos cuáles y sus edades...',
    'TEXT',
    'File',
    'Ej: Tengo un perro de 3 años y un gato de 5 meses...',
    7,
    true,
    UUID_TO_BIN('57f1842d-3737-4477-b13a-882e9d7072dd')
);

-- ---------- Categoría 2: Sobre la adopción ----------

INSERT INTO question_forms (id, title, question_type, icon_name, placeholder, sort_order, is_active, category_id) VALUES
(
    UUID_TO_BIN('0d830fe5-471b-43b0-8d67-f26034a708e8'),
    '¿Tenés experiencia previa con mascotas?',
    'SELECTION',
    'HandHeart',
    'Seleccioná una opción',
    1,
    true,
    UUID_TO_BIN('1896cb4b-7c64-4930-9ad8-c9f8ec577bb7')
),
(
    UUID_TO_BIN('83827721-847f-4d27-b211-de046017f84e'),
    '¿Tenés posibilidad de darle atención veterinaria / vacunas / balanceado?',
    'SELECTION',
    'DollarSign',
    'Seleccioná una opción',
    2,
    true,
    UUID_TO_BIN('1896cb4b-7c64-4930-9ad8-c9f8ec577bb7')
),
(
    UUID_TO_BIN('4ae29811-800d-4172-96f8-463454916baf'),
    '¿Algo más que quieras contarnos?',
    'TEXT',
    'MessageSquare',
    'Cualquier información adicional que creas relevante...',
    3,
    true,
    UUID_TO_BIN('1896cb4b-7c64-4930-9ad8-c9f8ec577bb7')
);


-- ============================================================
-- 3. OPCIONES DE RESPUESTA (QUESTION_FORM_DETAILS)
-- ============================================================

-- Opciones: Tipo de vivienda
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('139fed22-cd8a-4f63-89ce-a30a2785348d'), 'Casa', UUID_TO_BIN('4b844480-e9dc-4c0b-a086-5d27482f8b0f')),
(UUID_TO_BIN('0eaa1893-5626-4907-b7c7-108e9a7bad27'), 'Departamento', UUID_TO_BIN('4b844480-e9dc-4c0b-a086-5d27482f8b0f')),
(UUID_TO_BIN('435dc646-ef6d-4163-a518-13dd8c16a77e'), 'Quinta / Campo', UUID_TO_BIN('4b844480-e9dc-4c0b-a086-5d27482f8b0f'));

-- Opciones: ¿Alquilas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('75f37650-1f01-4d62-8605-a5551f86cd7e'), 'Sí', UUID_TO_BIN('7d9404de-5768-49f7-b99f-8e9663be7d79')),
(UUID_TO_BIN('edafadc7-4fad-45d3-82a3-287d6a02efcb'), 'No', UUID_TO_BIN('7d9404de-5768-49f7-b99f-8e9663be7d79'));

-- Opciones: ¿Te permiten mascotas los dueños?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('508500c9-05a4-4511-8ceb-e84d9585f2cb'), 'Sí', UUID_TO_BIN('e8a243fd-1d00-4f52-af0a-770e8be7d823')),
(UUID_TO_BIN('4f13ed13-b68c-4e7e-a42e-75e9f8f50f9f'), 'No', UUID_TO_BIN('e8a243fd-1d00-4f52-af0a-770e8be7d823')),
(UUID_TO_BIN('29bbd763-ac7a-44e3-9834-a14b48313f7d'), 'No aplica (no alquilo)', UUID_TO_BIN('e8a243fd-1d00-4f52-af0a-770e8be7d823'));

-- Opciones: ¿Contás con patio cerrado?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('01af4ea6-b412-4d52-a66b-89d6b35ca02c'), 'Sí, totalmente cerrado', UUID_TO_BIN('7d543533-5c00-4cdd-8210-ddf07e44b2fd')),
(UUID_TO_BIN('2f9f5dac-5ddc-4f4f-b9e6-ba37062812a9'), 'Patio abierto o con rejas amplias', UUID_TO_BIN('7d543533-5c00-4cdd-8210-ddf07e44b2fd')),
(UUID_TO_BIN('3d714990-17ef-467d-8f7a-47fa10a2db3d'), 'No tengo patio', UUID_TO_BIN('7d543533-5c00-4cdd-8210-ddf07e44b2fd'));

-- Opciones: ¿Vivís con otras personas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('8c471437-b6d0-4754-8296-41de57f1021a'), 'Vivo solo/a', UUID_TO_BIN('1747a2ec-a953-4fb2-8188-3abdc8a8112d')),
(UUID_TO_BIN('9b5fea35-5fd7-4ff8-8028-1320e3830804'), 'Con familia / pareja', UUID_TO_BIN('1747a2ec-a953-4fb2-8188-3abdc8a8112d')),
(UUID_TO_BIN('120937c9-34ef-41a6-9947-c86dd681561b'), 'Con amigos / compañeros', UUID_TO_BIN('1747a2ec-a953-4fb2-8188-3abdc8a8112d'));

-- Opciones: ¿Tenés otras mascotas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('cd488e00-e464-4db1-8881-70a1e0a5c3dd'), 'Sí', UUID_TO_BIN('dca76420-5d42-43c3-a06b-515dfbe23788')),
(UUID_TO_BIN('0b8a3c2d-6062-4255-a153-0406107399b0'), 'No', UUID_TO_BIN('dca76420-5d42-43c3-a06b-515dfbe23788'));

-- Opciones: ¿Tenés experiencia previa con mascotas?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('f2722c64-9a2f-48a6-aafd-832c045ec367'), 'Sí', UUID_TO_BIN('0d830fe5-471b-43b0-8d67-f26034a708e8')),
(UUID_TO_BIN('bac95fec-6f32-4120-9eee-142b22757190'), 'No, sería la primera vez', UUID_TO_BIN('0d830fe5-471b-43b0-8d67-f26034a708e8'));

-- Opciones: ¿Tenés posibilidad de darle atención veterinaria...?
INSERT INTO question_form_details (id, description, question_form_id) VALUES
(UUID_TO_BIN('d06c3a57-a7cc-478b-bafb-17ac1e880075'), 'Sí', UUID_TO_BIN('83827721-847f-4d27-b211-de046017f84e')),
(UUID_TO_BIN('139fed22-cd8a-4f63-89ce-a30a2785348d'), 'No', UUID_TO_BIN('83827721-847f-4d27-b211-de046017f84e'));