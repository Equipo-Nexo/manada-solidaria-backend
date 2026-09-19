-- Flyway migration
-- V202609082243__seed_veterinarias_villa_maria.sql
-- Seed de veterinarias de Villa María, Córdoba.
-- IDs de location y vet_information generados como UUID v4.
-- Requiere MySQL y tablas location, vet_information y schedule creadas previamente.

-- ============================================================
-- Seed de veterinarias - Villa María, Córdoba
-- Compatible con:
-- location.id       BINARY(16)
-- vet_information.id BINARY(16)
-- schedule.id       BINARY(16)
-- ============================================================


-- ============================================================
-- LOCATIONS
-- ============================================================

INSERT INTO location (id, name, address, number, latitude, longitude) VALUES
(
    UUID_TO_BIN('3840abfb-05ff-421e-88b3-895d88cfe3f9'),
    'Villa María',
    NULL,
    NULL,
    -32.410625,
    -63.248316
),
(
    UUID_TO_BIN('aecaacf8-059d-472b-a625-ca2d835d1266'),
    'Villa María',
    NULL,
    NULL,
    -32.41217755,
    -63.233459
),
(
    UUID_TO_BIN('eb0d678a-fead-477b-8172-c94126292743'),
    'Villa María',
    NULL,
    NULL,
    -32.417323,
    -63.236983
),
(
    UUID_TO_BIN('76c08e44-e776-459d-8dce-efae6998ceb9'),
    'Villa María',
    NULL,
    NULL,
    -32.4065,
    -63.255474
),
(
    UUID_TO_BIN('ceda407b-867b-479d-9e25-c0487d1b437e'),
    'Villa María',
    NULL,
    NULL,
    -32.408038,
    -63.231308
),
(
    UUID_TO_BIN('c6d78427-7718-43ac-a93f-66686dc36b8b'),
    'Villa María',
    NULL,
    NULL,
    -32.401262,
    -63.23628
),
(
    UUID_TO_BIN('408042a6-30f4-42c5-a010-4f0e53607d1e'),
    'Villa María',
    NULL,
    NULL,
    -32.415516,
    -63.247955
),
(
    UUID_TO_BIN('6f1eafcd-94e6-4c6e-9d41-71f8cbb946b0'),
    'Villa María',
    'Carlos Pellegrini',
    449,
    -32.413309,
    -63.236346
),
(
    UUID_TO_BIN('d9269cbd-73fe-46c0-ad9b-9cae86d74351'),
    'Villa María',
    NULL,
    NULL,
    -32.415850882978724,
    -63.26486968510638
),
(
    UUID_TO_BIN('5eb26647-a2fc-4c2c-bf0b-1556ddbfcfc1'),
    'Villa María',
    NULL,
    NULL,
    -32.41883041276596,
    -63.24392875106383
),
(
    UUID_TO_BIN('4b3ccfa6-2164-4cd7-836e-2d3f5689b1b4'),
    'Villa María',
    NULL,
    NULL,
    -32.41586576595745,
    -63.2485117212766
),
(
    UUID_TO_BIN('62c3c609-de6b-473e-aa15-b2eab8699682'),
    'Villa María',
    NULL,
    NULL,
    -32.4188305,
    -63.2483232
),
(
    UUID_TO_BIN('dc478e5a-0858-462b-9fb8-c953cf2350ad'),
    'Villa María',
    NULL,
    NULL,
    -32.40490795319149,
    -63.24321488510638
),
(
    UUID_TO_BIN('ba8e65db-ff81-4d7e-927c-cefbfdefb111'),
    'Villa María',
    NULL,
    NULL,
    -32.4181747,
    -63.2268219
),
(
    UUID_TO_BIN('ac37b4fb-11ba-4d09-86fe-e3792905ef1e'),
    'Villa María',
    NULL,
    NULL,
    -32.41821064255319,
    -63.22563960212766
),
(
    UUID_TO_BIN('b02d8c46-b2c2-4a88-a0f0-0e618113b5b6'),
    'Villa María',
    NULL,
    NULL,
    -32.41435302978723,
    -63.25312118510638
),
(
    UUID_TO_BIN('16d11dfb-b1c6-488e-861b-c90abd02f492'),
    'Villa María',
    NULL,
    NULL,
    -32.4190712,
    -63.239193
);


-- ============================================================
-- VET INFORMATION
-- ============================================================

INSERT INTO vet_information (
    id,
    name,
    area_code,
    phone_number,
    email,
    profile_picture_url,
    vet_page_url,
    description,
    location_id
)
VALUES

(
    UUID_TO_BIN('4bf957fa-69cb-4666-9afb-11c46378262b'),
    'Veterinaria Darwin',
    '353',
    '4532223',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('3840abfb-05ff-421e-88b3-895d88cfe3f9')
),

(
    UUID_TO_BIN('069a1227-4779-4eda-a080-9cdf7c0e9483'),
    'Canne''s Veterinaria',
    '353',
    '4245056',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('aecaacf8-059d-472b-a625-ca2d835d1266')
),

(
    UUID_TO_BIN('3cc2733f-4569-4686-bc79-6fe13ceba59c'),
    'El Fortín Veterinaria',
    '353',
    '4065029',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('eb0d678a-fead-477b-8172-c94126292743')
),

(
    UUID_TO_BIN('6a27cff6-898c-4485-b350-b421d3ee4813'),
    'Veterinaria Nueva Salud',
    '353',
    '4013241',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('76c08e44-e776-459d-8dce-efae6998ceb9')
),

(
    UUID_TO_BIN('e76dccb8-4711-48e3-aaa9-047216eda0bf'),
    'Veterinaria Guau',
    '353',
    '4537678',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('ceda407b-867b-479d-9e25-c0487d1b437e')
),

(
    UUID_TO_BIN('875b3651-18e9-4abd-a750-605764d0f019'),
    'Veterinaria Como Reyes',
    '353',
    '4219764',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('c6d78427-7718-43ac-a93f-66686dc36b8b')
),

(
    UUID_TO_BIN('bf1a3e86-3047-46d5-ae86-086d04c7e153'),
    'Bienestar Animal Centro Médico Veterinario',
    '353',
    '4277297',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('408042a6-30f4-42c5-a010-4f0e53607d1e')
),

(
    UUID_TO_BIN('f813624f-cff7-4024-afee-e2bde38884a7'),
    'Dr. Seba Veterinaria',
    '353',
    '4015496',
    'drsebaveterinaria@outlook.com',
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('6f1eafcd-94e6-4c6e-9d41-71f8cbb946b0')
),

(
    UUID_TO_BIN('b7c3b26c-dc4b-4364-9135-8b3da7a7c5a0'),
    'Veterinaria Palermo',
    '353',
    '5101886',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('d9269cbd-73fe-46c0-ad9b-9cae86d74351')
),

(
    UUID_TO_BIN('92179536-fb45-46f7-b351-feb91e81d049'),
    'Sol Veterinaria',
    '353',
    '5646400',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('5eb26647-a2fc-4c2c-bf0b-1556ddbfcfc1')
),

(
    UUID_TO_BIN('daf12986-a574-421e-804a-009eac92fc72'),
    'Veterinaria San Francisco',
    '353',
    '4187702',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('4b3ccfa6-2164-4cd7-836e-2d3f5689b1b4')
),

(
    UUID_TO_BIN('a6250233-d88a-4550-b9f0-2e5fd0858b9b'),
    'Centro Integral Médico Veterinario Mi Ba-Bau',
    '353',
    '4526877',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('62c3c609-de6b-473e-aa15-b2eab8699682')
),

(
    UUID_TO_BIN('13c55783-a501-4436-9b32-6e7eb9170300'),
    'Arca de Noe Veterinaria',
    '353',
    '5693990',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('dc478e5a-0858-462b-9fb8-c953cf2350ad')
),

(
    UUID_TO_BIN('160112d3-b018-474a-9c2b-dcc563becdc4'),
    'Veterinaria Madala',
    '353',
    '4064358',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('ba8e65db-ff81-4d7e-927c-cefbfdefb111')
),

(
    UUID_TO_BIN('aa04e40c-0d39-414c-b1f5-2efce58b7e62'),
    'Veterinaria Belfo''s',
    '353',
    '4194392',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('ac37b4fb-11ba-4d09-86fe-e3792905ef1e')
),

(
    UUID_TO_BIN('8b8c9bf1-a312-4504-900f-6a5e11e8221f'),
    'Veterinaria Esperanza',
    '353',
    '4522279',
    'vetesperanza@gmail.com',
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('b02d8c46-b2c2-4a88-a0f0-0e618113b5b6')
),

(
    UUID_TO_BIN('4b65cea8-5e94-41fc-9435-32ef8986fb79'),
    'Amigo',
    '353',
    '6572535',
    NULL,
    NULL,
    NULL,
    NULL,
    UUID_TO_BIN('16d11dfb-b1c6-488e-861b-c90abd02f492')
);


-- ============================================================
-- SCHEDULES
-- ============================================================


-- ============================================================
-- Veterinaria Darwin
-- Lun-Vie 08:30-12:30 / 17:00-20:00
-- Sáb     09:00-12:30 / 17:00-20:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('4bf957fa-69cb-4666-9afb-11c46378262b'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('08:30:00' AS TIME) AS opening_time,
        CAST('12:30:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('17:00:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES
(
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('4bf957fa-69cb-4666-9afb-11c46378262b'),
    'SATURDAY',
    '09:00:00',
    '12:30:00'
),
(
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('4bf957fa-69cb-4666-9afb-11c46378262b'),
    'SATURDAY',
    '17:00:00',
    '20:00:00'
);


-- ============================================================
-- Canne's Veterinaria
-- Lun-Vie 09:00-12:30 / 17:00-19:30
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('069a1227-4779-4eda-a080-9cdf7c0e9483'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('12:30:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('17:00:00' AS TIME),
        CAST('19:30:00' AS TIME)
) h;


-- ============================================================
-- El Fortín
-- Lun-Vie 09:00-13:00 / 16:30-21:00
-- Sáb     09:00-13:00 / 17:00-21:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('3cc2733f-4569-4686-bc79-6fe13ceba59c'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('13:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:30:00' AS TIME),
        CAST('21:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES
(
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('3cc2733f-4569-4686-bc79-6fe13ceba59c'),
    'SATURDAY',
    '09:00:00',
    '13:00:00'
),
(
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('3cc2733f-4569-4686-bc79-6fe13ceba59c'),
    'SATURDAY',
    '17:00:00',
    '21:00:00'
);


-- ============================================================
-- Nueva Salud
-- Lun-Vie 09:30-14:00 / 17:00-20:00
-- Sáb     09:30-14:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('6a27cff6-898c-4485-b350-b421d3ee4813'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:30:00' AS TIME) AS opening_time,
        CAST('14:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('17:00:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('6a27cff6-898c-4485-b350-b421d3ee4813'),
    'SATURDAY',
    '09:30:00',
    '14:00:00'
);


-- ============================================================
-- Veterinaria Guau
-- Lun-Vie 09:00-12:30 / 16:30-20:30
-- Sáb     09:00-13:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('e76dccb8-4711-48e3-aaa9-047216eda0bf'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('12:30:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:30:00' AS TIME),
        CAST('20:30:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('e76dccb8-4711-48e3-aaa9-047216eda0bf'),
    'SATURDAY',
    '09:00:00',
    '13:00:00'
);


-- ============================================================
-- Veterinaria Como Reyes
-- Lun-Vie 09:00-12:30 / 16:30-20:00
-- Sáb     09:00-13:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('875b3651-18e9-4abd-a750-605764d0f019'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('12:30:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:30:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('875b3651-18e9-4abd-a750-605764d0f019'),
    'SATURDAY',
    '09:00:00',
    '13:00:00'
);


-- ============================================================
-- Bienestar Animal
-- Lun-Vie 09:30-21:00
-- Sáb     09:30-12:30
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('bf1a3e86-3047-46d5-ae86-086d04c7e153'),
    d.day_of_week,
    '09:30:00',
    '21:00:00'
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('bf1a3e86-3047-46d5-ae86-086d04c7e153'),
    'SATURDAY',
    '09:30:00',
    '12:30:00'
);


-- ============================================================
-- Dr. Seba
-- Lun-Vie 09:00-12:00 / 17:00-20:00
-- Sáb     09:00-12:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('f813624f-cff7-4024-afee-e2bde38884a7'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('12:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('17:00:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('f813624f-cff7-4024-afee-e2bde38884a7'),
    'SATURDAY',
    '09:00:00',
    '12:00:00'
);


-- ============================================================
-- Veterinaria Palermo
-- Lun-Vie 09:00-12:30 / 17:00-20:00
-- Sáb     09:30-12:30
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('b7c3b26c-dc4b-4364-9135-8b3da7a7c5a0'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('12:30:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('17:00:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('b7c3b26c-dc4b-4364-9135-8b3da7a7c5a0'),
    'SATURDAY',
    '09:30:00',
    '12:30:00'
);


-- ============================================================
-- Sol Veterinaria
-- Horarios desconocidos
-- ============================================================


-- ============================================================
-- Veterinaria San Francisco
-- Lun-Vie 08:00-12:00 / 16:00-19:00
-- Sáb     08:30-12:30
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('daf12986-a574-421e-804a-009eac92fc72'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('08:00:00' AS TIME) AS opening_time,
        CAST('12:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:00:00' AS TIME),
        CAST('19:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('daf12986-a574-421e-804a-009eac92fc72'),
    'SATURDAY',
    '08:30:00',
    '12:30:00'
);


-- ============================================================
-- Mi Ba-Bau
-- Lun-Vie 09:00-13:00 / 16:00-20:00
-- Sáb     09:00-13:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('a6250233-d88a-4550-b9f0-2e5fd0858b9b'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('13:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:00:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('a6250233-d88a-4550-b9f0-2e5fd0858b9b'),
    'SATURDAY',
    '09:00:00',
    '13:00:00'
);


-- ============================================================
-- Arca de Noe
-- Lun-Vie 08:30-12:30 / 16:00-19:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('13c55783-a501-4436-9b32-6e7eb9170300'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('08:30:00' AS TIME) AS opening_time,
        CAST('12:30:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:00:00' AS TIME),
        CAST('19:00:00' AS TIME)
) h;


-- ============================================================
-- Veterinaria Madala
-- Lun-Vie 07:00-13:00 / 15:30-18:30
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('160112d3-b018-474a-9c2b-dcc563becdc4'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('07:00:00' AS TIME) AS opening_time,
        CAST('13:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('15:30:00' AS TIME),
        CAST('18:30:00' AS TIME)
) h;


-- ============================================================
-- Veterinaria Belfo's
-- Lun-Vie 08:30-12:00 / 15:00-19:00
-- Sáb     09:30-12:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('aa04e40c-0d39-414c-b1f5-2efce58b7e62'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('08:30:00' AS TIME) AS opening_time,
        CAST('12:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('15:00:00' AS TIME),
        CAST('19:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('aa04e40c-0d39-414c-b1f5-2efce58b7e62'),
    'SATURDAY',
    '09:30:00',
    '12:00:00'
);


-- ============================================================
-- Veterinaria Esperanza
-- Lun-Vie 08:00-12:00 / 16:00-20:00
-- Sáb     09:00-12:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('8b8c9bf1-a312-4504-900f-6a5e11e8221f'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('08:00:00' AS TIME) AS opening_time,
        CAST('12:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:00:00' AS TIME),
        CAST('20:00:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('8b8c9bf1-a312-4504-900f-6a5e11e8221f'),
    'SATURDAY',
    '09:00:00',
    '12:00:00'
);


-- ============================================================
-- Amigo
-- Lun-Vie 09:00-12:00 / 16:30-19:30
-- Sáb     09:00-14:00
-- ============================================================

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
SELECT
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('4b65cea8-5e94-41fc-9435-32ef8986fb79'),
    d.day_of_week,
    h.opening_time,
    h.closing_time
FROM (
    SELECT 'MONDAY' AS day_of_week
    UNION ALL SELECT 'TUESDAY'
    UNION ALL SELECT 'WEDNESDAY'
    UNION ALL SELECT 'THURSDAY'
    UNION ALL SELECT 'FRIDAY'
) d
CROSS JOIN (
    SELECT
        CAST('09:00:00' AS TIME) AS opening_time,
        CAST('12:00:00' AS TIME) AS closing_time

    UNION ALL

    SELECT
        CAST('16:30:00' AS TIME),
        CAST('19:30:00' AS TIME)
) h;

INSERT INTO schedule (
    id,
    vet_id,
    day_of_week,
    opening_time,
    closing_time
)
VALUES (
    UUID_TO_BIN(UUID()),
    UUID_TO_BIN('4b65cea8-5e94-41fc-9435-32ef8986fb79'),
    'SATURDAY',
    '09:00:00',
    '14:00:00'
);