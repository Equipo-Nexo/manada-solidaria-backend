-- Requiere /sql/animal_posts/get-animal-post.sql
-- 555... (perdido): like de admin y de test-user -> 2, reactedByMe = true
-- 999... (adopcion): like solo de test-user      -> 1, reactedByMe = false
-- 777... (en la calle): sin likes                 -> 0, reactedByMe = false

INSERT INTO reaction (id, created_at, type, owner_id, post_id)
VALUES
(
    '2deedcdd-48a3-4646-85db-08b154641a9b',
    CURRENT_TIMESTAMP,
    0,
    '22222222-2222-2222-2222-222222222222',
    '55555555-5555-5555-5555-555555555555'
),
(
    '0d3d220d-00a0-47c0-9a37-a50ba1fa7dee',
    CURRENT_TIMESTAMP,
    0,
    '33333333-3333-3333-3333-333333333333',
    '55555555-5555-5555-5555-555555555555'
),
(
    '98b0fb08-5d88-46a7-a9de-cb95d829de03',
    CURRENT_TIMESTAMP,
    0,
    '33333333-3333-3333-3333-333333333333',
    '99999999-9999-9999-9999-999999999999'
);
