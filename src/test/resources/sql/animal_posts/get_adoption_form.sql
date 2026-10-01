-- ==========================
-- LOCATION
-- ==========================

INSERT INTO location (id, name, address, number, latitude, longitude)
VALUES
(
    '12121212-1212-1212-1212-121212121212',
    'Refugio Norte',
    'Calle Falsa',
    123,
    -34.5,
    -58.5
);


-- ==========================
-- ANIMAL
-- ==========================

INSERT INTO animal (id, color, age, gender, size, type)
VALUES
(
    '23232323-2323-2323-2323-232323232323',
    'blanco',
    'PUPPY',
    'FEMALE',
    'SMALL',
    'CAT'
);


-- ==========================
-- PROFILES + USERS
-- ==========================

-- Usuario externo (Owner de la publicación)
INSERT INTO profile (id, email, lastname, name, area_code, phone_number, profile_imageurl, user_notificationurl, roles)
VALUES
(
    '44444444-4444-4444-4444-444444444444',
    'owner@email.com',
    'Perez',
    'Juan',
    '1122',
    '334455',
    'cf-owner',
    NULL,
    ARRAY['COMMUNITY']
);

INSERT INTO users (id, username, password, profile_id)
VALUES
(
    '33333333-3333-3333-3333-333333333333',
    'owner-user',
    'password',
    '44444444-4444-4444-4444-444444444444'
);

-- Usuario solicitante (Applicant)
INSERT INTO profile (id, email, lastname, name, area_code, phone_number, profile_imageurl, user_notificationurl, roles)
VALUES
(
    'b1111111-1111-1111-1111-111111111111',
    'applicant@email.com',
    'Gomez',
    'Maria',
    '353',
    '123456',
    'cf-applicant',
    NULL,
    ARRAY['COMMUNITY']
);

INSERT INTO users (id, username, password, profile_id)
VALUES
(
    'a1111111-1111-1111-1111-111111111111',
    'applicant-user',
    'password',
    'b1111111-1111-1111-1111-111111111111'
);


-- =================================================
-- ADOPTION POST (JOINED TABLE STRATEGY)
-- =================================================

INSERT INTO animal_post (id, name, description, image_url, area_code, phone_number, animal_id, location_id, owner_id, created_at)
VALUES
(
    '99999999-9999-9999-9999-999999999999',
    'Busco hogar para gata',
    'Rescatada de la calle',
    'cf-image-456',
    '1122',
    '334455',
    '23232323-2323-2323-2323-232323232323',
    '12121212-1212-1212-1212-121212121212',
    '33333333-3333-3333-3333-333333333333',
    CURRENT_TIMESTAMP
);

INSERT INTO adoption_post (id)
VALUES
(
    '99999999-9999-9999-9999-999999999999'
);

INSERT INTO adoption_post_status_history (id, adoption_post_id, status, created_at, finished_at)
VALUES
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '99999999-9999-9999-9999-999999999999',
    'SEARCHING_ADOPT_AND_TRANSIT',
    CURRENT_TIMESTAMP,
    NULL
);


-- =================================================
-- QUESTION CATEGORIES & QUESTIONS
-- =================================================

INSERT INTO question_categories (id, name, description, sort_order)
VALUES
(
    'e1111111-1111-1111-1111-111111111111',
    'Vivienda',
    'Condiciones del hogar del postulante',
    1
);

INSERT INTO question_forms (id, title, question_type, icon_name, placeholder, sort_order, is_active, category_id)
VALUES
(
    'f1111111-1111-1111-1111-111111111111',
    '¿Alquilás? ¿Te permiten mascotas?',
    'TEXT',
    'home-icon',
    'Ingresá detalles de tu vivienda',
    1,
    true,
    'e1111111-1111-1111-1111-111111111111'
),
(
    'f2222222-2222-2222-2222-222222222222',
    '¿Contás con patio cerrado?',
    'TEXT',
    'fence-icon',
    'Describí tu patio o balcón',
    2,
    true,
    'e1111111-1111-1111-1111-111111111111'
);


-- =================================================
-- ADOPTION FORM + ANSWERS (DETAILS)
-- =================================================

INSERT INTO adoption_forms (id, area_code, phone_number, is_read, created_at, applicant_id, adoption_post_id)
VALUES
(
    'c1111111-1111-1111-1111-111111111111',
    '353',
    '4123456',
    false,
    CURRENT_TIMESTAMP,
    'a1111111-1111-1111-1111-111111111111',
    '99999999-9999-9999-9999-999999999999'
);

INSERT INTO adoption_form_details (id, answer, adoption_form_id, question_form_id)
VALUES
(
    'd1111111-1111-1111-1111-111111111111',
    'Alquilo y sí me permiten.',
    'c1111111-1111-1111-1111-111111111111',
    'f1111111-1111-1111-1111-111111111111'
),
(
    'd2222222-2222-2222-2222-222222222222',
    'Sí, totalmente cerrado.',
    'c1111111-1111-1111-1111-111111111111',
    'f2222222-2222-2222-2222-222222222222'
);