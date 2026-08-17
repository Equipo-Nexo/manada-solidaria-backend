-- ==========================
-- LOCATIONS
-- ==========================

INSERT INTO location (
    id,
    name,
    address,
    number,
    latitude,
    longitude
)
VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'Sede Animalia',
    'Av. Argentina',
    100,
    -32.4075,
    -63.2402
),
(
    '22222222-2222-2222-2222-222222222222',
    'Sede El Sol',
    'Av. Libertador',
    200,
    -32.4065,
    -63.2395
),
(
    '33333333-3333-3333-3333-333333333333',
    'Sede San Roque',
    'Av. España',
    300,
    -32.4055,
    -63.2385
);


-- ==========================
-- VET INFORMATION
-- ==========================

INSERT INTO vet_information (
    id,
    location_id,
    description,
    email,
    name,
    area_code,
    phone_number,
    profile_picture_url,
    vet_page_url
)
VALUES
(
    '44444444-4444-4444-4444-444444444444',
    '33333333-3333-3333-3333-333333333333',
    'Atención clínica, vacunación y cirugías.',
    'contacto@sanroque.com',
    'Veterinaria San Roque',
    '3514',
    '567890',
    'vet-profile-san-roque',
    'https://veterinariasanroque.com'
),
(
    '55555555-5555-5555-5555-555555555555',
    '22222222-2222-2222-2222-222222222222',
    'Atención veterinaria general.',
    'contacto@elsol.com',
    'Veterinaria El Sol',
    '3514',
    '567891',
    'vet-profile-el-sol',
    'https://veterinariaelsol.com'
),
(
    '66666666-6666-6666-6666-666666666666',
    '11111111-1111-1111-1111-111111111111',
    'Atención clínica y vacunación.',
    'contacto@animalia.com',
    'Veterinaria Animalia',
    '3514',
    '567892',
    'vet-profile-animalia',
    'https://veterinariaanimalia.com'
);


-- ==========================
-- SCHEDULES
-- ==========================

INSERT INTO schedule (
    id,
    closing_time,
    opening_time,
    vet_id,
    day_of_week
)
VALUES
(
    '77777777-7777-7777-7777-777777777777',
    '12:30:00',
    '08:00:00',
    '44444444-4444-4444-4444-444444444444',
    'MONDAY'
),
(
    '88888888-8888-8888-8888-888888888888',
    '18:00:00',
    '08:00:00',
    '44444444-4444-4444-4444-444444444444',
    'TUESDAY'
),
(
    '99999999-9999-9999-9999-999999999999',
    '18:00:00',
    '09:00:00',
    '55555555-5555-5555-5555-555555555555',
    'WEDNESDAY'
),
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '17:30:00',
    '08:30:00',
    '66666666-6666-6666-6666-666666666666',
    'THURSDAY'
);