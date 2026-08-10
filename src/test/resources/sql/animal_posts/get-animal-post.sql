-- ==========================
-- LOCATION
-- ==========================

INSERT INTO location (id, name, address, number, latitude, longitude)
VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'Parque Centenario',
    'Av. Patricias',
    100,
    -34.6,
    -58.4
),
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
    '22222222-2222-2222-2222-222222222222',
    'marron',
    'ADULT',
    'MALE',
    'MEDIUM',
    'DOG'
),
(
    '23232323-2323-2323-2323-232323232323',
    'blanco',
    'PUPPY',
    'FEMALE',
    'SMALL',
    'CAT'
),
(
    '24242424-2424-2424-2424-242424242424',
    'negro',
    'ADULT',
    'MALE',
    'MEDIUM',
    'DOG'
);


-- ==========================
-- PROFILE + USER
-- ==========================

INSERT INTO profile (
    id,
    email,
    lastname,
    name,
    phone_number,
    profile_imageurl,
    user_notificationurl,
    roles
)
VALUES
(
    '44444444-4444-4444-4444-444444444444',
    'test-user@email.com',
    'Perez',
    'Juan',
    '1122334455',
    NULL,
    NULL,
    ARRAY['COMMUNITY']
);


INSERT INTO users (
    id,
    username,
    password,
    profile_id
)
VALUES
(
    '33333333-3333-3333-3333-333333333333',
    'test-user',
    'password',
    '44444444-4444-4444-4444-444444444444'
);


-- =================================================
-- LOST POST (HAS OWNER = TRUE) -> TYPE LOST
-- ID: 555...
-- =================================================

INSERT INTO animal_post (
    id,
    name,
    description,
    image_url,
    phone_number,
    animal_id,
    location_id,
    owner_id,
    created_at
)
VALUES
(
    '55555555-5555-5555-5555-555555555555',
    'Perdí mi perro',
    'Se perdió en el parque',
    'cf-image-123',
    '1122334455',
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    '33333333-3333-3333-3333-333333333333',
    CURRENT_TIMESTAMP
);


INSERT INTO lost_post (
    id,
    has_owner,
    reward
)
VALUES
(
    '55555555-5555-5555-5555-555555555555',
    true,
    5000
);


INSERT INTO lost_post_status_history (
    id,
    lost_post_id,
    status,
    created_at,
    finished_at
)
VALUES
(
    '66666666-6666-6666-6666-666666666666',
    '55555555-5555-5555-5555-555555555555',
    'CREATED',
    CURRENT_TIMESTAMP,
    NULL
);



-- =================================================
-- LOST POST (HAS OWNER = FALSE) -> TYPE IN_STREET
-- ID: 777...
-- =================================================

INSERT INTO animal_post (
    id,
    name,
    description,
    image_url,
    phone_number,
    animal_id,
    location_id,
    owner_id,
    created_at
)
VALUES
(
    '77777777-7777-7777-7777-777777777777',
    'Perro encontrado',
    'Está en la calle',
    'cf-image-street',
    NULL,
    '24242424-2424-2424-2424-242424242424',
    '11111111-1111-1111-1111-111111111111',
    NULL,
    CURRENT_TIMESTAMP
);


INSERT INTO lost_post (
    id,
    has_owner,
    reward
)
VALUES
(
    '77777777-7777-7777-7777-777777777777',
    false,
    NULL
);


INSERT INTO lost_post_status_history (
    id,
    lost_post_id,
    status,
    created_at,
    finished_at
)
VALUES
(
    '88888888-8888-8888-8888-888888888888',
    '77777777-7777-7777-7777-777777777777',
    'SEARCHING',
    CURRENT_TIMESTAMP,
    NULL
);



-- =================================================
-- ADOPTION POST -> TYPE ADOPTION
-- ID: 999...
-- =================================================

INSERT INTO animal_post (
    id,
    name,
    description,
    image_url,
    phone_number,
    animal_id,
    location_id,
    owner_id,
    created_at
)
VALUES
(
    '99999999-9999-9999-9999-999999999999',
    'Busco hogar para gata',
    'Rescatada de la calle',
    'cf-image-456',
    '1122334455',
    '23232323-2323-2323-2323-232323232323',
    '12121212-1212-1212-1212-121212121212',
    '33333333-3333-3333-3333-333333333333',
    CURRENT_TIMESTAMP
);


INSERT INTO adoption_post (
    id
)
VALUES
(
    '99999999-9999-9999-9999-999999999999'
);


INSERT INTO adoption_post_status_history (
    id,
    adoption_post_id,
    status,
    created_at,
    finished_at
)
VALUES
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '99999999-9999-9999-9999-999999999999',
    'SEARCHING_ADOPT_AND_TRANSIT',
    CURRENT_TIMESTAMP,
    NULL
);