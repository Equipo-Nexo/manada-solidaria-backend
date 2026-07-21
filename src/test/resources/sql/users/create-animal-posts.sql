-- =========================================================
-- 1. UBICACIÓN
-- =========================================================

MERGE INTO LOCATION (
    ID,
    NAME,
    ADDRESS,
    NUMBER,
    LATITUDE,
    LONGITUDE
)
KEY (ID)
VALUES (
    '33333333-3333-3333-3333-333333333335',
    'Villa María',
    'Av. Libertador San Martín',
    500,
    -32.4108,
    -63.2436
);


-- =========================================================
-- 2. ANIMAL
-- =========================================================

MERGE INTO ANIMAL (
    ID,
    TYPE,
    SIZE,
    GENDER,
    AGE,
    COLOR
)
KEY (ID)
VALUES (
    '66666666-6666-6666-6666-666666666666',
    'DOG',
    'MEDIUM',
    'MALE',
    'ADULT',
    'BROWN'
);


-- =========================================================
-- 3. DATOS COMUNES DEL ANIMAL POST
-- =========================================================

MERGE INTO ANIMAL_POST (
    ID,
    NAME,
    DESCRIPTION,
    IMAGE_URL,
    SHARE_POST_URL,
    PHONE_NUMBER,
    UPDATED_AT,
    CREATED_AT,
    LOCATION_ID,
    OWNER_ID,
    ANIMAL_ID
)
KEY (ID)
VALUES (
    '77777777-7777-7777-7777-777777777777',
    'Perro perdido en Villa María',
    'Se perdió cerca de Av. Libertador San Martín. Tiene un collar rojo.',
    NULL,
    NULL,
    '+5493534000000',
    NULL,
    CURRENT_TIMESTAMP,
    '33333333-3333-3333-3333-333333333335',
    '22222222-2222-2222-2222-222222222222',
    '66666666-6666-6666-6666-666666666666'
);


-- =========================================================
-- 4. DATOS ESPECÍFICOS DEL LOST POST
-- =========================================================

MERGE INTO LOST_POST (
    ID,
    HAS_OWNER
)
KEY (ID)
VALUES (
    '77777777-7777-7777-7777-777777777777',
    FALSE
);


-- =========================================================
-- 5. HISTORIAL DE ESTADOS
-- =========================================================

MERGE INTO LOST_POST_STATUS_HISTORY (
    ID,
    CREATED_AT,
    FINISHED_AT,
    STATUS,
    LOST_POST_ID
)
KEY (ID)
VALUES (
    '88888888-8888-8888-8888-888888888888',
    CURRENT_TIMESTAMP,
    NULL,
    'CREATED',
    '77777777-7777-7777-7777-777777777777'
);